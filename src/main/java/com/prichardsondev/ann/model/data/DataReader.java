package com.prichardsondev.ann.model.data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataReader {
    private static final int rows = 28;
    private static final int cols = 28;

    public static List<Image> readData(String path){
        List<Image> images = new ArrayList<Image>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] lineData = line.split(",");

                double[][] data = new double[rows][cols];
                int label = Integer.parseInt(lineData[0]);
                int i = 1;

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        data[row][col] = Double.parseDouble(lineData[i]);
                        i++;
                    }
                }

                images.add(new Image(data, label));
            }
        } catch (Exception e){}

        return images;
    }

    public static Image readData(double[] imgData, int label) {


        double[][] data = new double[rows][cols];

        int i = 1;


        for (int col = 0; col < cols; col++) {
            data[col][col] = imgData[i];
            i++;
        }


//        images.add(new Image(data, label));
        return new Image(data, label);
    }

    public static List<Image> readImage(String path){
        List<Image> images = new ArrayList<Image>();
        try {
            // Read the image file
            BufferedImage img = ImageIO.read(new File(path));

            // Convert the image to grayscale if needed
            BufferedImage grayImg = convertToGrayscale(img);

            // Flatten the image and normalize pixel values
            double[] testVector = flattenImage(grayImg);

            // Now testVector contains your flattened and normalized image data
            // You can proceed to test your neural network with this vector

            Image i = readData(testVector,7);
            images.add(i);


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return images;
    }

    public static Image CSV2Image(String csv) {

        String[] lineData = csv.split(",");

        double[][] data = new double[rows][cols];
        int label = Integer.parseInt(lineData[0]);
        int i = 1;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                data[row][col] = Double.parseDouble(lineData[i]);
                i++;
            }
        }

        return new Image(data, label);

    }
    // Method to convert image to grayscale
    private static BufferedImage convertToGrayscale(BufferedImage img) {
        BufferedImage grayImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        grayImg.getGraphics().drawImage(img, 0, 0, null);
        return grayImg;
    }

    // Method to flatten the grayscale image
    private static double[] flattenImage(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        double[] testVector = new double[width * height];

        // Flatten the image and normalize pixel values
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = img.getRGB(x, y);
                // Extract the grayscale value from the pixel
                int grayValue = (pixel >> 16) & 0xff;
                testVector[y * width + x] = (double) grayValue / 255.0;
            }
        }
        return testVector;
    }
}
