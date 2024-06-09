package com.prichardsondev.ann;



import com.prichardsondev.ann.model.DataProcessor;

import java.io.IOException;

public class ShapeData {
    private static final String DATA_DIR = "src/main/data/";
    public static void main(String[] args) throws IOException {

        DataProcessor processor = new DataProcessor();

        processor.augmentData(DATA_DIR + "data.csv", DATA_DIR + "augment_data.csv");

        processor.splitCSVFile(DATA_DIR + "augment_data.csv", 80);

        System.out.println("Data processed successfully");
    }
}
