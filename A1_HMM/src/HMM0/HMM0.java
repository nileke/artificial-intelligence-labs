package HMM0;

import utils.Matrices;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HMM0 {
    private static double[][] A;
    private static double[][] B;
    private static double[][] pi;

    private HMM0() {
        double[][] res = this.calculateEmission(A,B,pi);
        this.printEmission(res);
    }

    private void printEmission(double[][] output) {
        System.out.print(output.length + " " + output[0].length);
        for (double[] row : output) {
            for (double c : row) {
                System.out.print(" " + c); // String.format("%1.1f",c)
            }
            System.out.println();
        }
    }

    private double[][] calculateEmission(double[][] A, double[][] B, double[][] pi) {
        double[][] a;

        a = Matrices.multiply(pi, A);
        if (a == null) return null;

        return Matrices.multiply(a, B);
    }

    public static void main(String[] args) throws FileNotFoundException {
        String filepath = "./resources/sample_00.in";
        Scanner sc = new Scanner(new File(filepath));
        //Scanner sc = new Scanner(System.in);
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

        new HMM0();
    }
}
