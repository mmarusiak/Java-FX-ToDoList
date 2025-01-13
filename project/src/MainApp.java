import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println("Hello world");
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/MainView.fxml"));
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
