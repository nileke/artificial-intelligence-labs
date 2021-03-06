package HMM3;

import utils.Kattio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class HMM3 {

    public HMM3(double[][] transitionMatrix,
                double[][] observationMatrix,
                double[][] pi,
                int[] emissions) {


        System.out.println(this.compute(transitionMatrix, observationMatrix, pi, emissions));
    }


    public String compute(double[][] transitionMatrix,
                        double[][] observationMatrix,
                        double[][] pi,
                        int[] emissions) {

        int iter = 1;
        double oldLogProb = -Integer.MAX_VALUE;
        int maxIterations = 1000;

        //double logProb = this.gammaPass(transitionMatrix, observationMatrix, pi, emissions);
        BaumWelch bw = new BaumWelch(transitionMatrix, observationMatrix, pi, emissions);
        double logProb = bw.setGammaPass();
        while (true) {
            if (iter < maxIterations && logProb >= oldLogProb) {
                oldLogProb = logProb;
                logProb = bw.setGammaPass();
            } else {
                break;
            }
            iter++;
        }

        System.out.println("Number of iterations: " + iter);
        return bw.toString();
/*
        System.out.println("Number of iterations: " + iter);

        Matrices.printMatrix(this.estimatedTransitionMatrix, "Transition Matrix");
        Matrices.printMatrix(this.estimatedObservationMatrix, "Observation Matrix");
*/
    }


    public static void main(String[] args) throws FileNotFoundException {

        String filepath1 = "./A1_HMM/resources/hmm4_01.in";
        String filepath2 = "./A1_HMM/resources/hmm4_02.in";
        String filepath3 = "./A1_HMM/resources/hmm4_03.in";
        Kattio sc = new Kattio(new FileInputStream(filepath1));

        //Kattio sc = new Kattio(System.in);

        double[][] transitionMatrix;
        double[][] observationMatrix;
        double[][] pi;
        int[] emissions;

        int m;
        int n;

        m = sc.getInt();
        n = sc.getInt();
        transitionMatrix = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                transitionMatrix[i][j] = sc.getDouble();
            }
        }

        m = sc.getInt();
        n = sc.getInt();
        observationMatrix = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                observationMatrix[i][j] = sc.getDouble();
            }
        }

        m = sc.getInt();
        n = sc.getInt();
        pi = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
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
