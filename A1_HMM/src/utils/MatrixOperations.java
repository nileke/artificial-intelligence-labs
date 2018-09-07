package utils;

public class MatrixOperations {

    public static double[][] multiply(double[][] A, double[][] B) {
        int ACol = A[0].length;
        int BRow = B.length;

        if(ACol != BRow ) {
                throw new IllegalArgumentException("Incorrect matrix dimensions. Check your input");
        }

        int ARow = A.length;
        int BCol = B[0].length;

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

    public static double[] vectorElementMultiplication(double[] x, double[] y) {
        int xLen = x.length;
        int yLen = y.length;

        if (xLen != yLen) {
            throw new IllegalArgumentException("Vectors must have same length");
        }
        double[] result = new double[xLen];

        for (int i=0; i < xLen; i++) {
            result[i] = x[i] * y[i];
        }

        return result;
    }

    // For debugging
    public static void printMatrix(double[][] matrix, String... varName) {
        if (varName.length > 0) {
            System.out.println(varName[0]);
        }
        for (double[] m : matrix) {
            for (double var : m) {
                System.out.print(var + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
