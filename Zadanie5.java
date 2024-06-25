public class Zadanie5 {

    public static int maxPathSum(int[][] pyramid) {
        int n = pyramid.length;

        // Массив для хранения максимальных сумм
        int[][] maxSum = new int[n][n];

        // Начинается с последней строки пирамиды
        for (int i = 0; i < n; i++) {
            maxSum[n - 1][i] = pyramid[n - 1][i]; // максимальная сумма равна значению самой нижней строки
        }

        // Вверх по пирамиде
        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                // Максимальная сумма между двумя соседними элементами в следующей строке
                maxSum[i][j] = pyramid[i][j] + Math.max(maxSum[i + 1][j], maxSum[i + 1][j + 1]);
            }
        }

        // Максимальная сумму от вершины до основания пирамиды
        return maxSum[0][0];
    }

    public static void main(String[] args) {
        int[][] pyramid = {
            {3},
            {7, 4},
            {2, 4, 6},
            {8, 5, 9, 3}
        };

        int maxSum = maxPathSum(pyramid);
        System.out.println("Максимальная сумма скольжения по пирамиде: " + maxSum); // результат: 23
    }
}