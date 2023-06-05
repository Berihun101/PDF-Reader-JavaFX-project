package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
<<<<<<< HEAD
import javafx.geometry.Rectangle2D;
=======
>>>>>>> origin/main
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.swing.tree.DefaultTreeModel;
import java.io.IOException;
import java.util.Objects;


public class PdfApp extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PdfApp.class.getResource("pdfReader.fxml"));
<<<<<<< HEAD
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

=======
         scene = new Scene(fxmlLoader.load(), 900, 800);
>>>>>>> origin/main
        PdfReaderController controller = fxmlLoader.getController();
        controller.initialize(null, null);

        stage.setTitle("Pdf Reader");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void sceneFactory(String fxml) throws IOException{
        scene.setRoot(loadFXML(fxml));
        scene.getWindow().sizeToScene();
    }
    private static Parent loadFXML(String fxml) throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(PdfApp.class.getResource(fxml + ".fxml")));
    }

}