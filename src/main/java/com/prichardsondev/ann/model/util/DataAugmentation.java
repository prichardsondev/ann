package com.prichardsondev.ann.model.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class DataAugmentation {

    public static void main(String[] args) throws IOException {
        String csvFile = "/test.csv";
        String outputFile = "augmented_test_with_label_int.csv";
        List<String[]> data = readCSV(csvFile);
        List<String[]> augmentedData = augmentData(data);
        writeCSV(outputFile, augmentedData);
    }

    public static List<String[]> readCSV(String file) throws IOException {
        List<String[]> data = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            data.add(line.split(","));
        }
        br.close();
        return data;
    }

    public static void writeCSV(String file, List<String[]> data) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for (String[] row : data) {
            bw.write(String.join(",", row));
            bw.newLine();
        }
        bw.close();
    }

    public static List<String[]> augmentData(List<String[]> data) {
        List<String[]> augmentedData = new ArrayList<>();
        for (String[] row : data) {
            String label = row[0];
            int[][] image = new int[28][28];
            for (int i = 1; i < row.length; i++) {
                image[(i - 1) / 28][(i - 1) % 28] = Integer.parseInt(row[i]);
            }

            augmentedData.addAll(Arrays.asList(
                    augmentImage(image, label, DataAugmentation::addRandomNoise),
                    augmentImage(image, label, img -> rotateImage(img, 15)),
                    augmentImage(image, label, img -> scaleImage(img, 1.1)),
                    augmentImage(image, label, img -> translateImage(img, 2, 2))
            ));
        }
        return augmentedData;
    }

    private static String[] augmentImage(int[][] image, String label, Function<int[][], int[][]> transformation) {
        int[][] transformedImage = transformation.apply(image);
        String[] augmentedRow = new String[28 * 28 + 1];
        augmentedRow[0] = label;
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                augmentedRow[1 + i * 28 + j] = String.valueOf(transformedImage[i][j]);
            }
        }
        return augmentedRow;
    }

    private static int[][] addRandomNoise(int[][] image) {
        Random rand = new Random();
        int[][] noisyImage = new int[28][28];
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                if (image[i][j] == 0) {
                    noisyImage[i][j] = 0;
                } else {
                    int noise = rand.nextInt(256);
                    noisyImage[i][j] = Math.min(255, Math.max(1, image[i][j] + noise));
                }
            }
        }
        return noisyImage;
    }

    private static int[][] rotateImage(int[][] image, double angle) {
        BufferedImage img = toBufferedImage(image);
        BufferedImage rotatedImg = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = rotatedImg.createGraphics();
        g2d.rotate(Math.toRadians(angle), 14, 14);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return toArray(rotatedImg);
    }

    private static int[][] scaleImage(int[][] image, double scale) {
        BufferedImage img = toBufferedImage(image);
        int newWidth = (int) (28 * scale);
        int newHeight = (int) (28 * scale);
        BufferedImage scaledImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = scaledImg.createGraphics();
        g2d.drawImage(img, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        int[][] scaledArray = toArray(scaledImg);
        int[][] result = new int[28][28];
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                if (i < newHeight && j < newWidth) {
                    result[i][j] = scaledArray[i][j];
                } else {
                    result[i][j] = 0;
                }
            }
        }
        return result;
    }

    private static int[][] translateImage(int[][] image, int shiftX, int shiftY) {
        BufferedImage img = toBufferedImage(image);
        BufferedImage translatedImg = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = translatedImg.createGraphics();
        g2d.drawImage(img, shiftX, shiftY, null);
        g2d.dispose();
        return toArray(translatedImg);
    }

    private static BufferedImage toBufferedImage(int[][] image) {
        BufferedImage img = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                int value = image[i][j];
                img.setRGB(j, i, (value << 16) | (value << 8) | value);
            }
        }
        return img;
    }

    private static int[][] toArray(BufferedImage image) {
        int[][] array = new int[28][28];
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                array[i][j] = image.getRGB(j, i) & 0xFF;
            }
        }
        return array;
    }
}
