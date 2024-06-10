package com.prichardsondev.ann.model;
import static com.prichardsondev.ann.model.data.DataReader.CSV2Image;
import static com.prichardsondev.ann.model.util.ArrayUtil.prependValue;
import static com.prichardsondev.ann.model.util.DataAugmentation.*;

import com.prichardsondev.ann.model.network.NeuralNetwork;
import com.prichardsondev.ann.model.util.ModelCheckpoint;
import com.prichardsondev.ann.model.data.Image;
import com.prichardsondev.ann.model.util.DataAugmentation;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class DataProcessor {
    private static final String DATA_DIR = "src/main/data/";

    public int[] saveCanvasAsMNISTVector(Canvas drawingCanvas) throws IOException {
        // Capture the canvas as an image
        WritableImage writableImage = new WritableImage((int) drawingCanvas.getWidth(), (int) drawingCanvas.getHeight());
        drawingCanvas.snapshot(null, writableImage);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        // Resize to 28x28 with better interpolation
        BufferedImage resizedImage = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(bufferedImage, 0, 0, 28, 28, null);
        g2d.dispose();

        // Convert to grayscale vector
        int[] vector = new int[28 * 28];
        for (int y = 0; y < 28; y++) {
            for (int x = 0; x < 28; x++) {
                int grayValue = resizedImage.getRaster().getSample(x, y, 0);
                vector[y * 28 + x] = 255 - grayValue; // Invert the color
            }
        }

        return vector;
    }

    public int[] prependValue(int[] vector, String value) {
        int[] result = new int[vector.length + 1];
        result[0] = Integer.parseInt(value);
        System.arraycopy(vector, 0, result, 1, vector.length);
        return result;
    }

    public void appendToCSV(String filePath, int[] vector) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            StringJoiner joiner = new StringJoiner(",");
            for (int i : vector) {
                String s = String.valueOf(i);
                joiner.add(s);
            }
            String row = joiner.toString();
            writer.write(row);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void augmentData(String inputFilePath, String outputFilePath) throws IOException {
        List<String[]> data = readCSV(inputFilePath);
        List<String[]> augmentedData = DataAugmentation.augmentData(data);
        writeCSV(outputFilePath, augmentedData);
    }

    public int predictDigit(Canvas c, String numberLabel) throws IOException {
        int[] vector = saveCanvasAsMNISTVector(c);
        int[] prependVector = prependValue(vector, numberLabel);
        String row = Arrays.stream(prependVector)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));
        Image image = CSV2Image(row);
        /*this is a checkpoint form a model I trained at 74%. I'd add some data and build your own model*/
        NeuralNetwork n = ModelCheckpoint.loadModel( DATA_DIR + "model_checkpoint.ser");
        return n != null ? n.guess(image) : 0; // Placeholder
    }

    public  void splitCSVFile(String csvFile, int trainingPercent) {
        List<String> lines = new ArrayList<>();
        String header = "";

        // Read the CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            header = br.readLine(); // read the header
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Shuffle the lines
        Collections.shuffle(lines);

        // Calculate the split point
        int splitIndex = (int) (lines.size() * (trainingPercent / 100.0));

        // Split the lines into training and test sets
        List<String> trainingSet = lines.subList(0, splitIndex);
        List<String> testSet = lines.subList(splitIndex, lines.size());

        // Write the training set to training.csv
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(  DATA_DIR + "train_augmented.csv"))) {
            bw.write(header);
            bw.newLine();
            for (String line : trainingSet) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the test set to test.csv
        try (BufferedWriter bw = new BufferedWriter(new FileWriter( DATA_DIR + "test_augmented.csv"))) {
            bw.write(header);
            bw.newLine();
            for (String line : testSet) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

