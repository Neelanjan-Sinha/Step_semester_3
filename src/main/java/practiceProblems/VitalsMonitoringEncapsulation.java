import java.util.Arrays;

public class Main {

    static class PatientVitals {

        // All fields are private
        private double[] readings;
        private int count;

        // Maximum 500 readings
        private static final int MAX_READINGS = 500;


        // Constructor
        public PatientVitals(double[] initialReadings) {

            readings = new double[MAX_READINGS];
            count = 0;

            if (initialReadings != null) {

                for (double value : initialReadings) {
                    recordReading(value);
                }
            }
        }


        // Adds a reading only if it is valid
        public void recordReading(double value) {

            // Reject invalid readings
            if (value <= 0 || value > 45) {
                return;
            }

            // Do not exceed 500 readings
            if (count >= MAX_READINGS) {
                return;
            }

            readings[count] = value;
            count++;
        }


        // Calculates average
        public double getAverage() {

            if (count == 0) {
                return 0.0;
            }

            double sum = 0;

            for (int i = 0; i < count; i++) {
                sum += readings[i];
            }

            return sum / count;
        }


        // Returns a defensive copy
        public double[] getAllReadings() {

            return Arrays.copyOf(readings, count);
        }
    }


    public static void main(String[] args) {

        // Example 1
        PatientVitals v = new PatientVitals(
                new double[]{36.5, -2, 37.1}
        );

        System.out.println(
                Arrays.toString(v.getAllReadings())
        );


        // Example 2
        double[] copy = v.getAllReadings();

        copy[0] = 999;

        System.out.println(
                v.getAllReadings()[0]
        );
    }
}
