import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class Calculator {

    private static final String HISTORY_FILE = "history.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Загрузка истории
        List<String> history = loadHistory();

        // Основной цикл
        while (true) {
            System.out.print("Введите уравнение или 'exit' для выхода: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.equalsIgnoreCase("history")) {
                printHistory(history);
                continue;
            }

            try {
                double result = evaluateExpression(input);
                System.out.println("Результат: " + result);

                // Сохранение выражения и результата в историю
                String historyEntry = input + " = " + result;
                history.add(historyEntry);
                saveHistory(history);

            } catch (Exception e) {
                System.out.println("Ошибка в выражении: " + e.getMessage());
            }
        }

        scanner.close();
    }

    // Загрузка истории из файла
    private static List<String> loadHistory() {
        List<String> history = new ArrayList<>();
        try {
            Path path = Paths.get(HISTORY_FILE);
            if (Files.exists(path)) {
                history = Files.readAllLines(path);
            }
        } catch (IOException e) {
            System.out.println("Не удалось загрузить историю: " + e.getMessage());
        }
        return history;
    }

    // Сохранение истории в файл
    private static void saveHistory(List<String> history) {
        try {
            Files.write(Paths.get(HISTORY_FILE), history);
        } catch (IOException e) {
            System.out.println("Не удалось сохранить историю: " + e.getMessage());
        }
    }

    // Вывод истории
    private static void printHistory(List<String> history) {
        if (history.isEmpty()) {
            System.out.println("История пуста.");
        } else {
            System.out.println("История вычислений:");
            for (String entry : history) {
                System.out.println(entry);
            }
        }
    }

    // Вычисление выражения
    private static double evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s+", "");
        List<String> tokens = tokenize(expression);
        Queue<String> rpn = infixToRPN(tokens);
        return evaluateRPN(rpn);
    }

    // Разбиение строки на токены
    private static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("([+\\-*/%^()|]|//|\\d+\\.?\\d*|\\d*\\.?\\d+)").matcher(expression);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    // Преобразование инфиксного выражения в обратную польскую нотацию (ОПН)
    private static Queue<String> infixToRPN(List<String> tokens) {
        Map<String, Integer> precedence = new HashMap<>();
        precedence.put("|", 4);
        precedence.put("^", 3);
        precedence.put("*", 2);
        precedence.put("/", 2);
        precedence.put("//", 2);
        precedence.put("%", 2);
        precedence.put("+", 1);
        precedence.put("-", 1);

        Queue<String> output = new LinkedList<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (token.matches("\\d+\\.?\\d*")) {
                output.add(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                operators.pop();
            } else {
                while (!operators.isEmpty() && precedence.getOrDefault(operators.peek(), 0) >= precedence.getOrDefault(token, 0)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }

        return output;
    }

    // Вычисление выражения в ОПН
    private static double evaluateRPN(Queue<String> rpn) {
        Stack<Double> stack = new Stack<>();

        while (!rpn.isEmpty()) {
            String token = rpn.poll();
            if (token.matches("\\d+\\.?\\d*")) {
                stack.push(Double.parseDouble(token));
            } else {
                double b = stack.pop();
                double a = stack.isEmpty() ? 0 : stack.pop();

                switch (token) {
                    case "+":
                        stack.push(a + b);
                        break;
                    case "-":
                        stack.push(a - b);
                        break;
                    case "*":
                        stack.push(a * b);
                        break;
                    case "/":
                        stack.push(a / b);
                        break;
                    case "//":
                        stack.push((double) ((long) a / (long) b));
                        break;
                    case "%":
                        stack.push(a % b);
                        break;
                    case "^":
                        stack.push(Math.pow(a, b));
                        break;
                    case "|":
                        stack.push(Math.abs(b));
                        break;
                }
            }
        }

        return stack.pop();
    }
}
