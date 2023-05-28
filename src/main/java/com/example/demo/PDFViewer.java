package com.example.demo;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;


import java.io.File;
import java.io.IOException;

public class PDFViewer {
    public static void display(File file, AnchorPane pdfPane, ScrollPane scrollPane) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFRenderer renderer = new PDFRenderer(document);

        // Calculate the scale factor based on the screen DPI
        double dpi = Screen.getPrimary().getDpi();
        double scale = dpi / 72.0;

        // Create an ImageView for each page of the PDF document
        VBox pages = new VBox();
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            Image image = new Image(String.valueOf(renderer.renderImage(i, (float) scale)));
            ImageView imageView = new ImageView(image);
            pages.getChildren().add(imageView);
        }

        // Add the pages VBox to a StackPane to center it in the AnchorPane
        StackPane stackPane = new StackPane(pages);
        stackPane.setPrefHeight(document.getPage(0).getMediaBox().getHeight() * scale);
        stackPane.setPrefWidth(document.getPage(0).getMediaBox().getWidth() * scale);
        pdfPane.getChildren().add(stackPane);

        // Set the content of the scroll pane to the StackPane
        scrollPane.setContent(stackPane);

        // Add a Scale transform to the StackPane to enable zooming
        Scale scaleTransform = new Scale(1, 1);
        stackPane.getTransforms().add(scaleTransform);

        // Add event handlers to the scroll pane to handle zooming
        scrollPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double scaleFactor = (deltaY > 0) ? 1.1 : 1 / 1.1;
            scaleTransform.setX(scaleTransform.getX() * scaleFactor);
            scaleTransform.setY(scaleTransform.getY() * scaleFactor);
            event.consume();
        });

        scrollPane.setOnZoom(event -> {
            double zoomFactor = event.getZoomFactor();
            scaleTransform.setX(scaleTransform.getX() * zoomFactor);
            scaleTransform.setY(scaleTransform.getY() * zoomFactor);
            event.consume();
        });

        // Center the StackPane in the AnchorPane
        AnchorPane.setTopAnchor(stackPane, (pdfPane.getHeight() - stackPane.getPrefHeight()) / 2);
        AnchorPane.setBottomAnchor(stackPane, (pdfPane.getHeight() - stackPane.getPrefHeight()) / 2);
        AnchorPane.setLeftAnchor(stackPane, (pdfPane.getWidth() - stackPane.getPrefWidth()) / 2);
        AnchorPane.setRightAnchor(stackPane, (pdfPane.getWidth() - stackPane.getPrefWidth()) / 2);
    }
    public static void zoomIn(AnchorPane pdfPane) {
        // Get the first child of the StackPane (the VBox of pages)
        StackPane stackPane = (StackPane) pdfPane.getChildren().get(0);
        VBox pages = (VBox) stackPane.getChildren().get(0);

        // Increase the scale of the pages by 10%
        double scaleFactor = pages.getScaleX() * 1.1;
        pages.setScaleX(scaleFactor);
        pages.setScaleY(scaleFactor);
    }
    public static void zoomOut(AnchorPane pdfPane) {
        // Get the first child of the StackPane (the VBox of pages)
        StackPane stackPane = (StackPane) pdfPane.getChildren().get(0);
        VBox pages = (VBox) stackPane.getChildren().get(0);

        // Decrease the scale of the pages by 10%
        double scaleFactor = pages.getScaleX() / 1.1;
        pages.setScaleX(scaleFactor);
        pages.setScaleY(scaleFactor);
    }
}
