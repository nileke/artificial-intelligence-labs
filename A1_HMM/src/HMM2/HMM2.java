package HMM2;

import utils.Matrices;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class HMM2 {
    private static double[][] A;
    private static double[][] B;
    private static double[][] pi;
    private static int[] emissions;

    public HMM2() {
        double[][] delta = estimateSequenceStateVector();
        //delta = Matrices.transpose(delta);

        Matrices.printMatrix(delta);
        int[] sequence = new int[delta.length];
        int idx;
        for (int i=delta.length-1; i >= 0; i--) {
            idx = getStateSequence(delta[i]);
            sequence[i] = idx;
        }

        for (int s : sequence) System.out.print(s + " ");
    }

    public int getStateSequence(double[] delta) {
        // int[] result = new int[delta.length];
        int idx = 0;
        for (int i=1; i < delta.length; i++) {
            idx = delta[i] > delta[idx] ? i : idx;
        }

        return idx;
    }

    public double[][] estimateSequenceStateVector() {

        double[][] b_i;
        double[][] delta_i = new double[pi.length][pi[0].length];
        double[][] deltaMatrix = new double[A.length][A[0].length];
        double[][] resultMatrix = new double[A.length][A[0].length];
        for (int i=0; i < emissions.length; i++) {
            b_i = Matrices.getColumn(B, emissions[i]);

            if (i == 0) {
                delta_i = Matrices.multiplyEntrywise(pi, b_i);
                for (int m=0; m < A.length; m++) {
                    for (int n=0; n < A[0].length; n++) {
                        deltaMatrix[m][n] = delta_i[n][0];
                    }
                }
                resultMatrix[i] = Matrices.transpose(delta_i)[0];
            }
            else {
                // delta_i vector per row * b_i per col
                int idx = -1;
                for (int m=0; m < A.length; m++) {
                    idx += 1;
                    for (int n=0; n < A[0].length; n++) {
                        deltaMatrix[m][n] = b_i[idx][0] * delta_i[n][0];
                    }
                }
            }

            double[][] nextDeltaMatrix = Matrices.multiplyEntrywise(A, deltaMatrix);
            // Matrices.printMatrix(deltaMatrix, "Delta Matrix");
            // Matrices.printMatrix(nextDeltaMatrix, "Next Delta Matrix");

            int x = 0;
            for (double[] row : nextDeltaMatrix) {
                delta_i[x][0] = findMaxInRow(row);
                x++;
            }

            if (i != 0) {
                resultMatrix[i] = Matrices.transpose(delta_i)[0];
            }

            Matrices.printMatrix(resultMatrix);
        }

        return resultMatrix;
    }


    public double findMaxInRow(double[] row) {
        double max = 0;
        for (double val : row) {
            if (val > max) max = val;
        }
        return max;
    }


    public static void main(String[] args) throws FileNotFoundException {
        String filepath = "./A1_HMM/resources/hmm3_01.in";
        Scanner sc = new Scanner(new File(filepath));
        //Scanner sc = new Scanner(System.in);

        // Need transposed A and B matrix
        int m;
        int n;


        // Transposed A matrix
        m = sc.nextInt();
        n = sc.nextInt();
        A = new double[n][m];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
                A[j][i] = sc.nextDouble();
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
        pi = new double[n][m];
        for (int i=0; i < n; i++) {
            for (int j=0; j < m; j++) {
                pi[i][j] = sc.nextDouble();
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

        new HMM2();
    }
}
