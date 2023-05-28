package com.example.demo;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PdfReaderController implements Initializable {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ToolBar toolBar;

    @FXML
    private ListView listView;


    @FXML
    private AnchorPane pdfPagesAnchorPane;

    @FXML
    private AnchorPane headerAnchorPane;

    @FXML
    private TextField pageNumberTextField;

    @FXML
    private MenuBar menuBar;

    @FXML
    private VBox rightSidebar;


    @FXML
    private void handleOpenAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Initialize the scrollPane object before calling renderPdf()
                scrollPane = new ScrollPane();
                renderPdf(selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleZoomInAction() {
        double currentZoom = scrollPane.getContent().getScaleX();
        scrollPane.getContent().setScaleX(currentZoom + 0.1);
        scrollPane.getContent().setScaleY(currentZoom + 0.1);
    }

    @FXML
    private void handleZoomOutAction() {
        double currentZoom = scrollPane.getContent().getScaleX();
        scrollPane.getContent().setScaleX(currentZoom - 0.1);
        scrollPane.getContent().setScaleY(currentZoom - 0.1);
    }

    private void renderPdf(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        double yPosition = 0.0;

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            PDPage page = document.getPage(i);
            BufferedImage image = pdfRenderer.renderImageWithDPI(i, 300);
            ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(pdfPagesAnchorPane.getWidth());
            imageView.setLayoutY(yPosition);
            pdfPagesAnchorPane.getChildren().add(imageView);
            yPosition += imageView.getBoundsInLocal().getHeight();
        }

        document.close();
    }

    private Image convertPdfPageToImage(PDFRenderer pdfRenderer, int pageIndex) throws IOException {
        // Adjust the DPI value as needed for the desired image quality
        final int dpi = 150;
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, dpi);
        return javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage,null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set the anchor constraints for the root node
        AnchorPane.setTopAnchor(anchorPane, 0.0);
        AnchorPane.setBottomAnchor(anchorPane, 0.0);
        AnchorPane.setLeftAnchor(anchorPane, 0.0);
        AnchorPane.setRightAnchor(anchorPane, 0.0);

        // Set the HBox constraints for the scrollPane content
        HBox.setHgrow(pdfPagesAnchorPane, Priority.ALWAYS);

        // Set the VBox constraints for the listView
        VBox.setVgrow(listView, Priority.ALWAYS);

        // Set the VBox constraints for the rightSidebar
        VBox.setVgrow(rightSidebar, Priority.ALWAYS);

        // Set the headerAnchorPane to stretch horizontally
        headerAnchorPane.setMaxWidth(Double.MAX_VALUE);

        // Set the pageNumberTextField to stretch horizontally
        pageNumberTextField.setMaxWidth(Double.MAX_VALUE);
    }
}
