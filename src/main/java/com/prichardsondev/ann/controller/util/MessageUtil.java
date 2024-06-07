package com.prichardsondev.ann.controller.util;

public class MessageUtil {
    public static String getInferenceMessage(int accuracyPercentage) {
        if (accuracyPercentage >= 0 && accuracyPercentage <= 25) {
            return "Accuracy " + accuracyPercentage + "%: Congrats! You'd make a good weather \n forecaster—never wrong, just often inaccurate!";
        } else if (accuracyPercentage >= 26 && accuracyPercentage <= 40) {
            return "Accuracy " + accuracyPercentage + "%: Congrats! You'd make a great baseball \nplayer but a lousy surgeon!";
        } else if (accuracyPercentage >= 41 && accuracyPercentage <= 60) {
            return "Accuracy " + accuracyPercentage + "%: Not bad! You could definitely win at \nguessing games at a carnival!";
        } else if (accuracyPercentage >= 61 && accuracyPercentage <= 80) {
            return "Accuracy " + accuracyPercentage + "%: Nice job! You're getting the hang of it—like \na barista who gets your coffee order right most of the time!";
        } else if (accuracyPercentage >= 81 && accuracyPercentage <= 90) {
            return "Accuracy " + accuracyPercentage + "%: Impressive! You'd make a solid \ndetective—Sherlock Holmes would be proud!";
        } else if (accuracyPercentage >= 91 && accuracyPercentage <= 99) {
            return "Accuracy " + accuracyPercentage + "%: Fantastic! You're a data wizard—almost \nready to replace your human counterparts!";
        } else if (accuracyPercentage == 100) {
            return "Accuracy " + accuracyPercentage + "%: Perfect! You're an ANN genius \n-move over,Andrej Karpathy!";
        } else {
            return "Invalid accuracy percentage!";
        }
    }
}
