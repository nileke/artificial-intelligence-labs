package HMM3;

// import for printing
import utils.Matrices;

public class HMM3 {
    double[][] transitionMatrix;
    double[][] observationMatrix;
    double[][] pi;
    int[] emissions;

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

        double[] o_t = observationMatrix[emissions[0]]; // First observation
        int numOfEmissions = emissions.length;
        double[][] deltaMatrix = new double[transitionMatrix.length][numOfEmissions]; // Matrix for delta probabilities
        //int[][] deltaIdxMatrix = new int[transitionMatrix.length][numOfEmissions]; // matrix for states

        // Get first delta vector
        for (int i=0; i < transitionMatrix.length; i++) {
            deltaMatrix[i][0] = pi[0][i] * o_t[i];
        }

        // Get delta vectors and states for t: 1-n
        for (int t=1; t < numOfEmissions; t++) {
            // Get observation o at time t
            o_t = observationMatrix[emissions[t]];

            // previous delta_i vector per row * o_t per col * a_nm
            // then assign max value to the time step vector
            for (int i = 0; i < transitionMatrix.length; i++) {
                //int stateMax = -1;
                double probabilityMax = -1;
                for (int j=0; j < transitionMatrix.length; j++) {
                    double res = deltaMatrix[j][t-1] * o_t[i] * transitionMatrix[j][i];
                    if (probabilityMax < res) {
                        probabilityMax = res;
                        //stateMax = j;
                    }
                }
                deltaMatrix[i][t] = probabilityMax;
                //deltaIdxMatrix[i][t] = stateMax;
            }
        }

        // Look for the highest value at the last time step t
        /*
        double probabilityMax = -1;
        int stateMax = -1;
        for (int m = 0; m < transitionMatrix.length; m++) {
            if (deltaMatrix[m][numOfEmissions-1] > probabilityMax) {
                stateMax = m;
                probabilityMax = deltaMatrix[m][numOfEmissions-1];
            }
        }
        */

        // Get the state sequence
        /*
        int[] sequence = new int[numOfEmissions];
        sequence[numOfEmissions-1] = stateMax;
        for (int i=numOfEmissions-1; i > 0; i--) {
            sequence[i-1] = deltaIdxMatrix[sequence[i]][i];
        }
        */

        return deltaMatrix;

    }

    public void gammaPass(double[][] transitionMatrix,
                          double[][] observationMatrix,
                          double[][] pi,
                          int[] emissions) {

    }

    public static void main(String[] args) {

    }
}
