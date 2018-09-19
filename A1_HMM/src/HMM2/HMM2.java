package HMM2;

import utils.Matrices;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

public class HMM2 {
    private static double[][] A;
    private static double[][] B;
    private static double[][] pi;
    private static int[] emissions;

    public HMM2() {
        double[][] delta = estimateSequenceStateVector();
        //Matrices.printMatrix(delta);
        int[] sequence = new int[delta.length];
        int idx;
        for (int i=delta.length-1; i >= 0; i--) {
            idx = getStateSequence(delta[i]);
            sequence[i] = idx;
        }
        Stack<Integer> res = getAnswer(delta);

        for (int s : res) System.out.print(s + " ");
        System.out.println();
    }

    public int getStateSequence(double[] delta) {
        // int[] result = new int[delta.length];
        int idx = 0;
        for (int i=1; i < delta.length; i++) {
            idx = delta[i] > delta[idx] ? i : idx;
        }

        return idx;
    }

    public Stack<Integer> getAnswer(double[][] matrix) {
        Stack<Integer> result = new Stack<>();
        //int[] result = new int[matrix.length];
        for (int row=0; row < matrix.length; row++) {
            int idx = 0;
            for (int col=0; col < matrix[0].length; col++) {
                idx = matrix[idx][col] < matrix[row][col] ? col : idx;
            }
            result.push(idx);
            //result[row] = idx;
        }
        return result;
    }

    public double[][] estimateSequenceStateVector() {
        double[][] b_i;
        double[][] delta_i;
        double[][] deltaMatrix = new double[A.length][A[0].length];
        double[][] deltaSequence = new double[emissions.length][A[0].length];
        for (int i=0; i < emissions.length; i++) {
            b_i = Matrices.getColumn(B, emissions[i]);

            if (i == 0) {
                // Get first delta vector
                delta_i = Matrices.multiplyEntrywise(pi, b_i);
            } else {
                // previous delta_i vector per row * b_i per col * a_ij
                int idx = 0;
                delta_i = Matrices.getColumn(Matrices.transpose(deltaSequence), i-1);
                for (int m=0; m < deltaMatrix.length; m++) {
                    for (int n=0; n < deltaMatrix[0].length; n++) {
                        deltaMatrix[m][n] = A[n][m] * b_i[idx][0] * delta_i[n][0];
                    }
                    idx += 1;
                }

                int x = 0;
                for (double[] row : deltaMatrix) {
                    delta_i[x][0] = findMaxInRow(row);
                    x++;
                }
            }

            // Get the max in each row to create the new delta vector
            deltaSequence[i] = Matrices.transpose(delta_i)[0];
        }

        return deltaSequence;
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

        // Transposed pi
        m = sc.nextInt();
        n = sc.nextInt();
        pi = new double[n][m];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
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
        System.out.println("=======================");
*/

        new HMM2();
    }
}
