import java.math.BigDecimal;

public class MatrixOperations {
    public int length;
    public int height;
    private double[][] matrix;

    public MatrixOperations(int length, int height) {
        this.length = length;
        this.height = height;
        this.matrix = new double[height][length];
    }


    public static double[][] multiply(double[][] A, double[][] B) {
        int ACol = A[0].length;
        int BRow = B.length;
        if(ACol != BRow) return null;
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

    // For debugging
    public static void printMatrix(String varName, double[][] matrix) {
        if (varName.length() > 0) {
            System.out.println(varName);
        }
        for (double[] m : matrix) {
            for (double var : m) {
                System.out.print(String.format("%1.1f",var) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
