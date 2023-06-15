import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;

import static com.example.demo.PdfApp.sceneFactory;


public class PdfReaderController implements Initializable {

    private PDDocument document;
    private PDFRenderer pdfRenderer;

    private VBox pdfPagesContainer;
    @FXML
    private ScrollPane scrollPane;

     @FXML
private ListView<String> recentFilesListView;
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
    private Button eyebtn;

    @FXML
    private Button penbtn;
    @FXML
    private Button darkbtn;

    @FXML
    private Button zinbtn;

    @FXML
    private Button zoutbtn;


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
            // Save the recently opened file
            saveRecentFile(selectedFile);
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
    private void handleFullScreenAction() {
        Stage currentStage = (Stage) anchorPane.getScene().getWindow();

        if (currentStage.isFullScreen()) {
            currentStage.setFullScreen(false);
            currentStage.setMaximized(false);
            currentStage.setHeight(600); // Set the desired height when exiting fullscreen
            currentStage.setWidth(800); // Set the desired width when exiting fullscreen
        } else {
            currentStage.setFullScreen(true);
            currentStage.setMaximized(true);
        }
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
    private void handleRecentFileClick(MouseEvent event) throws IOException{
    if (event.getClickCount() == 2) { // Check if it's a double-click
        String selectedFilePath = recentFilesListView.getSelectionModel().getSelectedItem();
        if (selectedFilePath != null) {
            File selectedFile = new File(selectedFilePath);
            if (selectedFile.exists()) {
                renderPdf(selectedFile); // Call the existing method to render the PDF
            } else {
                // Show an alert if the file does not exist
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File not found");
                alert.setHeaderText("The selected file could not be found.");
                alert.setContentText("Please check if the file has been moved or deleted.");
                alert.showAndWait();
            }
        }
    }
}

    @FXML
    private void handleAboutAction() throws IOException {
        PdfApp.sceneFactory("/com/example/demo/about");
    }

    @FXML
    private void handleDarkModeAction() {
        VBox pdfPagesContainer = (VBox) pdfPagesAnchorPane.getChildren().get(0);

        if (darkbtn.getStyleClass().contains("dark-mode")) {
            // Switch to light mode
            darkbtn.getStyleClass().remove("dark-mode");
            pdfPagesContainer.setStyle("-fx-background-color: white;");
            headerAnchorPane.setStyle("-fx-background-color: #f4f4f4;");
            rightSidebar.setStyle("-fx-background-color: #a4bc92;");
            scrollPane.setStyle("-fx-background-color: white;");
        } else {
            // Switch to dark mode
            darkbtn.getStyleClass().add("dark-mode");
            pdfPagesContainer.setStyle("-fx-background-color: #1c1c1c;");
            headerAnchorPane.setStyle("-fx-background-color: #252525;");
            rightSidebar.setStyle("-fx-background-color: #252525;");
            scrollPane.setStyle("-fx-background-color: #1c1c1c;");

        }
    }

    @FXML
    private void applyBlueLightFilter() {
        VBox pdfPagesContainer = (VBox) pdfPagesAnchorPane.getChildren().get(0);

        pdfPagesContainer.getChildren().forEach(node -> {
            ImageView imageView = (ImageView) node;
            Image image = imageView.getImage();

            PixelReader pixelReader = image.getPixelReader();
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            WritableImage filteredImage = new WritableImage(width, height);
            PixelWriter pixelWriter = filteredImage.getPixelWriter();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = pixelReader.getColor(x, y);

                    double red = ((Color) color).getRed();
                    double green = color.getGreen();
                    double blue = color.getBlue() * 0.8;

                    pixelWriter.setColor(x, y, new Color(red, green, blue, color.getOpacity()));
                }
            }

            // Update the ImageView with the filtered image
            imageView.setImage(filteredImage);
        });
    }

    @FXML
    private void handleZoomInAction() {
        VBox pdfPagesContainer = (VBox) pdfPagesAnchorPane.getChildren().get(0);
        pdfPagesContainer.setScaleX(pdfPagesContainer.getScaleX() * 1.1);
        pdfPagesContainer.setScaleY(pdfPagesContainer.getScaleY() * 1.1);
    }


    @FXML
    private void handleZoomOutAction() {
        VBox pdfPagesContainer = (VBox) pdfPagesAnchorPane.getChildren().get(0);
        pdfPagesContainer.setScaleX(pdfPagesContainer.getScaleX() / 1.1);
        pdfPagesContainer.setScaleY(pdfPagesContainer.getScaleY() / 1.1);
    }


    private void renderPdf(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(document);


        pageNumberTextField.setText("1");

        pdfPagesAnchorPane.getChildren().clear();
        scrollPane.setVvalue(0.0);

        scrollPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            fitPdfToScrollPane();
        });
        scrollPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            fitPdfToScrollPane();
        });

        VBox pdfPagesContainer = new VBox();
        pdfPagesContainer.setFillWidth(true);

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            PDPage page = document.getPage(i);
            BufferedImage image = pdfRenderer.renderImageWithDPI(i, 96);
            ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(pdfPagesAnchorPane.widthProperty()); // Bind width to anchor pane

            pdfPagesContainer.getChildren().add(imageView);
            VBox.setMargin(imageView, new Insets(10));
        }

        pdfPagesAnchorPane.getChildren().add(pdfPagesContainer);
        scrollPane.setContent(pdfPagesAnchorPane);

        fitPdfToScrollPane(); // Fit the PDF to the scrollpane initially




    }
    private void loadRecentFiles() {
    List<String> recentFiles = new ArrayList<>();
    Path recentFilesPath = Paths.get("recent_files.txt");

    // Read existing file paths
    try (BufferedReader reader = Files.newBufferedReader(recentFilesPath)) {
        String line;
        while ((line = reader.readLine()) != null) {
            recentFiles.add(line);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Update the ListView with the recent files (up to 4)
    recentFilesListView.getItems().clear();
    for (int i = 0; i < Math.min(4, recentFiles.size()); i++) {
        recentFilesListView.getItems().add(recentFiles.get(i));
    }
}


    private void saveRecentFile(File file) {
    List<String> recentFiles = new ArrayList<>();
    Path recentFilesPath = Paths.get("recent_files.txt");

    // Read existing file paths
    try (BufferedReader reader = Files.newBufferedReader(recentFilesPath)) {
        String line;
        while ((line = reader.readLine()) != null) {
            recentFiles.add(line);
        }
    } catch (IOException e) {
        // If the file doesn't exist, create it
        if (!Files.exists(recentFilesPath)) {
            try {
                Files.createFile(recentFilesPath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Update the list with the new file path
    recentFiles.remove(file.getAbsolutePath());
    recentFiles.add(0, file.getAbsolutePath());

    // Write the updated list back to the file
    try (BufferedWriter writer = Files.newBufferedWriter(recentFilesPath)) {
        for (String filePath : recentFiles) {
            writer.write(filePath);
            writer.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Update the ListView with the new recent files
    loadRecentFiles();
}

    private void fitPdfToScrollPane() {
        double scrollPaneWidth = scrollPane.getWidth();
        double scrollPaneHeight = scrollPane.getHeight();

        pdfPagesAnchorPane.setPrefWidth(scrollPaneWidth);
        pdfPagesAnchorPane.setPrefHeight(scrollPaneHeight);

        VBox pdfPagesContainer = (VBox) pdfPagesAnchorPane.getChildren().get(0);

        pdfPagesContainer.getChildren().forEach(node -> {
            ImageView imageView = (ImageView) node;
            double pageWidth = imageView.getImage().getWidth();
            double pageHeight = imageView.getImage().getHeight();

            double scaleX = scrollPaneWidth / pageWidth;
            double scaleY = scrollPaneHeight / pageHeight;
            double scale = Math.min(scaleX, scaleY);

            imageView.fitWidthProperty().bind(scrollPane.widthProperty().subtract(20));
            imageView.setFitHeight(pageHeight * scale);
        });
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
        loadRecentFiles();
    }



}
