package com.example.demo;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class PdfReaderController implements Initializable {

    private PDDocument document;
    private PDFRenderer pdfRenderer;

    private VBox pdfPagesContainer;
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

    private Stage stage;

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
    private void handleCloseAction() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Are you sure you want to close the PDF?");
        confirmation.setContentText("Press OK to close or Cancel to stay.");
        ButtonType okButton = ButtonType.OK;
        ButtonType cancelButton = ButtonType.CANCEL;
        confirmation.getButtonTypes().setAll(okButton, cancelButton);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                pdfPagesAnchorPane.getChildren().clear();
                scrollPane.setContent(null);
                scrollPane = null;
            }
        });
    }




    @FXML
    private void handleExitAction() {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Are you sure you want to exit?");
        confirmation.setContentText("Press OK to exit or Cancel to stay.");
        ButtonType okButton = ButtonType.OK;
        ButtonType cancelButton = ButtonType.CANCEL;
        confirmation.getButtonTypes().setAll(okButton, cancelButton);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                Platform.exit();
            }
        });
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

        listView.getItems().clear();
        pageNumberTextField.setText("1");

        pdfPagesAnchorPane.getChildren().clear();
        scrollPane.setVvalue(0.0);

        VBox pdfPagesContainer = new VBox();
        pdfPagesContainer.setFillWidth(true);

        // Set margin for the VBox (centering it vertically)
        Insets vboxMargin = new Insets(20, 0, 20, 0);
        VBox.setMargin(pdfPagesContainer, vboxMargin);

        // Bind the VBox height to the ScrollPane height
        pdfPagesContainer.prefHeightProperty().bind(scrollPane.heightProperty());

        // Add the VBox to an AnchorPane
        AnchorPane anchorPane = new AnchorPane(pdfPagesContainer);
        pdfPagesAnchorPane.getChildren().add(anchorPane);

        // Update the VBox width when the ScrollPane width changes
        scrollPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double scrollPaneWidth = newValue.doubleValue();
            double vboxWidth = scrollPaneWidth - vboxMargin.getLeft() - vboxMargin.getRight();
            pdfPagesContainer.setPrefWidth(vboxWidth);
        });

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            PDPage page = document.getPage(i);
            BufferedImage image = pdfRenderer.renderImageWithDPI(i, 96);
            ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));
            imageView.setPreserveRatio(true);

            StackPane imageContainer = new StackPane(imageView);
            imageContainer.setStyle("-fx-background-color: white");

            pdfPagesContainer.getChildren().add(imageContainer);
            VBox.setMargin(imageContainer, new Insets(10));
        }

        // Center the VBox within the ScrollPane vertically
        VBox.setVgrow(pdfPagesContainer, Priority.ALWAYS);

        scrollPane.setContent(pdfPagesAnchorPane);

        // Update the ListView with page numbers
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            listView.getItems().add(Integer.toString(i));
        }

        // Set the event handler for ListView selection
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int selectedIndex = listView.getSelectionModel().getSelectedIndex();
                scrollPane.setVvalue((double) selectedIndex / document.getNumberOfPages());
                updatePageNumberText(selectedIndex + 1, document.getNumberOfPages());
            }
        });

        // Add listener to scrollPane vvalue property to update pageNumberTextField
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            int currentPage = (int) Math.ceil(newValue.doubleValue() * document.getNumberOfPages());
            updatePageNumberText(currentPage, document.getNumberOfPages());
        });
    }

    private void updatePageNumberText(int currentPage, int totalPages) {
        pageNumberTextField.setText(currentPage + " / " + totalPages);
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
