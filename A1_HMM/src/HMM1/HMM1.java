package HMM1;

// imports for printing
import utils.Matrices;
import java.util.Arrays;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class HMM1 {
    private static double[][] A;
    private static double[][] B;
    private static double[][] pi;
    private static int[] emissions;


    public HMM1() {
        double result = this.alphaPass(A,B,pi,emissions);
        System.out.println(result);
    }

    public double alphaPass(double[][] transitionMatrix,
                                double[][] observationMatrix,
                                double[][] pi,
                                int[] emissions) {

        int idx = emissions[0];
        double[][] alpha = new double[transitionMatrix.length][emissions.length];
        double[] alphaPass = new double[emissions.length];
        double res = 0;
        for (int i=0; i < transitionMatrix.length; i++) {
            alpha[i][0] = observationMatrix[i][idx] * pi[0][i];
            alphaPass[0] += alpha[i][0];
        }

        for (int t=1; t < emissions.length; t++) {
            idx = emissions[t];
            for (int i=0; i < transitionMatrix.length; i++) {
                for (int j=0; j < transitionMatrix.length; j++) {
                    alpha[i][t] += transitionMatrix[j][i] * alpha[j][t-1];

                }
                alpha[i][t] *= observationMatrix[i][idx];
                alphaPass[t] += alpha[i][t];
            }

        }

        return alphaPass[emissions.length-1];
    }


    public static void main(String[] args) throws FileNotFoundException {
        String filepath = "./A1_HMM/resources/hmm2_01.in";
        Scanner sc = new Scanner(new File(filepath)).useLocale(Locale.US);
        //Scanner sc = new Scanner(System.in);
        int m;
        int n;

        m = sc.nextInt();
        n = sc.nextInt();
        A = new double[m][n];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
                A[i][j] = sc.nextDouble();
            }
        }

        m = sc.nextInt();
        n = sc.nextInt();
        B = new double[m][n];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
                B[i][j] = sc.nextDouble();
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
        Matrices.printMatrix(A, "A matrix");
        Matrices.printMatrix(B, "B matrix");
        Matrices.printMatrix(pi, "pi vector");
        System.out.println(Arrays.toString(emissions));
        System.out.println("=======================");
*/



        new HMM1();
    }
}
