package paka;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("authorise.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("printerMain.fxml"));
        primaryStage.setTitle("Darbu aprite");
        primaryStage.setScene(new Scene(root, 350, 350));
        primaryStage.setResizable(false);
        primaryStage.setX(200);
        primaryStage.setY(20);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}