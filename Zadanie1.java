import java.util.Arrays;
import java.util.Scanner;

public class Zadanie1 {
    public static int nextGreaterNumber(int n) {
        char[] digits = Integer.toString(n).toCharArray();
        int i = digits.length - 2;

        // Найти наибольший индекс i
        while (i >= 0 && digits[i] >= digits[i + 1]) {
            i--;
        }

        // Если такого индекса не существует, вернуть -1
        if (i == -1) {
            return -1;
        }

        // Найти наибольший индекс j
        int j = digits.length - 1;
        while (digits[j] <= digits[i]) {
            j--;
        }

        // Поменять местами i и j
        char temp = digits[i];
        digits[i] = digits[j];
        digits[j] = temp;

        // Перевернуть последовательность до конца массива
        Arrays.sort(digits, i + 1, digits.length);

        long result = Long.parseLong(new String(digits));
        return (result <= Integer.MAX_VALUE) ? (int) result : -1;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите целое положительное число: ");
        int n = scanner.nextInt();
        int result = nextGreaterNumber(n);
        System.out.println("Следующее большее число: " + result);
    }
}