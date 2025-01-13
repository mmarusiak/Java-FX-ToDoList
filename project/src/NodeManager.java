import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import logic.ListNode;
import logic.TaskNode;
import logic.TitleNode;

import java.io.IOException;

public class NodeManager {
    @FXML
    private VBox toDoSection;

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

    public void updateScene(ListNode parent, TaskNode targetNode) throws IOException {
        parent.addChild(targetNode);
        System.out.println(parent.getNodeName() + parent.hashCode());
        VBox parentBox = (VBox) toDoSection.lookup("#" + parent.getId());
        parentBox.getChildren().add(createFXNode(targetNode));
    }

    private <T extends ListNode> void initializeElementsFromTitleNode(T parentNode) throws IOException {

        VBox mySection = new VBox();
        initializeElementsFromTitleNode(parentNode, mySection);
        toDoSection.getChildren().add(mySection);
    }


    private <T extends ListNode> void initializeElementsFromTitleNode(T parentNode, VBox container) throws IOException{

        VBox baseElement = createFXNode(parentNode);

        if(!parentNode.getChildren().isEmpty()) {
            for (var child : parentNode.getChildren()) {
                initializeElementsFromTitleNode(child, baseElement);
            }
        }
        // Add the base element to the container
        container.getChildren().add(baseElement);
    }

    private VBox createFXNode(ListNode node) throws IOException {

        FXMLLoader baseNodeLoader = new FXMLLoader(getClass().getResource("resources/fxml/TaskNode.fxml"));

        VBox baseElement = baseNodeLoader.load();
        baseElement.setId(node.getId());

        HBox hBox = (HBox) baseElement.getChildren().getFirst();
        Text title = (Text) hBox.lookup("#nodeTitle");
        Text description = (Text) hBox.lookup("#nodeDescription");
        CheckBox checkBox = (CheckBox) hBox.lookup("#checkBox");

        if(node instanceof TitleNode){
            title.setStyle("-fx-font-weight: bold;");
            hBox.getChildren().remove(checkBox);
        }

        title.setText(node.getNodeName());
        description.setText(node.getNodeDescription());

        return baseElement;
    }

    public void createNewTask(){
        System.out.println("Creating new task");
    }
}
