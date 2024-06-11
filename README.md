## Artificial Neural Network Tools - ANN - Java Sandbox

This project started by following this tutorial to build, test and train a model for the mnist dataset  
[Java Neural Network Tutorial](https://www.youtube.com/watch?v=3MMonOWGe0M&list=PLpcNcOt2pg8k_YsrMjSwVdy3GX-rc_ZgN)  

Project contains three applications for an Artificial Neural Network (ANN) built in Java:  

1. A console application for training and serializing the model.
2. A JavaFX application for drawing numbers, saving training data, and running inference against the serialized model.
3. A console application for shaping data. Augmenting a dataset of images, and splitting the set for training, validation, test  
 

### Features

- **Training and Serialization:** Train your ANN model and save it for future use.
- **Analyze Models:** Serialized model naming convention includes dataset, learning rates, success rate
- **Retrain** Transfer Learning - Start with parameters from a saved model to train new model.
- **JavaFX Interface:** Draw and save numbers to create training data, and run inference on the saved model.
- **Early Stopping and Checkpointing:** Monitor validation loss and stop training when necessary, with model saving at appropriate points.
- **Data Augmentation and Splitting:** Generate synthetic data and split it into training, validation, and test sets using the ShapeData console app.


### Model Naming Convention

dataset_lrconvolutionlayer_lrfullyconnectedlayer_#epoch_successrate  
train_augmented.csv_0.150_0.200_42_0.730_model.ser  

## Usage

[Code Overview](https://youtu.be/1z4VcV4YWBY?feature=shared)  

## Dependencies

javafx
javafx-swing


## License

This project is licensed under the [MIT License](LICENSE.md).
This README.md file includes sections for installation instructions, usage examples, contributing guidelines, and licensing information. You can customize it according to your project's specific needs, adding or removing sections as necessary.
