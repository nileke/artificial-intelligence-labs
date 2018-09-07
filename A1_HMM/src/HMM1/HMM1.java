package HMM1;

import utils.MatrixOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class HMM1 {
    private static double[][] A;
    private static double[][] B;
    private static double[][] pi;
    private static int[] emissions;

    private static double[][] alphaMatrix;

    public HMM1() {
        double[][] resultMatrix = this.calculateEmissionSequence();
        System.out.println(findMaxState(resultMatrix[emissions.length-1]));
    }

    private double findMaxState(double[] alpha) {
        double max = alpha[0];

        for (int i = 1; i < alpha.length; i++) {
            if (alpha[i] > max) {
                max = alpha[i];
            }
        }
        return max;
    }

    public double[][] calculateEmissionSequence() {
        double[][] alphaMatrix = new double[emissions.length][pi[0].length];

        int idx = emissions[0];
        double[] alpha = MatrixOperations.vectorElementMultiplication(B[idx], pi[0]);
        alphaMatrix[0] = alpha;

        double[][] alphaTemp = new double[1][alpha.length];
        double[][] c;
        for (int i=1; i < emissions.length; i++) {
            idx = emissions[i];
            alphaTemp[0] = alpha;
            c = MatrixOperations.multiply(alphaTemp, A);
            alpha = MatrixOperations.vectorElementMultiplication(c[0],B[idx]);
            alphaMatrix[i] = alpha;
        }
        // MatrixOperations.printMatrix(alphaMatrix);
        return alphaMatrix;
    }

    public static void main(String[] args) throws FileNotFoundException {
        String filepath = "./resources/hmm2_01.in";
        Scanner sc = new Scanner(new File(filepath));
        //Scanner sc = new Scanner(System.in);
        int m;
        int n;

        m = sc.nextInt();
        n = sc.nextInt();
        A = new double[m][n];
        for (int i=0; i < n; i++) {
            for (int j=0; j < m; j++) {
                A[i][j] = sc.nextDouble();
            }
        }

        m = sc.nextInt();
        n = sc.nextInt();
        B = new double[m][n];

        // Transposing B so that every row == column for easier operations.
        for (int i=0; i < n; i++) {
            for (int j=0; j < m; j++) {
                B[j][i] = sc.nextDouble();
            }
        }

        m = sc.nextInt();
        n = sc.nextInt();
        pi = new double[m][n];
        for (int i=0; i < n; i++) {
            for (int j=0; j < m; j++) {
                pi[j][i] = sc.nextDouble();
            }
        }

        n = sc.nextInt();
        emissions = new int[n];
        for (int i=0; i < n; i++) {
            emissions[i] = sc.nextInt();
        }
/*

        MatrixOperations.printMatrix(A, "A matrix");
        MatrixOperations.printMatrix(B, "B Matrix");
        MatrixOperations.printMatrix(pi, "pi vector");
        System.out.println(Arrays.toString(emissions));
*/

        new HMM1();
    }
}
