import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HMM0 {
    private static double[][] A;
    private static double[][] B;
    private static double[][] pi;

    public static void main(String[] args) {
        //String filepath = "./resources/data.input";
        Scanner sc = new Scanner(System.in);
        //try (Scanner sc = new Scanner(new File(filepath))) {

            int x;
            int y;

            x = sc.nextInt();
            y = sc.nextInt();
            A = new double[x][y];

            for (int i = 0; i < y; i++) {
                for (int j = 0; j < x; j++) {
                    A[i][j] = sc.nextDouble();
                }
            }

            x = sc.nextInt();
            y = sc.nextInt();
            B = new double[x][y];

            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    B[i][j] = sc.nextDouble();
                }
            }

            x = sc.nextInt();
            y = sc.nextInt();
            pi = new double[x][y];

            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    pi[i][j] = sc.nextDouble();
                }
            }


            calculateNextO();
        /*} catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    private static void calculateNextO() {
        // (pi * A) * B
        double[][] a;
        double[][] res;

        a = MatrixOperations.multiply(pi, A);

        res = MatrixOperations.multiply(a, B);

        // Prints for debugging
        /*
        MatrixOperations.printMatrix("A-matrix", A);
        MatrixOperations.printMatrix("B-matrix", B);
        MatrixOperations.printMatrix("pi", pi);
        System.out.println();
        MatrixOperations.printMatrix("Multiply A and B", MatrixOperations.multiply(A,B));
        System.out.println();
        MatrixOperations.printMatrix("a-vector", a);
        MatrixOperations.printMatrix("a times B", MatrixOperations.multiply(a, B));
        MatrixOperations.printMatrix("emission", res);
        System.out.print(res.length + " " + res[0].length);
        MatrixOperations.printMatrix(" ",res);
        */

        assert res != null;
        System.out.print(res.length + " " + res[0].length);
        for (double[] row : res) {
            for (double c : row) {
                System.out.print(" " + String.format("%1.1f",c));
            }
            System.out.println();
        }
    }
}
