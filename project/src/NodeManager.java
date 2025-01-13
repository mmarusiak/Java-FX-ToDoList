import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
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
    private TextArea newTitleArea, newDescriptionArea;

    public void initializeScene() throws IOException {

        initializeElementsFromTitleNode(TitleNode.highPriorityTasks);
        initializeElementsFromTitleNode(TitleNode.mediumPriorityTasks);
        initializeElementsFromTitleNode(TitleNode.lowPriorityTasks);
        initializeElementsFromTitleNode(TitleNode.unassignedTasks);
        initializeElementsFromTitleNode(TitleNode.doneTasks);
    }

    public void clearScene(){
        toDoSection.getChildren().clear();
    }

    public void addTaskToSection(ListNode parent, TaskNode targetNode) throws IOException {
        parent.addChild(targetNode);
        System.out.println(parent.getNodeName() + parent.hashCode());
        VBox parentBox = (VBox) toDoSection.lookup("#" + parent.getId());
        parentBox.getChildren().add(createFXNode(targetNode, false));
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

        HBox hBox = (HBox) baseElement.getChildren().getFirst();
        Text title = (Text) hBox.lookup("#nodeTitle");
        Text description = (Text) hBox.lookup("#nodeDescription");
        CheckBox checkBox = (CheckBox) hBox.lookup("#checkBox");

        setUpDragAndDrop(baseElement, toDoSection);

        if(node instanceof TitleNode){
            title.setStyle("-fx-font-weight: bold;");
            hBox.getChildren().remove(checkBox);
        }

        title.setText(node.getNodeName());
        description.setText(node.getNodeDescription());

        return baseElement;
    }

    public void createNewTask() throws IOException {
        String title = newTitleArea.getText();
        String description = newDescriptionArea.getText();
        TaskNode newTask = new TaskNode(title, description, TitleNode.unassignedTasks);

        addTaskToSection(TitleNode.unassignedTasks, newTask);
    }

    private void setUpDragAndDrop(VBox sourceContainer, VBox targetContainer) {
        for (var node : sourceContainer.getChildren()) {
            if (!(node instanceof HBox)) continue;
            // Enable dragging for each HBox
            node.setOnDragDetected(event -> {
                Dragboard dragboard = node.getParent().startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("NodeBox");
                dragboard.setContent(content);
                event.consume();
            });
        }

        targetContainer.setOnDragOver(event -> {
            if (event.getGestureSource() instanceof VBox && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        targetContainer.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString() && event.getGestureSource() instanceof NodeBox draggedBox) {

                // Remove VBox from its current parent
                ((VBox) draggedBox.getParent()).getChildren().remove(draggedBox);

                // Determine where to insert the dragged NodeBox
                int dropIndex = calculateDropIndex(targetContainer, event.getSceneY());

                // Add TitleBox to the target container at the correct position
                if(draggedBox.isTitleNode()) targetContainer.getChildren().add(dropIndex, draggedBox);
                else{
                    // detect where we should drop our node box... to which component???
                    // i want to be able to drop task to:
                    // to other tasks, then it becomes subtasks!
                    // to other titles!
                }
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    private int calculateDropIndex(VBox targetContainer, double dropY) {
        for (int i = 0; i < targetContainer.getChildren().size(); i++) {
            Bounds bounds = targetContainer.getChildren().get(i).localToScene(targetContainer.getChildren().get(i).getBoundsInLocal());
            if (dropY < bounds.getMinY() + bounds.getHeight() / 2) {
                return i;
            }
        }
        return targetContainer.getChildren().size(); // Drop at the end if no match
    }
}