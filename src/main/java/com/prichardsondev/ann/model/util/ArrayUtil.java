package com.prichardsondev.ann.model.util;

public class ArrayUtil {

    public static int[] prependValue(int[] originalArray, String valueToAdd) {
        // Convert the String to an int
        int newValue = Integer.parseInt(valueToAdd);

        // Create a new array that is one element larger than the original
        int[] newArray = new int[originalArray.length + 1];

        // Set the first element of the new array to the new int value
        newArray[0] = newValue;

        // Copy the elements of the original array to the new array
        System.arraycopy(originalArray, 0, newArray, 1, originalArray.length);

        return newArray;
    }
}
