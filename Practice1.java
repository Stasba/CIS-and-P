import java.util.*;
import java.util.regex.*;

public class Practice1 {

    
    static class Model {
        public double evaluate(String expression) throws Exception {
            if (!expression.matches("^-?\\d+(\\.\\d+)?[\\s\\S]*\\d(\\.\\d+)?$")) {
                throw new Exception("Уравнение должно начинаться и заканчиваться числом.");
            }

            List<String> tokens = tokenize(expression.replaceAll("\\s+", ""));
            if (tokens.size() > 199) {
                throw new Exception("Слишком много операций. Максимум — 100.");
            }

            return calculate(tokens);
        }

        // Поддержка операций ^, *, /, //, +, -
        private double calculate(List<String> tokens) throws Exception {
            Stack<Double> values = new Stack<>();
            Stack<String> ops = new Stack<>();

            Map<String, Integer> precedence = Map.of(
                    "^", 4,
                    "*", 3,
                    "/", 3,
                    "//", 3,
                    "+", 2,
                    "-", 2
            );

            for (int i = 0; i < tokens.size(); i++) {
                String token = tokens.get(i);
                if (isNumber(token)) {
                    values.push(Double.parseDouble(token));
                } else {
                    while (!ops.isEmpty() && precedence.get(token) <= precedence.get(ops.peek())) {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                    }
                    ops.push(token);
                }
            }

            while (!ops.isEmpty()) {
                values.push(applyOp(ops.pop(), values.pop(), values.pop()));
            }

            return values.pop();
        }

        private List<String> tokenize(String expr) {
            List<String> tokens = new ArrayList<>();
            Matcher matcher = Pattern.compile("(//)|[+\\-*/^]|\\d+(\\.\\d+)?").matcher(expr);
            while (matcher.find()) {
                tokens.add(matcher.group());
            }
            return tokens;
        }

        private boolean isNumber(String token) {
            return token.matches("-?\\d+(\\.\\d+)?");
        }

        private double applyOp(String op, double b, double a) throws Exception {
            return switch (op) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> a / b;
                case "//" -> (double)((long)a / (long)b);
                case "^" -> Math.pow(a, b);
                default -> throw new Exception("Неподдерживаемая операция: " + op);
            };
        }
    }

    // Представление
    static class View {
        Scanner scanner = new Scanner(System.in);

        public String getExpression() {
            System.out.print("Введите математическое выражение: ");
            return scanner.nextLine();
        }

        public void showResult(double result) {
            System.out.println("Результат: " + result);
        }

        public void showError(String message) {
            System.out.println("Ошибка: " + message);
        }
    }

    // Контроллер
    static class Controller {
        private final Model model;
        private final View view;

        public Controller(Model model, View view) {
            this.model = model;
            this.view = view;
        }

        public void run() {
            String expr = view.getExpression();
            try {
                double result = model.evaluate(expr);
                view.showResult(result);
            } catch (Exception e) {
                view.showError(e.getMessage());
            }
        }
    }

    // MAIN
    public static void main(String[] args) {
        Model model = new Model();
        View view = new View();
        Controller controller = new Controller(model, view);
        controller.run();
    }
}
