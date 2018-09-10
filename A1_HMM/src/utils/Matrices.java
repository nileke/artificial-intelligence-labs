package utils;

import java.util.Arrays;

public class Matrices {

    public static double[][] multiply(double[][] A, double[][] B) {
        int ACol;
        int ARow;
        int BCol;
        int BRow;

        ACol = A[0].length;
        BRow = B.length;

        if(ACol != BRow ) {
            throw new IllegalArgumentException("Incorrect matrix dimensions.");
        }

        ARow = A.length;
        BCol = B[0].length;

        double[][] result = new double[ARow][BCol];
        for(int i = 0; i < ARow; i++) {
            for(int j = 0; j < BCol; j++) {
                for(int k = 0; k < ACol; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return result;
    }

    public static double elementSum(double[][] matrix) {
        double result = 0;
        for (int i=0; i < matrix.length; i++) {
            for (int j=0; j < matrix[0].length; j++) {
                result += matrix[i][j];
            }
        }
        return result;
    }

    private static double findMaxElement(double[] alpha) {
        double max = alpha[0];

        for (int i = 1; i < alpha.length; i++) {
            if (alpha[i] > max) {
                max = alpha[i];
            }
        }
        return max;
    }

    public static double[][] multiplyEntrywise(double[][] x, double[][] y) {
        if (x.length != y.length || x[0].length != y[0].length) {
            throw new IllegalArgumentException("Different dimensions of input vectors.");
        }
        double[][] result = new double[x.length][y[0].length];

        for (int i=0; i < x.length; i++) {
            for (int j=0; j < y[0].length; j++) {
                result[i][j] = x[i][j] * y[i][j];
            }
        }
        return result;
    }

    public static double[][] transpose(double[][] matrix) {
        double[][] result = new double[matrix[0].length][matrix.length];

        for (int i=0; i < matrix.length; i++) {
            for (int j=0; j < matrix[0].length; j++) {
                result[j][i] = matrix[i][j];
            }
        }

        return result;
    }

    public static double[][] getColumn(double[][] matrix, int col) {
        double[][] result = new double[matrix.length][1];
        for (int i=0; i < matrix.length; i++) {
            result[i][0] = matrix[i][col];
        }
        return result;
    }

    // For debugging
    public static void printMatrix(double[][] matrix, String... varName) {
        if (varName.length > 0) {
            System.out.println(varName[0]);
        }
        for (double[] m : matrix) {
            System.out.println(Arrays.toString(m));
        }
        System.out.println();
    }

}
