package C;

import utils.Kattio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        String filepath1 = "./A1_HMM/resources/hmm_c_N1000.in";
        String filepath2 = "./A1_HMM/resources/hmm_c_N10000.in";
        String fileInit = "./A1_HMM/resources/hmm_c_init.in";
        String fileInit8 = "./A1_HMM/resources/hmm_c_init_8.in";

        Kattio sc = new Kattio(new FileInputStream(fileInit8));


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

        sc = new Kattio(new FileInputStream(filepath2));

        n = sc.getInt();
        n = 10000;
        emissions = new int[n];
        for (int i=0; i < n; i++) {
            emissions[i] = sc.getInt();
        }

        new TrainHMM(transitionMatrix, observationMatrix, pi, emissions);

    }
}

