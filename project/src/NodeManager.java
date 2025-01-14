import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import logic.ListNode;
import logic.NodeBox;
import logic.TaskNode;
import logic.TitleNode;

import java.io.IOException;

public class NodeManager {
    @FXML
    private VBox toDoSection;
    @FXML
    private HBox mainApp;
    @FXML
    private TextField newTitleArea, newDescriptionArea;

    public void initializeScene() throws IOException {

        initializeElementsFromTitleNode(TitleNode.highPriorityTasks);
        initializeElementsFromTitleNode(TitleNode.mediumPriorityTasks);
        initializeElementsFromTitleNode(TitleNode.lowPriorityTasks);
        initializeElementsFromTitleNode(TitleNode.unassignedTasks);
        initializeElementsFromTitleNode(TitleNode.doneTasks);

        //newTitleArea.setWrapText(false);
        //newDescriptionArea.setWrapText(false);
        //newTitleArea.setPrefWidth(80);
        //newTitleArea.setMaxWidth(80);
    }

    public void clearScene(){
        toDoSection.getChildren().clear();
    }

    public void addTaskToSection(ListNode parent, TaskNode targetNode) throws IOException {
        parent.addChild(targetNode);
        System.out.println(parent.getNodeName() + parent.hashCode());
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
            actualTarget.getChildren().add(Math.max(dropIndex, 1), child);

            ListNode parent = ((NodeBox) actualTarget).getMyNode();
            TaskNode taskNode = (TaskNode) child.getMyNode();
            taskNode.setParent(parent);

            double width = (double) 400 - 30 * (getDepthOfNode(child) - 6);
            child.setStyle("-fx-max-width: " + width + ";-fx-min-width: " + width + ";");
            System.out.println(child.getStyle());
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

        setUpDragAndDrop(baseElement, toDoSection);

        if(node instanceof TitleNode){
            title.setStyle("-fx-font-weight: bold;");
            hBox.getChildren().remove(checkBox);
            nodeDone.setVisible(false);
        }else if(node instanceof TaskNode taskNode){
            hBox.getStyleClass().add("taskNode");
            nodeDone.setText((int)taskNode.getState() * 100 + "%");
            taskNode.addPropertyChangeListener(evt -> {
                nodeDone.setText(Math.round((float)evt.getNewValue() * 100f) + "%");
            });
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                taskNode.setState(newValue ? 1f : 0f);
            });
        }

        title.setText(node.getNodeName());
        description.setText(node.getNodeDescription());
        description.managedProperty().bind(description.visibleProperty());
        nodeDone.managedProperty().bind(nodeDone.visibleProperty());

        return baseElement;
    }

    public void createNewTask() throws IOException {
        String title = newTitleArea.getText();
        String description = newDescriptionArea.getText();
        TaskNode newTask = new TaskNode(title, description, TitleNode.unassignedTasks);

        addTaskToSection(TitleNode.unassignedTasks, newTask);
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