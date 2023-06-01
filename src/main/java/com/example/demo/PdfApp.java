package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;


public class PdfApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PdfApp.class.getResource("pdfReader.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 800);
        PdfReaderController controller = fxmlLoader.getController();
        controller.initialize(null, null);
        stage.setTitle("Pdf Reader");
        stage.setResizable(true);
//        Image image = new Image("");
//        stage.getIcons().add(image);
//        stage.setMaximized(true);
      stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch();
    }

}