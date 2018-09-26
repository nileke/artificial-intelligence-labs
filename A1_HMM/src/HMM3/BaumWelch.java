package HMM3;

public class BaumWelch {
    private int N;
    private int M;
    private int T;

    private double[][] A;
    private double[][] B;
    private double[][] pi;
    private int[] emissions;

    private double[][] alpha;
    private double[][] beta;
    private double[][] gamma;
    private double[][] diGamma;

    private double[] scaleVector;


    public BaumWelch() {}

    public BaumWelch(double[][] A, double[][] B, double[][] pi, int[] emissions) {
        this.A = A;
        this.B = B;
        this.pi = pi;
        this.emissions = emissions;

        this.N = A.length;
        this.M = B[0].length;
        this.T = emissions.length;

    }

    public double[][] setAlphaPass() {
        if (this.A == null || this.B == null || this.pi == null || this.emissions == null) {
            throw new IllegalArgumentException("Missing class variables.");
        }
        return setAlphaPass(this.A, this.B, this.pi, this.emissions);
    }

    public double[][] setBetaPass() {
        if (this.A == null || this.B == null || this.pi == null || this.emissions == null) {
            throw new IllegalArgumentException("Missing class variables.");
        }
        return setBetaPass(this.A, this.B, this.emissions);
    }

    public double setGammaPass() {
        if (this.A == null || this.B == null || this.pi == null || this.emissions == null) {
            throw new IllegalArgumentException("Missing class variables.");
        }
        return setGammaPass(this.A, this.B, this.pi, this.emissions);
    }

    public double[][] setAlphaPass(double[][] transitionMatrix,
                                double[][] observationMatrix,
                                double[][] pi,
                                int[] emissions) {

        this.alpha = new double[N][T];
        this.scaleVector = new double[T];

        // Get first alpha and alphaPass
        for (int i=0; i < this.N; i++) {
            this.alpha[i][0] = observationMatrix[i][this.emissions[0]] * pi[0][i];
            scaleVector[0] += alpha[i][0];
        }

        this.scaleVector[0] = 1/scaleVector[0];
        for (int i=0; i < this.N; i++) {
            this.alpha[i][0] *= scaleVector[0];
        }

        // Get alpha matrix and alphaPass for time t: 1-k
        for (int t=1; t < this.T; t++) {
            this.scaleVector[t] = 0;
            for (int i=0; i < N; i++) {
                this.alpha[i][t] = 0;
                for (int j=0; j < this.N; j++) {
                    this.alpha[i][t] += transitionMatrix[j][i] * this.alpha[j][t-1];
                }
                this.alpha[i][t] *= observationMatrix[i][emissions[t]];
                this.scaleVector[t] += this.alpha[i][t];
            }

            this.scaleVector[t] = 1/scaleVector[t];
            for (int i=0; i < this.N; i++) {
                this.alpha[i][t] *= this.scaleVector[t];
            }
        }

        return alpha;
    }


    public double[][] setBetaPass(double[][] transitionMatrix,
                               double[][] observationMatrix,
                               int[] emissions) {

        double[][] beta = new double[this.N][this.T]; // Matrix for delta probabilities

        // Get first delta vector
        for (int i=0; i < transitionMatrix.length; i++) {
            beta[i][T-1] = scaleVector[T-1];
        }

        // Get delta vectors and states for t: 1-n
        for (int t=T-2; t >= 0; t--) {
            // previous delta_i vector per row * o_t per col * a_ji
            // then assign max value to the time step vector
            for (int i = 0; i < N; i++) {
                beta[i][t] = 0;
                for (int j=0; j < N; j++) {
                    beta[i][t] += beta[j][t+1] * observationMatrix[j][emissions[t+1]] * transitionMatrix[i][j];
                }
                beta[i][t] *= scaleVector[t];
            }
        }

        return beta;
    }

    public double setGammaPass(double[][] transitionMatrix,
                            double[][] observationMatrix,
                            double[][] pi,
                            int[] emissions) {

        // Initialize lambda, gamma and diGamma
        this.alpha = setAlphaPass(transitionMatrix, observationMatrix, pi, emissions);
        this.beta = setBetaPass(transitionMatrix, observationMatrix, emissions);
        this.gamma = new double[this.N][this.T];
        this.diGamma = new double[this.N][this.N];

        // Sum of all diGamma matrices
        double logProb = 0 - this.scaleVector[0];

        double denom;
        for (int t=0; t < T-1; t++) {
            // Calculate the denominator for gamma and diGamma calculation
            denom = 0;
            for (int i=0; i < N; i++) {
                for (int j=0; j < N; j++) {
                    denom += this.alpha[i][t]
                            * transitionMatrix[i][j]
                            * observationMatrix[j][emissions[t+1]]
                            * this.beta[j][t+1];
                }
            }

            // Calculate gamma and diGamma
            double res;
            for (int i=0; i < N; i++) {
                this.gamma[i][t] = 0;
                for (int j=0; j < N; j++) {
                    res = (
                            this.alpha[i][t]
                                    * transitionMatrix[i][j]
                                    * observationMatrix[j][emissions[t+1]]
                                    * beta[j][t+1]
                    ) / denom;

                    this.gamma[i][t] += res;
                    this.diGamma[i][j] += res;
                }
            }

            logProb -= Math.log(this.scaleVector[t+1]);
        }

        // Special case for T-1
        denom = 0;
        for (int i=0; i < N; i++) {
            denom += this.alpha[i][T-1];
        }
        for (int i=0; i < N; i++) {
            this.gamma[i][T-1] = this.alpha[i][T-1] / denom;
        }


        this.A = estimateNewA(diGamma, gamma);
        this.B = estimateNewB(gamma, emissions);
        this.pi = estimateNewPi(gamma);

        return logProb;
    }

    private double[][] estimateNewPi(double[][] gamma) {
        double[][] result = new double[1][N];
        for (int i=0; i < N; i++) {
            result[0][i] = gamma[i][0];
        }
        return result;
    }

    private double[][] estimateNewA(double[][] diGamma, double[][] gamma) {
        double[][] result = new double[N][N];
        double sumGamma;
        for (int i=0; i < N; i++) {
            for (int j=0; j < N; j++) {
                sumGamma = 0;
                for (int t=0; t < T-1; t++) {
                    sumGamma += gamma[i][t];
                }
                result[i][j] = diGamma[i][j] / sumGamma;
            }
        }

        return result;
    }

    private double[][] estimateNewB(double[][] gamma, int[] emissions) {

        double[][] result = new double[N][M];

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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int tm = this.A.length;
        int tn = this.A[0].length;

        int om = this.B.length;
        int on = this.B[0].length;

        sb.append(tm).append(" ").append(tn);

        for (int i=0; i < tm; i++) {
            for (int j=0; j < tn; j++) {
                sb.append(" ").append(this.A[i][j]);
            }
        }
        sb.append("\n");
        sb.append(om).append(" ").append(on);

        for (int i=0; i < om; i++) {
            for (int j=0; j < on; j++) {
                sb.append(" ").append(this.B[i][j]);
            }
        }

        return sb.toString();
    }
}
