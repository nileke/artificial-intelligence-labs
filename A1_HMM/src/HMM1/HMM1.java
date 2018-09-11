package HMM1;

import utils.Matrices;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class HMM1 {
    private static double[][] A;
    private static double[][] B;
    private static double[][] pi;
    private static int[] emissions;


    public HMM1() {
        double[][] resultMatrix = this.calculateEmissionSequence();
        System.out.println(Matrices.elementSum(resultMatrix));
    }

    public double[][] calculateEmissionSequence() {
        int idx = emissions[0];
        double[][] alpha = Matrices.multiplyEntrywise(Matrices.getColumn(B, idx), Matrices.transpose(pi));
        double[][] c;
        for (int i=1; i < emissions.length; i++) {
            idx = emissions[i];
            c = Matrices.multiply(Matrices.transpose(A), alpha);
            alpha = Matrices.multiplyEntrywise(c, Matrices.getColumn(B, idx));
        }

        return alpha;
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
