package com.example.demo;

import javafx.fxml.FXML;

import java.io.IOException;

public class AboutusController {
    @FXML
    private void handleBackAction() throws IOException {
        PdfApp.sceneFactory("PdfReader");
    }
}
