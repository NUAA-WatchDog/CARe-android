package cn.zjt.iot.oncar.android.Thread.LocalThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;

class svm_predict {

    private static svm_print_interface svm_print_null = new svm_print_interface() {
        public void print(String s) {
        }
    };

    private static svm_print_interface svm_print_stdout = new svm_print_interface() {
        public void print(String s) {
            System.out.print(s);
        }
    };

    private static svm_print_interface svm_print_string = svm_print_stdout;

    static void info(String s) {
        svm_print_string.print(s);
    }

    private static double atof(String s) {
        return Double.valueOf(s).doubleValue();
    }

    private static int atoi(String s) {
        return Integer.parseInt(s);
    }

    private static double predict(String input, svm_model model, int predict_probability) throws IOException {
        int correct = 0;
        int total = 0;
        double error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

        int svm_type = svm.svm_get_svm_type(model);

        StringTokenizer st = new StringTokenizer(input, " \t\n\r\f:");

        double target = atof(st.nextToken());
        int m = st.countTokens() / 2;
        svm_node[] x = new svm_node[m];
        for (int j = 0; j < m; j++) {
            x[j] = new svm_node();
            x[j].index = atoi(st.nextToken());
            x[j].value = atof(st.nextToken());
        }

        double v = svm.svm_predict(model, x);

        if (v == target)
            ++correct;
        error += (v - target) * (v - target);
        sumv += v;
        sumy += target;
        sumvv += v * v;
        sumyy += target * target;
        sumvy += v * target;
        ++total;

        if (svm_type == svm_parameter.EPSILON_SVR ||
                svm_type == svm_parameter.NU_SVR) {
            svm_predict.info("Mean squared error = " + error / total + " (regression)\n");
            svm_predict.info("Squared correlation coefficient = " +
                    ((total * sumvy - sumv * sumy) * (total * sumvy - sumv * sumy)) /
                            ((total * sumvv - sumv * sumv) * (total * sumyy - sumy * sumy)) +
                    " (regression)\n");
        } else
            svm_predict.info("Accuracy = " + (double) correct / total * 100 +
                    "% (" + correct + "/" + total + ") (classification)\n");

        return v;
    }

    private static void exit_with_help() {
        System.err.print("usage: svm_predict [options] test_file model_file output_file\n"
                + "options:\n"
                + "-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n"
                + "-q : quiet mode (no outputs)\n");
        System.exit(1);
    }

    public static double main(File fileDir, String modelFileName, String input) throws IOException {
        int predict_probability = 0;
        double output = 0.0;
        svm_print_string = svm_print_stdout;

        try {
            svm_model model = svm.svm_load_model(new BufferedReader(new FileReader(new File(fileDir, modelFileName))));
            if (model == null) {
                System.err.print("can't open model file " + modelFileName + "\n");
                System.exit(1);
            }

            if (svm.svm_check_probability_model(model) != 0) {
                svm_predict.info("Model supports probability estimates, but disabled in prediction.\n");
            }

            output = predict(input, model, predict_probability);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            exit_with_help();
        } catch (ArrayIndexOutOfBoundsException e) {
            exit_with_help();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }
}
