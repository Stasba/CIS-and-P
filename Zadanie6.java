import java.util.Scanner;

public class Zadanie6 {

    public static String sumStrings(String num1, String num2) {
        // Строки в числа типа long
        long number1 = Long.parseLong(num1);
        long number2 = Long.parseLong(num2);

        // Сложение чисел
        long sum = number1 + number2;

        // Результат в строку
        return Long.toString(sum);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ввод чисел
        System.out.print("Введите первое число: ");
        String num1 = scanner.nextLine().trim();

        System.out.print("Введите второе число: ");
        String num2 = scanner.nextLine().trim();

        // Вызов функции сложения чисел
        String result = sumStrings(num1, num2);

        // Результат
        System.out.println("Сумма чисел: " + result);

        scanner.close();
    }
}