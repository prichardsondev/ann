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

import java.io.IOException;

public class InferenceController {
    private static final String DATA_DIR = "src/main/data/";
    private int numberCorrect = 0;
    private int numberGuess = 0;

    @FXML
    private TextArea tallyArea;

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
        String scratch = DATA_DIR + "scratch.csv";

        dataProcessor.appendToCSV(scratch, prependVector);
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
        int predictedDigit = dataProcessor.predictDigit(drawingCanvas, labelField.getText());
        guessField.setText(String.valueOf(predictedDigit));

        if (predictedDigit == Integer.parseInt(labelField.getText())) numberCorrect++;
        double percent = (double) numberCorrect / ++numberGuess;
        //tallyArea.setText("Number correct: " + numberCorrect + "\nNumber guessed: " + numberGuess + "\nPercent correct: " + percent);
        statusBar.setText("Number correct: " + numberCorrect + "\nNumber guessed: " +
                numberGuess + "\n" + MessageUtil.getInferenceMessage( (int)(percent*100)));
    }

    @FXML
    private void handleAugmentData() throws IOException {
        String csvFile = DATA_DIR + "data.csv";
        String outputFile = DATA_DIR + "data_augmented.csv";
        dataProcessor.augmentData(csvFile, outputFile);
        statusBar.setText("Data augmented");
    }

    @FXML
    private void handleSplitDataSet() {
        String fullSet = DATA_DIR + "data_augmented.csv";
        dataProcessor.splitCSVFile(fullSet, 80);
        statusBar.setText("Data augmented");
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
}


