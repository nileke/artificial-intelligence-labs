package HMM3;

// import for printing

// import utils.Matrices;
import utils.Kattio;

import java.io.File;
import java.io.FileNotFoundException;

public class HMM3 {
    double[][] estimatedTransitionMatrix;
    double[][] estimatedObservationMatrix;
    double[][] estimatedPi;

    int M;
    int N;
    int T;

    double[] c;

    public HMM3(double[][] transitionMatrix,
                double[][] observationMatrix,
                double[][] pi,
                int[] emissions) {

        this.N = transitionMatrix.length;
        this.M = observationMatrix[0].length;
        this.T = emissions.length;

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

        for (int i=0; i < om; i++) {
            for (int j=0; j < on; j++) {
                sb.append(" ").append(this.estimatedObservationMatrix[i][j]);
            }
        }

        return sb.toString();
    }

    public void compute(double[][] transitionMatrix,
                        double[][] observationMatrix,
                        double[][] pi,
                        int[] emissions) {

        int iter = 1;
        double oldLogProb = -Double.MAX_VALUE;
        int maxIterations = 1000;

        double logProb = this.gammaPass(transitionMatrix, observationMatrix, pi, emissions);
        while (true) {
            if (iter < maxIterations && logProb >= oldLogProb) {
                oldLogProb = logProb;
                logProb = this.gammaPass(this.estimatedTransitionMatrix, this.estimatedObservationMatrix, this.estimatedPi, emissions);
            } else {
                break;
            }
            iter++;
        }

/*
        System.out.println("Number of iterations: " + iter);

        Matrices.printMatrix(this.estimatedTransitionMatrix, "Transition Matrix");
        Matrices.printMatrix(this.estimatedObservationMatrix, "Observation Matrix");
*/
    }

    public double[][] alphaPass(double[][] transitionMatrix,
                                double[][] observationMatrix,
                                double[][] pi,
                                int[] emissions) {

        int idx = emissions[0];
        double[][] alpha = new double[N][T];
        c = new double[T];

        // Get first alpha and alphaPass
        for (int i=0; i < N; i++) {
            alpha[i][0] = observationMatrix[i][idx] * pi[0][i];
            c[0] += alpha[i][0];
        }

        c[0] = 1/c[0];
        for (int i=0; i < N; i++) {
            alpha[i][0] *= c[0];
        }

        // Get alpha matrix and alphaPass for time t: 1-k
        for (int t=1; t < T; t++) {
            c[t] = 0;
            for (int i=0; i < N; i++) {
                alpha[i][t] = 0;
                for (int j=0; j < N; j++) {
                    alpha[i][t] += transitionMatrix[j][i] * alpha[j][t-1];
                }
                alpha[i][t] *= observationMatrix[i][emissions[t]];
                c[t] += alpha[i][t];
            }

            c[t] = 1/c[t];
            for (int i=0; i < N; i++) {
                alpha[i][t] *= c[t];
            }
        }

        return alpha;
    }


    public double[][] betaPass(double[][] transitionMatrix,
                         double[][] observationMatrix,
                         double[][] pi,
                         int[] emissions) {

        double[][] deltaMatrix = new double[N][T]; // Matrix for delta probabilities

        // Get first delta vector
        for (int i=0; i < transitionMatrix.length; i++) {
            deltaMatrix[i][T-1] = c[T-1];
        }

        // Get delta vectors and states for t: 1-n
        for (int t=T-2; t >= 0; t--) {
            // previous delta_i vector per row * o_t per col * a_ji
            // then assign max value to the time step vector
            for (int i = 0; i < N; i++) {
                deltaMatrix[i][t] = 0;
                for (int j=0; j < N; j++) {
                    deltaMatrix[i][t] += deltaMatrix[j][t+1] * observationMatrix[j][emissions[t+1]] * transitionMatrix[i][j];
                }
                deltaMatrix[i][t] *= c[t];
            }
        }

        return deltaMatrix;
    }

