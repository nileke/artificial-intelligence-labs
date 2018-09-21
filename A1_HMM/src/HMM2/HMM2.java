package HMM2;

// Imports for prints

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HMM2 {
    private static double[][] transitionMatrix;
    private static double[][] observationMatrix;
    private static double[][] pi;
    private static int[] emissions;

    public HMM2() {
        int[] stateSequence = estimateSequenceStateVector();
        
        StringBuilder output = new StringBuilder();
        for (int s : stateSequence) output.append(s).append(" ");
        System.out.println(output.toString());
    }


    public int[] estimateSequenceStateVector() {
        double[] o_t = observationMatrix[emissions[0]]; // First observation
        int numOfEmissions = emissions.length;
        double[][] deltaMatrix = new double[transitionMatrix.length][numOfEmissions]; // Matrix for delta probabilities
        int[][] deltaIdxMatrix = new int[transitionMatrix.length][numOfEmissions]; // matrix for states

        // Get first delta vector
        for (int i = 0; i < transitionMatrix.length; i++) {
            deltaMatrix[i][0] = pi[0][i] * o_t[i];
        }

        // Get delta vectors and states for t: 1-n
        for (int t=1; t < numOfEmissions; t++) {
            // Get observation o at time t
            o_t = observationMatrix[emissions[t]];

            // previous delta_i vector per row * o_t per col * a_nm
            for (int m = 0; m < transitionMatrix.length; m++) {
                int stateMax = -1;
                double probabilityMax = -1;
                for (int n = 0; n < transitionMatrix.length; n++) {
                    double res = deltaMatrix[n][t-1] * o_t[m] * transitionMatrix[n][m];
                    if (probabilityMax < res) {
                        probabilityMax = res;
                        stateMax = n;
                    }
                }
                deltaMatrix[m][t] = probabilityMax;
                deltaIdxMatrix[m][t] = stateMax;
            }
        }

        // Look for the highest value at the last time step t
        double probabilityMax = -1;
        int stateMax = -1;
        for (int m = 0; m < transitionMatrix.length; m++) {
            if (deltaMatrix[m][numOfEmissions-1] > probabilityMax) {
                stateMax = m;
                probabilityMax = deltaMatrix[m][numOfEmissions-1];
            }
        }

        // Get the state sequence
        int[] sequence = new int[numOfEmissions];
        sequence[numOfEmissions-1] = stateMax;
        for (int i=numOfEmissions-1; i > 0; i--) {
            sequence[i-1] = deltaIdxMatrix[sequence[i]][i];
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
        transitionMatrix = new double[m][n];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
                transitionMatrix[i][j] = sc.nextDouble();
            }
        }

        // Transposed observationMatrix so that every column is a sub-array
        m = sc.nextInt();
        n = sc.nextInt();
        observationMatrix = new double[n][m];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
                observationMatrix[j][i] = sc.nextDouble();
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
        Matrices.printMatrix(transitionMatrix, "transitionMatrix matrix");
        Matrices.printMatrix(observationMatrix, "observationMatrix matrix");
        Matrices.printMatrix(pi, "pi vector");
        System.out.println("=======================");
*/

        new HMM2();
    }
}
