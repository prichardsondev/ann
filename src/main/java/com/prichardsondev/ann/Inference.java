package com.prichardsondev.ann;

import com.prichardsondev.ann.controller.InferenceController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Inference extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Inference.class.getResource("inference-view.fxml"));
        Parent root = fxmlLoader.load(); // Load the FXML first
        InferenceController inferenceController = fxmlLoader.getController(); // Then get the controller
        inferenceController.setPrimaryStage(stage); // Set the primary stage

        Scene scene = new Scene(root, 400, 600);
        stage.setTitle("AI_BA!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}