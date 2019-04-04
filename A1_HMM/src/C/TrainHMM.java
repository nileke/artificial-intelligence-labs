package C;

import HMM3.BaumWelch;

import java.util.Arrays;

public class TrainHMM {

    public TrainHMM(double[][] transitionMatrix, double[][] observationMatrix, double[][] pi, int[] emissions) {
        this.compute(transitionMatrix, observationMatrix, pi, emissions);
    }


    public void compute(double[][] transitionMatrix, double[][] observationMatrix, double[][] pi, int[] emissions) {

        BaumWelch bw = new BaumWelch(transitionMatrix, observationMatrix, pi, emissions);

        double oldLogProb = -Integer.MAX_VALUE;

        int maxIterations = 100000;
        int iter = 0;
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


        System.out.println(bw.toString());
        System.out.println(bw.getPi().length + " " + + bw.getPi()[0].length + " " + Arrays.toString(bw.getPi()[0]).replace("[", "")
                        .replace("]", "")
                        .replace(",",""));
    }
}
