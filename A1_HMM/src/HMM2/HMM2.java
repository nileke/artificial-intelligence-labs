package HMM2;

// Imports for prints
import utils.Matrices;
import java.util.Arrays;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HMM2 {
    private static double[][] A;
    private static double[][] B;
    private static double[][] pi;
    private static int[] emissions;

    public HMM2() {
        int[] stateSequence = estimateSequenceStateVector();

        for (int s : stateSequence) System.out.print(s + " ");
        System.out.println();
    }


    public int[] estimateSequenceStateVector() {
        double[] b_t = B[emissions[0]]; // First observation
        double[][] deltaMatrix = new double[A.length][emissions.length]; // Matrix for delta probabilities
        int[][] sequenceMatrix = new int[A[0].length][emissions.length]; // matrix for states

        // Get first delta vector
        for (int i=0; i < A.length; i++) {
            deltaMatrix[i][0] = pi[0][i] * b_t[i];
        }

        // Get delta vectors and states for t: 1-n
        for (int t=1; t < emissions.length; t++) {
            // Get observations at time t
            b_t = B[emissions[t]];

            // previous delta_i vector per row * b_t per col * a_nm
            for (int m=0; m < A.length; m++) {
                int stateMax = -1;
                double probabilityMax = -1;
                for (int n=0; n < A.length; n++) {
                    double res = deltaMatrix[n][t-1] * b_t[m] * A[n][m];
                    if (probabilityMax < res) {
                        probabilityMax = res;
                        stateMax = n;
                    }
                }
                deltaMatrix[m][t] = probabilityMax;
                sequenceMatrix[m][t] = stateMax;
            }
        }

        // Look for the highest value at the last time step t
        double probabilityMax = -1;
        int stateMax = -1;
        for (int m=0; m < A.length; m++) {
            if (deltaMatrix[m][emissions.length-1] > probabilityMax) {
                stateMax = m;
                probabilityMax = deltaMatrix[m][emissions.length-1];
            }
        }

        // Get the state sequence
        int[] sequence = new int[emissions.length];
        sequence[emissions.length-1] = stateMax;
        for (int i=emissions.length-1; i > 0; i--) {
            sequence[i-1] = sequenceMatrix[sequence[i]][i];
        }

        return sequence;
    }


    public static void main(String[] args) throws FileNotFoundException {
        String filepath = "./A1_HMM/resources/hmm3_01.in";
        String filepath2 = "./A1_HMM/resources/hmm3_02.in";
        String filepath3 = "./A1_HMM/resources/hmm3_03.in";
        Scanner sc = new Scanner(new File(filepath));
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

        // Transposed B so that every column is a sub-array
        m = sc.nextInt();
        n = sc.nextInt();
        B = new double[n][m];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
                B[j][i] = sc.nextDouble();
            }
        }

        m = sc.nextInt();
        n = sc.nextInt();
        pi = new double[m][n];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
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
        System.out.println("=======================");
*/

        new HMM2();
    }
}