//package com.prichardsondev.ann.controller;
//
//import com.prichardsondev.ann.controller.util.MessageUtil;
//import com.prichardsondev.ann.model.util.DataAugmentation;
//import javafx.embed.swing.SwingFXUtils;
//import javafx.fxml.FXML;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.control.*;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import javafx.scene.image.WritableImage;
//import javafx.scene.input.ContextMenuEvent;
//import javafx.scene.input.MouseButton;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.paint.Color;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.util.List;
//
//import static com.prichardsondev.ann.model.util.ArrayUtil.prependValue;
//import static com.prichardsondev.ann.model.util.CSV.appendToCSV;
//import static com.prichardsondev.ann.model.util.CSV.splitCSVFile;
//import static com.prichardsondev.ann.model.util.DataAugmentation.*;
//
//public class InferenceController {
//    private static final String DATA_DIR = "src/main/data/";
//
//    public TextArea tallyArea;
//    @FXML
//    private Canvas drawingCanvas;
//
//    @FXML
//    private Button saveButton, clearButton, augmentDataButton, guessButton, splitDataSetButton;
//
//    @FXML
//    private TextField labelField, guessField;
//
//    @FXML
//    private Label saveCountLabel, statusBar;
//
//    private GraphicsContext gc;
//    private double prevX, prevY;
//
//    private int saveCount = 0;
//
//    @FXML
//    public void initialize() {
//        gc = drawingCanvas.getGraphicsContext2D();
//        saveCountLabel.setText("0");
//        statusBar.setText("Ready");
//        statusBar.setWrapText(true);
//        statusBar.setPrefWidth(400);  // Set a preferred width for the label
//        setupCanvas();
//    }
//
//    private void setupCanvas() {
//        gc.setFill(Color.WHITE);
//        gc.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
//
//        drawingCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
//        drawingCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
//    }
//
//    @FXML
//    private void handleMouseDragged(MouseEvent event) {
//        if((event.getButton() == MouseButton.PRIMARY)) {
//            double x = event.getX();
//            double y = event.getY();
//            drawLine(prevX, prevY, x, y);
//            prevX = x;
//            prevY = y;
//        }
//    }
//
//    private void drawLine(double x1, double y1, double x2, double y2) {
//        gc.setStroke(Color.BLACK);
//        gc.setLineWidth(8);
//        gc.strokeLine(x1, y1, x2, y2);
//    }
//
//    @FXML
//    private void handleMousePressed(MouseEvent event) {
//        if (event.getButton() == MouseButton.PRIMARY) {
//            prevX = event.getX();
//            prevY = event.getY();
//        }
//    }
//
//    @FXML
//    private void handleSave() throws IOException {
//        int[] vector = saveCanvasAsMNISTVector();
//        int[] prependVector = prependValue(vector, labelField.getText());
//        String scratch = DATA_DIR + "scratch.csv";
//
//        appendToCSV(scratch, prependVector);
//        clearCanvas();
//        updateSaveCount();
//        statusBar.setText("Saved item " + saveCount);
//    }
//
//    @FXML
//    private void handleClear() {
//        gc.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
//        statusBar.setText("Canvas cleared");
//    }
//
//    @FXML
//    private void handleAugmentData() throws IOException {
//        // Augment data logic here
//        String csvFile = DATA_DIR + "test.csv";
//        String outputFile = DATA_DIR + "test_augmented.csv";
//        java.util.List<String[]> data = readCSV(csvFile);
//        List<String[]> augmentedData = augmentData(data);
//        writeCSV(outputFile, augmentedData);
//        statusBar.setText("Data augmented");
//    }
//
//    @FXML
//    private void handleGuess() {
//        // Inference logic here
//        int predictedDigit = 0 /* model prediction */;
//        guessField.setText(String.valueOf(predictedDigit));
//        statusBar.setText("Inference complete: " + predictedDigit);
//    }
//
//    @FXML
//    private void handleSplitDataSet() {
//        String fullSet = DATA_DIR +  "test_augmented.csv";
//        splitCSVFile(fullSet, 80);
//    }
//
//    private void updateSaveCount() {
//        saveCount++;
//        saveCountLabel.setText(String.valueOf(saveCount));
//
//        if (saveCount % 10 == 0) {
//            int currentLabel = Integer.parseInt(labelField.getText());
//            currentLabel = (currentLabel + 1) % 10;
//            labelField.setText(String.valueOf(currentLabel));
//
//            String message = "You've saved 10 items. Start drawing " + labelField.getText();
//            saveCount = 0;
//            saveCountLabel.setText(String.valueOf(saveCount));
//
//            if (currentLabel == 0) {
//                message = "You've saved 10 items. Start drawing 0 or quit";
//            }
//
//            showAlert("Info", message);
//        }
//    }
//
//    private void showAlert(String title, String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    private void clearCanvas() {
//        GraphicsContext gc = drawingCanvas.getGraphicsContext2D();
//        gc.setFill(Color.WHITE); // Set the fill color to white
//        gc.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight()); // Fill the canvas with white
//    }
//
//    private int[] saveCanvasAsMNISTVector() throws IOException {
//        // Capture the canvas as an image
//        WritableImage writableImage = new WritableImage((int) drawingCanvas.getWidth(), (int) drawingCanvas.getHeight());
//        drawingCanvas.snapshot(null, writableImage);
//        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
//
//        // Resize to 28x28 with better interpolation
//        BufferedImage resizedImage = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
//        java.awt.Graphics2D g2d = resizedImage.createGraphics();
//        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        g2d.drawImage(bufferedImage, 0, 0, 28, 28, null);
//        g2d.dispose();
//
//        //bufferedImageToFile(resizedImage, "src/resized.png");
//        // Convert to grayscale vector
//        int[] vector = new int[28 * 28];
//        for (int y = 0; y < 28; y++) {
//            for (int x = 0; x < 28; x++) {
//                int grayValue = resizedImage.getRaster().getSample(x, y, 0);
//                // Invert the color: 0 for white, 1-255 for black/grayscale
//                vector[y * 28 + x] = 255 - grayValue;
//            }
//        }
//
//        return vector;
//    }
//
//    public void handleMouseClicked(MouseEvent mouseEvent) throws IOException {
//        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
//            handleSave();
//        }
//    }
//}
