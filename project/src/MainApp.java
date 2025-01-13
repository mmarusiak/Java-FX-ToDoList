import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.ListNode;
import logic.TitleNode;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println("Hello world");
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/MainView.fxml"));
        Parent root = loader.load();
        NodeManager nodeManager = loader.getController();


        // Set up the scene
        Scene scene = new Scene(root, 800, 600);

        // Configure the stage
        primaryStage.setTitle("To Do app");
        primaryStage.setScene(scene);
        primaryStage.show();

        nodeManager.initializeScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