    public double gammaPass(double[][] transitionMatrix,
                          double[][] observationMatrix,
                          double[][] pi,
                          int[] emissions) {

        double[][] gamma;
        double[][] diGamma;
        double[][] sumDiGamma;

        // Initialize lambda, gamma and diGamma
        double[][] alpha = alphaPass(transitionMatrix, observationMatrix, pi, emissions);
        double[][] beta = betaPass(transitionMatrix, observationMatrix, pi, emissions);
        gamma = new double[N][T];
        diGamma = new double[N][N];

        // Sum of all diGamma matrices
        sumDiGamma = new double[N][N];
        double logProb = this.c[0];

        double denom;
        for (int t=0; t < T-1; t++) {
            // Calculate the denominator for gamma and diGamma calculation
            denom = 0;
            for (int i=0; i < N; i++) {
                for (int j=0; j < N; j++) {
                    denom += alpha[i][t]
                            * transitionMatrix[i][j]
                            * observationMatrix[j][emissions[t+1]]
                            * beta[j][t+1];
                }
            }

            // Calculate gamma and diGamma
            for (int i=0; i < N; i++) {
                gamma[i][t] = 0;
                for (int j=0; j < N; j++) {
                    diGamma[i][j] = (
                            alpha[i][t]
                            * transitionMatrix[i][j]
                            * observationMatrix[j][emissions[t+1]]
                            * beta[j][t+1]
                            ) / denom;

                    gamma[i][t] += diGamma[i][j];
                    sumDiGamma[i][j] += diGamma[i][j];
                }
            }

            logProb += Math.log(this.c[t+1]);
        }

        denom = 0;
        for (int i=0; i < N; i++) {
            denom += alpha[i][T-1];
        }
        for (int i=0; i < N; i++) {
            gamma[i][T-1] = alpha[i][T-1] / denom;
        }


        this.estimatedTransitionMatrix = estimateTransitionMatrix(sumDiGamma, gamma);
        this.estimatedObservationMatrix = estimateObservationMatrix(gamma, emissions, observationMatrix);
        this.estimatedPi = estimatePi(gamma);

        return -logProb;
    }

    public double[][] estimatePi(double[][] gamma) {
        double[][] result = new double[1][N];
        for (int i=0; i < N; i++) {
            result[0][i] = gamma[i][0];
        }
        return result;
    }

    public double[][] estimateTransitionMatrix(double[][] diGammaSum, double[][] gamma) {
        double[][] result = new double[N][N];
        double sumGamma;
        for (int i=0; i < N; i++) {
            for (int j=0; j < N; j++) {
                sumGamma = 0;
                for (int t=0; t < T-1; t++) {
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
        for (int i=0; i < N; i++) {
            for (int j=0; j < M; j++) {
                numer = 0;
                denom = 0;
                for (int t=0; t < T; t++) {
                    if (emissions[t] == j) numer += gamma[i][t];
                    denom += gamma[i][t];
                }
                result[i][j] = numer / denom;
            }
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        /*
        String filepath1 = "./A1_HMM/resources/hmm4_01.in";
        String filepath2 = "./A1_HMM/resources/hmm4_02.in";
        String filepath3 = "./A1_HMM/resources/hmm4_03.in";
        Scanner sc = new Scanner(new File(filepath2));
        */
        Kattio sc = new Kattio(System.in);

        double[][] transitionMatrix;
        double[][] observationMatrix;
        double[][] pi;
        int[] emissions;

        int m;
        int n;

        m = sc.getInt();
        n = sc.getInt();
        transitionMatrix = new double[m][n];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
                transitionMatrix[i][j] = sc.getDouble();
            }
        }

        m = sc.getInt();
        n = sc.getInt();
        observationMatrix = new double[m][n];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
                observationMatrix[i][j] = sc.getDouble();
            }
        }

        m = sc.getInt();
        n = sc.getInt();
        pi = new double[m][n];
        for (int i=0; i < m; i++) {
            for (int j=0; j < n; j++) {
                pi[i][j] = sc.getDouble();
            }
        }

        n = sc.getInt();
        emissions = new int[n];
        for (int i=0; i < n; i++) {
            emissions[i] = sc.getInt();
        }

        new HMM3(transitionMatrix, observationMatrix, pi, emissions);
    }
}
