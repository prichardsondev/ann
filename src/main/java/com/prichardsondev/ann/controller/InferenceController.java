package com.prichardsondev.ann.controller;

import com.prichardsondev.ann.controller.util.MessageUtil;
import com.prichardsondev.ann.model.DataProcessor;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class InferenceController {
    private static final String DATA_DIR = "src/main/data/";
    private int numberCorrect = 0;
    private int numberGuess = 0;
    private Stage primaryStage;

    @FXML
    private Canvas drawingCanvas;

    @FXML
    private TextField labelField, guessField;

    @FXML
    private Label saveCountLabel, statusBar;

    private GraphicsContext gc;
    private double prevX, prevY;
    private int saveCount = 0;
    private DataProcessor dataProcessor;

    @FXML
    public void initialize() {
        gc = drawingCanvas.getGraphicsContext2D();
        saveCountLabel.setText("0");
        statusBar.setText("Ready");
        statusBar.setWrapText(true);
        statusBar.setPrefWidth(400);  // Set a preferred width for the label
        dataProcessor = new DataProcessor();
        setupCanvas();
    }

    private void setupCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());

        drawingCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        drawingCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            double x = event.getX();
            double y = event.getY();
            drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }

    private void drawLine(double x1, double y1, double x2, double y2) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(8);
        gc.strokeLine(x1, y1, x2, y2);
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            prevX = event.getX();
            prevY = event.getY();
        }
    }

    @FXML
    private void handleSave() throws IOException {
        int[] vector = dataProcessor.saveCanvasAsMNISTVector(drawingCanvas);
        int[] prependVector = dataProcessor.prependValue(vector, labelField.getText());
        String file = DATA_DIR + "data.csv";

        dataProcessor.appendToCSV(file, prependVector);
        clearCanvas();
        updateSaveCount();
        statusBar.setText("Saved item " + saveCount);
    }

    @FXML
    private void handleClear() {
        clearCanvas();
        statusBar.setText("Canvas cleared");
    }

    @FXML
    private void handleGuess() throws IOException {
        /*
            Hackish - don't actually need the value to predict just
            to keep running tally - modify predictionDigit code
         */
        if(guessField.getText().isEmpty())
        {
            showAlert("Missing Guess Value", "Enter value you are guessing");
            return;
        }
        int predictedDigit = dataProcessor.predictDigit(drawingCanvas, guessField.getText());

        if (predictedDigit == Integer.parseInt(guessField.getText())) numberCorrect++;
        double percent = (double) numberCorrect / ++numberGuess;

        statusBar.setText("Prediction: " + predictedDigit + "\nNumber correct: " + numberCorrect + "\nNumber guessed: " +
                numberGuess + "\n" + MessageUtil.getInferenceMessage( (int)(percent*100)));

        clearCanvas();
    }

    private void updateSaveCount() {
        saveCount++;
        saveCountLabel.setText(String.valueOf(saveCount));

        if (saveCount % 10 == 0) {
            int currentLabel = Integer.parseInt(labelField.getText());
            currentLabel = (currentLabel + 1) % 10;
            labelField.setText(String.valueOf(currentLabel));

            String message = "You've saved 10 items. Start drawing " + labelField.getText();
            saveCount = 0;
            saveCountLabel.setText(String.valueOf(saveCount));

            if (currentLabel == 0) {
                message = "You've saved 10 items. Start drawing 0 or quit";
            }

            showAlert("Info", message);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.showAndWait();
    }

    private void clearCanvas() {
        gc.setFill(Color.WHITE); // Set the fill color to white
        gc.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight()); // Fill the canvas with white
    }

    public void handleMouseClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            handleSave();
        }
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
}