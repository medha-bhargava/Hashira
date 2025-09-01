import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.*;

public class FindConstantC_Linear {

    // Solve system of linear equations using Gaussian elimination
    private static BigInteger[] solveSystem(BigInteger[][] A, BigInteger[] B, int k) {
        // Convert to rationals using double temporarily for simplicity
        double[][] matrix = new double[k][k+1];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                matrix[i][j] = A[i][j].doubleValue();
            }
            matrix[i][k] = B[i].doubleValue();
        }

        // Gaussian elimination
        for (int i = 0; i < k; i++) {
            // pivot
            int maxRow = i;
            for (int r = i+1; r < k; r++) {
                if (Math.abs(matrix[r][i]) > Math.abs(matrix[maxRow][i])) {
                    maxRow = r;
                }
            }
            double[] temp = matrix[i];
            matrix[i] = matrix[maxRow];
            matrix[maxRow] = temp;

            // normalize pivot row
            double pivot = matrix[i][i];
            for (int j = i; j <= k; j++) {
                matrix[i][j] /= pivot;
            }

            // eliminate
            for (int r = 0; r < k; r++) {
                if (r != i) {
                    double factor = matrix[r][i];
                    for (int j = i; j <= k; j++) {
                        matrix[r][j] -= factor * matrix[i][j];
                    }
                }
            }
        }

        // Extract solution
        BigInteger[] coeffs = new BigInteger[k];
        for (int i = 0; i < k; i++) {
            coeffs[i] = BigInteger.valueOf(Math.round(matrix[i][k]));
        }
        return coeffs;
    }

    private static void processFile(String filePath) throws Exception {
        // Read file
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line.trim());
        }
        String json = sb.toString();

        // Extract n and k
        int n = Integer.parseInt(json.replaceAll(".*\"n\"\\s*:\\s*(\\d+).*", "$1"));
        int k = Integer.parseInt(json.replaceAll(".*\"k\"\\s*:\\s*(\\d+).*", "$1"));

        int[] xs = new int[n];
        BigInteger[] ys = new BigInteger[n];

        // Regex to match entries
        Pattern p = Pattern.compile("\"(\\d+)\"\\s*:\\s*\\{\\s*\"base\"\\s*:\\s*\"(\\d+)\",\\s*\"value\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");
        Matcher m = p.matcher(json);

        int idx = 0;
        while (m.find()) {
            int x = Integer.parseInt(m.group(1));
            int base = Integer.parseInt(m.group(2));
            String value = m.group(3);
            xs[idx] = x;
            ys[idx] = new BigInteger(value, base);
            idx++;
        }

        // Build system of equations using first k points
        BigInteger[][] A = new BigInteger[k][k];
        BigInteger[] B = new BigInteger[k];

        for (int i = 0; i < k; i++) {
            int x = xs[i];
            BigInteger y = ys[i];
            for (int j = 0; j < k; j++) {
                A[i][j] = BigInteger.valueOf((long) Math.pow(x, k - 1 - j));
            }
            B[i] = y;
        }

        // Solve system
        BigInteger[] coeffs = solveSystem(A, B, k);

        // Constant is the last coefficient
        BigInteger c = coeffs[k - 1];
        System.out.println("File: " + filePath + " => Constant c = " + c);
    }

    public static void main(String[] args) throws Exception {
        processFile("input1.json");
        processFile("input2.json");
    }
}
