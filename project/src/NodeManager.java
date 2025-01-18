import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import logic.ListNode;
import logic.NodeBox;
import logic.TaskNode;
import logic.TitleNode;
import java.io.IOException;
import java.util.Objects;

public class NodeManager {
    @FXML
    private VBox toDoSection;
    @FXML
    private VBox mainApp;
    @FXML
    private TextField nodeTextField;
    @FXML
    private Text nodeTextHelper;

    public void initializeScene() throws IOException {

        initializeElementsFromTitleNode(TitleNode.highPriorityTasks);
        initializeElementsFromTitleNode(TitleNode.mediumPriorityTasks);
        initializeElementsFromTitleNode(TitleNode.lowPriorityTasks);
        initializeElementsFromTitleNode(TitleNode.unassignedTasks);
        initializeElementsFromTitleNode(TitleNode.doneTasks);
        nodeTextField.setOnKeyPressed(event -> {
            String keyCode = event.getCode().toString();
            if(keyCode.equals("ENTER")) {
                try {
                    createNewNode();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(keyCode.equals("ESCAPE"))
            {
                done = false;
                changeNewNodeTexts();
            }
        });
    }

    // user input for creating new task
    private String title, description;
    private boolean done = false;
    public void createNewNode() throws IOException {
        if(done) {
            description = nodeTextField.getText();
            TaskNode newTask = new TaskNode(title, description, TitleNode.unassignedTasks);
            addTaskToSection(TitleNode.unassignedTasks, newTask);
        }
        else title = nodeTextField.getText();

        done = !done;
        changeNewNodeTexts();
    }

    private void changeNewNodeTexts(){
        nodeTextField.clear();

        String newText = !done ? "Title for new task" : "Description for new task";
        nodeTextField.setPromptText(newText);
        nodeTextHelper.setText(newText);
    }

    public void clearScene(){
        toDoSection.getChildren().clear();
    }

    public void addTaskToSection(ListNode parent, TaskNode targetNode) throws IOException {
        parent.addChildQuietly(targetNode);
        VBox parentBox = (VBox) toDoSection.lookup("#" + parent.getId());
        addNodeToParentNode(createFXNode(targetNode, false), parentBox, 0);
    }

    public void addNodeToParentNode(NodeBox child, Pane targetParent, double targetY){


        if((child.getParent()) != null) ((Pane)child.getParent()).getChildren().remove(child);

        // Determine where to insert the dragged NodeBox
        int dropIndex = calculateDropIndex(targetParent, targetY);

        // Add TitleBox to the target container at the correct position
        if(child.isTitleNode()) targetParent.getChildren().add(dropIndex, child);
        else{
            Pane actualTarget = getTargetContainer(child, targetParent, targetY);
            dropIndex = calculateDropIndex(actualTarget, targetY);

            addTaskToPane(child, actualTarget, dropIndex);

            ListNode parent = ((NodeBox) actualTarget).getMyNode();
            TaskNode taskNode = (TaskNode) child.getMyNode();
            taskNode.setParentQuietly(parent);
        }
    }

    private void addTaskToPane(NodeBox child, Pane targetParent, int dropIndex){
        targetParent.getChildren().add(Math.max(dropIndex, 1), child);
        double width = (double) 300 - 30 * (getDepthOfNode(child) - 6);
        child.setTranslateX(10);
        if (child.getWidth() != width) {
            child.setStyle("-fx-max-width: " + width + ";-fx-min-width: " + width + ";");
            recalculateChildren(child);
        }
    }

    private void recalculateChildren(Pane parent){
        for(Node child : parent.getChildren()){
            if(child instanceof NodeBox nodeBox){
                double width = ((NodeBox) child).getWidth() - 30;
                child.setStyle("-fx-max-width: " + width + ";-fx-min-width: " + width + ";");
                recalculateChildren(nodeBox);
            }
        }
    }

    private <T extends ListNode> void initializeElementsFromTitleNode(T parentNode) throws IOException {

        toDoSection.getChildren().add(initializeElementsFromTitleNode(parentNode, null));
    }


    private <T extends ListNode> NodeBox initializeElementsFromTitleNode(T parentNode, VBox container) throws IOException{

        NodeBox baseElement = createFXNode(parentNode, true);

        if(!parentNode.getChildren().isEmpty()) {
            for (var child : parentNode.getChildren()) {
                initializeElementsFromTitleNode(child, baseElement);
            }
            System.out.println("not empty!");// nothing happens here
        }
        // Add the base element to the container
        if(container != null) container.getChildren().add(baseElement);
        return baseElement;
    }

    private NodeBox createFXNode(ListNode node, boolean isTitleNode) throws IOException {

        FXMLLoader baseNodeLoader = new FXMLLoader(getClass().getResource("resources/fxml/TaskNode.fxml"));

        NodeBox baseElement = baseNodeLoader.load();
        baseElement.setId(node.getId());
        baseElement.setTitleNode(isTitleNode);
        baseElement.setMyNode(node);

        HBox hBox = (HBox) baseElement.getChildren().getFirst();
        Text title = (Text) hBox.lookup("#nodeTitle");
        Text description = (Text) hBox.lookup("#nodeDescription");
        Text nodeDone = (Text) hBox.lookup("#nodeDone");
        CheckBox checkBox = (CheckBox) hBox.lookup("#checkBox");
        Region spacer = (Region) hBox.lookup("#spacer");
        Button deleteNodeButton = (Button) hBox.lookup("#deleteNodeButton");

        HBox.setHgrow(spacer, Priority.ALWAYS);

        setUpDragAndDrop(baseElement, toDoSection);

        if (node instanceof TitleNode titleNode) {
            hBox.getStyleClass().add("titleNode");
            title.setStyle("-fx-font-weight: bold;");
            hBox.getChildren().remove(checkBox);
            nodeDone.setVisible(false);
            titleNode.addPropertyChangeListener(evt -> {
                TitleNode targetNode = (TitleNode) evt.getOldValue();
                TaskNode newValue = (TaskNode) evt.getNewValue();

                System.out.println(newValue.getId());
                System.out.println(targetNode.getId());

                Pane pNode = (Pane) toDoSection.lookup("#" + targetNode.getId());
                NodeBox cNode = (NodeBox) toDoSection.lookup("#" + newValue.getId());

                if(Objects.equals(evt.getPropertyName(), "child_added")) addTaskToPane(cNode, pNode, 0);
                else if(Objects.equals(evt.getPropertyName(), "child_removed")) pNode.getChildren().remove(cNode);
            });

            hBox.setStyle("-fx-background-color: " + titleNode.getBackground() + ";");
        } else if (node instanceof TaskNode taskNode) {
            hBox.getStyleClass().add("taskNode");
            nodeDone.setText((int) taskNode.getState() * 100 + "%");
            taskNode.addPropertyChangeListener(evt -> {
                if(evt.getPropertyName().equals("state")) {
                    float newVal = (float) evt.getNewValue();
                    nodeDone.setText(Math.round(newVal * 100f) + "%");
                    checkBox.selectedProperty().setValue(newVal == 1);
                    title.setStyle("-fx-strikethrough: " + (newVal == 1 ? "true" : "false") + ";");
                }
                else if (evt.getPropertyName().equals("child_removed")) {
                    TaskNode child = (TaskNode) evt.getNewValue();
                    baseElement.getChildren().remove(baseElement.lookup("#" + child.getId()));
                }
            });
            Boolean state = taskNode.getState() == 1;
            checkBox.setOnMouseClicked(event -> {
                boolean newValue = checkBox.selectedProperty().getValue();
                taskNode.setState(newValue ? 1 : 0, !taskNode.getChildren().isEmpty());
            });
            deleteNodeButton.setOnMouseClicked(event -> {
                taskNode.deleteTask();
            });
        }

        title.setText(node.getNodeName());
        description.setText(node.getNodeDescription());
        if (description.getText().isEmpty()) description.managedProperty().set(false);
        else description.managedProperty().bind(description.visibleProperty());
        nodeDone.managedProperty().bind(nodeDone.visibleProperty());
        deleteNodeButton.managedProperty().bind(deleteNodeButton.visibleProperty());

        return baseElement;
    }

    private void setUpDragAndDrop(VBox sourceContainer, VBox targetContainer) {

        // Enable dragging for each HBox
        sourceContainer.setOnDragDetected(event -> {
            Dragboard dragboard = sourceContainer.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("NodeBox");
            dragboard.setContent(content);
            event.consume();
        });

        targetContainer.setOnDragOver(event -> {
            if (event.getGestureSource() instanceof VBox && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        targetContainer.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString() && event.getGestureSource() instanceof NodeBox draggedBox) {
                addNodeToParentNode(draggedBox, targetContainer, event.getSceneY());
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }


    private Pane getTargetContainer(Pane source, Pane currentTarget, double sceneY) {

        if(currentTarget.getChildren().size() == 1) return currentTarget;

        for (var node : currentTarget.getChildren()) {
            if(!checkIfWeAreOnPane((Pane)node, sceneY) || node == source) continue;
            if(node instanceof HBox hBox) return currentTarget;
            return getTargetContainer(source, (Pane) node, sceneY);
        }

        return currentTarget;
    }

    private int getDepthOfNode(Node node){
        int d = 0;
        while(node.getParent() != null){
            d ++;
            node = node.getParent();
        }

        return d;
    }

    private boolean checkIfWeAreOnPane(Node node, double y) {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());

        return y > bounds.getMinY() && y < bounds.getMaxY();
    }

    private int calculateDropIndex(Pane targetContainer, double dropY) {
        for (int i = 0; i < targetContainer.getChildren().size(); i++) {
            Bounds bounds = targetContainer.getChildren().get(i).localToScene(targetContainer.getChildren().get(i).getBoundsInLocal());
            if (dropY < bounds.getMinY() + bounds.getHeight() / 2) {
                return i;
            }
        }
        return targetContainer.getChildren().size(); // Drop at the end if no match
    }
}