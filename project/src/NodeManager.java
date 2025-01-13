import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import logic.ListNode;
import logic.TaskNode;
import logic.TitleNode;

import java.io.IOException;

public class NodeManager {
    @FXML
    private VBox toDoSection;

    public void updateScene() throws IOException {

        toDoSection.getChildren().removeAll();

        addElementsFromTitleNode(TitleNode.doneTasks);
        addElementsFromTitleNode(TitleNode.highPriorityTasks);
        addElementsFromTitleNode(TitleNode.lowPriorityTasks);
    }
    private <T extends ListNode> void addElementsFromTitleNode(T parentNode) throws IOException {

        VBox mySection = new VBox();
        addElementsFromTitleNode(parentNode, mySection);
        toDoSection.getChildren().add(mySection);
    }


    private <T extends ListNode> void addElementsFromTitleNode(T parentNode, VBox container) throws IOException{

        System.out.println(parentNode.getNodeName());

        FXMLLoader baseNodeLoader = new FXMLLoader(getClass().getResource("resources/TaskNode.fxml"));

        VBox myContainer = new VBox();
        HBox baseElement = baseNodeLoader.load();

        // Access the components inside the loaded base element
        Label dynamicLabel = (Label) baseElement.lookup("#nodeTitle");
        Button dynamicButton = (Button) baseElement.lookup("#nodeButton");

        // Customize the label and button
        dynamicLabel.setText(parentNode.getNodeName());
        dynamicButton.setOnAction(e ->{
            System.out.println(parentNode.getNodeDescription() + " clicked!");
            parentNode.addChild(new TaskNode("Example", "Lorem lorem", parentNode));
            try {
                updateScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        myContainer.getChildren().add(baseElement);
        if(!parentNode.getChildren().isEmpty()) {
            for (var child : parentNode.getChildren()) {
                addElementsFromTitleNode(child, myContainer);
            }
        }
        // Add the base element to the container
        container.getChildren().add(myContainer);
    }
}
