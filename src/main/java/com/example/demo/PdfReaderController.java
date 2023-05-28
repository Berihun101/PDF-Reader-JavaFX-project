package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfReaderController {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private void handleOpenAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
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
        int numPages = document.getNumberOfPages();

        for (int pageIndex = 0; pageIndex < numPages; pageIndex++) {
            javafx.scene.image.Image fxImage = convertPdfPageToImage(pdfRenderer, pageIndex);
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(fxImage);
            scrollPane.setContent(imageView);
        }

        document.close();
    }

    private Image convertPdfPageToImage(PDFRenderer pdfRenderer, int pageIndex) throws IOException {
        // Adjust the DPI value as needed for the desired image quality
        final int dpi = 150;
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, dpi);
        return javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
