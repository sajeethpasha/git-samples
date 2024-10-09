package org.example;

import java.util.Arrays;

public class Main2 {
    public static int solution(int[] numbers, int[] nRange) {
        int nMin = nRange[0];
        int nMax = nRange[1];

        return Arrays.stream(numbers)  // Convert the numbers array to a stream
                .filter(num -> num > nMin && num < nMax)  // Filter numbers that are greater than nMin and less than nMax
                .min()  // Find the minimum value from the filtered numbers
                .orElse(0);  // Return 0 if no value matches the filter
    }

    public static void main(String[] args) {
        int[] numbers1 = {11, 4, 23, 9, 10};
        int[] nRange1 = {5, 12};
        System.out.println(solution(numbers1, nRange1)); // Expected Output: 9

        int[] numbers2 = {1, 3, 2};
        int[] nRange2 = {1, 1};
        System.out.println(solution(numbers2, nRange2)); // Expected Output: 0

        int[] numbers3 = {7, 23, 3, 1, 3, 5, 2};
        int[] nRange3 = {2, 7};
        System.out.println(solution(numbers3, nRange3)); // Expected Output: 3
    }}