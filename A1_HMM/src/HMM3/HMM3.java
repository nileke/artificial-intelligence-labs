package HMM3;

// import for printing

import utils.Matrices;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class HMM3 {
    double[][] estimatedTransitionMatrix;
    double[][] estimatedObservationMatrix;
    double[][] estimatedPi;

    public HMM3(double[][] transitionMatrix,
                double[][] observationMatrix,
                double[][] pi,
                int[] emissions) {

        this.compute(transitionMatrix, observationMatrix, pi, emissions);
        System.out.println(this.toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int tm = estimatedTransitionMatrix.length;
        int tn = estimatedTransitionMatrix[0].length;

        int om = estimatedObservationMatrix.length;
        int on = estimatedObservationMatrix[0].length;

        sb.append(tm).append(" ").append(tn);

        for (int i=0; i < tm; i++) {
            for (int j=0; j < tn; j++) {
                sb.append(" ").append(this.estimatedTransitionMatrix[i][j]);
            }
        }
        sb.append("\n");
        sb.append(om).append(" ").append(on);

        for (int i=0; i < tm; i++) {
            for (int j=0; j < tn; j++) {
                sb.append(" ").append(this.estimatedObservationMatrix[i][j]);
            }
        }

        return sb.toString();
    }

    public void compute(double[][] transitionMatrix,
                        double[][] observationMatrix,
                        double[][] pi,
                        int[] emissions) {

        this.gammaPass(transitionMatrix, observationMatrix, pi, emissions);


        Matrices.printMatrix(this.estimatedTransitionMatrix, "Transition Matrix");
        Matrices.printMatrix(this.estimatedObservationMatrix, "Observation Matrix");

    }

    public double[][] alphaPass(double[][] transitionMatrix,
                                double[][] observationMatrix,
                                double[][] pi,
                                int[] emissions) {

        int idx = emissions[0];
        double[][] alpha = new double[transitionMatrix.length][emissions.length];
        double[] alphaPass = new double[emissions.length];

        // Get first alpha and alphaPass
        for (int i=0; i < transitionMatrix.length; i++) {
            alpha[i][0] = observationMatrix[i][idx] * pi[0][i];
            alphaPass[0] += alpha[i][0];
        }

        // Get alpha matrix and alphaPass for time t: 1-k
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

        return alpha;
    }


    public double[][] betaPass(double[][] transitionMatrix,
                         double[][] observationMatrix,
                         double[][] pi,
                         int[] emissions) {

        int numOfEmissions = emissions.length;
        double[][] deltaMatrix = new double[transitionMatrix.length][numOfEmissions]; // Matrix for delta probabilities

        // Get first delta vector
        for (int i=0; i < transitionMatrix.length; i++) {
            deltaMatrix[i][0] = pi[0][i] * observationMatrix[i][0];
        }

        // Get delta vectors and states for t: 1-n
        for (int t=1; t < numOfEmissions; t++) {
            // previous delta_i vector per row * o_t per col * a_ji
            // then assign max value to the time step vector
            for (int i = 0; i < transitionMatrix.length; i++) {
                double probabilityMax = -1;
                for (int j=0; j < transitionMatrix.length; j++) {
                    double res = deltaMatrix[j][t-1] * observationMatrix[i][emissions[t]] * transitionMatrix[j][i];
                    if (probabilityMax < res) {
                        probabilityMax = res;
                    }
                }
                deltaMatrix[i][t] = probabilityMax;
            }
        }

        return deltaMatrix;

    }

    public void gammaPass(double[][] transitionMatrix,
                          double[][] observationMatrix,
                          double[][] pi,
                          int[] emissions) {

        double[][] gamma;
        double[][] diGamma;
        double[][] sumDiGamma;

        int numStates = transitionMatrix.length;
        int numObservations = emissions.length;

        // Initialize lambda, gamma and diGamma
        double[][] alphaPass = alphaPass(transitionMatrix, observationMatrix, pi, emissions);
        double[][] betaPass = betaPass(transitionMatrix, observationMatrix, pi, emissions);
        gamma = new double[numStates][numObservations];
        diGamma = new double[numStates][numStates];

        // Sum of all diGamma matrices
        sumDiGamma = new double[numStates][numStates];

        int timesteps = emissions.length;
        double denom;
        for (int t=0; t < timesteps-1; t++) {
            // Calculate the denominator for gamma and diGamma calculation
            denom = 0;
            for (int i=0; i < numStates; i++) {
                for (int j=0; j < numStates; j++) {
                    denom += alphaPass[i][t]
                            * transitionMatrix[i][j]
                            * observationMatrix[j][emissions[t+1]]
                            * betaPass[j][emissions[t+1]];
                }
            }

            // Calculate gamma and diGamma
            for (int i=0; i < numStates; i++) {
                for (int j=0; j < numStates; j++) {
                    // Avoid dividing with zero
                    if (denom == 0) {
                        diGamma[i][j] = 0;
                        continue;
                    }

                    diGamma[i][j] = (
                            alphaPass[i][t]
                            * transitionMatrix[i][j]
                            * observationMatrix[j][emissions[t+1]]
                            * betaPass[j][t+1]
                            ) / denom;

                    gamma[i][t] += diGamma[i][j];
                    sumDiGamma[i][j] += diGamma[i][j];
                }
            }
        }

        this.estimatedTransitionMatrix = estimateTransitionMatrix(sumDiGamma, gamma, timesteps);
        this.estimatedObservationMatrix = estimateObservationMatrix(gamma, emissions, observationMatrix);
        this.estimatedPi = estimatePi(gamma);
    }

    public double[][] estimatePi(double[][] gamma) {
        double[][] result = new double[gamma.length][1];
        for (int i=0; i < gamma.length; i++) {
            result[i][0] = gamma[i][0];
        }
        return result;
    }

    public double[][] estimateTransitionMatrix(double[][] diGammaSum, double[][] gamma, int timesteps) {
        double[][] result = new double[diGammaSum.length][diGammaSum.length];
        double sumGamma;
        for (int i=0; i < diGammaSum.length; i++) {
            for (int j=0; j < diGammaSum.length; j++) {
                sumGamma = 0;
                for (int t=0; t < timesteps; t++) {
                    sumGamma += gamma[i][t];
                }
                result[i][j] = diGammaSum[i][j] / sumGamma;
            }
        }

        return result;
    }

    public double[][] estimateObservationMatrix(double[][] gamma, int[] emissions, double[][] observationMatrix) {

        double[][] result = new double[observationMatrix.length][observationMatrix[0].length];

        double numer;
        double denom;
        for (int i=0; i < result.length; i++) {
            for (int j=0; j < result[0].length; j++) {
                numer = 0;
                denom = 0;
                for (int t=0; t < emissions.length; t++) {
                    if (emissions[t] == j) numer += gamma[i][t];
                    denom += gamma[i][t];
                }
                result[i][j] = numer / denom;
            }
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        String filepath1 = "./A1_HMM/resources/hmm4_01.in";
        String filepath2 = "./A1_HMM/resources/hmm4_02.in";
        String filepath3 = "./A1_HMM/resources/hmm4_03.in";
        Scanner sc = new Scanner(new File(filepath1));

        double[][] transitionMatrix;
        double[][] observationMatrix;
        double[][] pi;
        int[] emissions;

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

        m = sc.nextInt();
        n = sc.nextInt();
        observationMatrix = new double[m][n];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
                observationMatrix[i][j] = sc.nextDouble();
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

        new HMM3(transitionMatrix, observationMatrix, pi, emissions);
    }
}
