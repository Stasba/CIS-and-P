import java.util.*;
import java.util.regex.*;

public class Main {

    // ===== МОДЕЛЬ =====
    static class Model {
        public double evaluate(String expression) throws Exception {
            expression = expression.replaceAll("\\s+", "");
            if (!hasValidParentheses(expression)) {
                throw new Exception("Скобки расставлены некорректно.");
            }

            if (countOperands(expression) > 15) {
                throw new Exception("Превышено допустимое количество слагаемых (максимум 15).");
            }

            String converted = preprocess(expression);
            return evaluateExpression(converted);
        }

        // Проверка правильности скобок
        private boolean hasValidParentheses(String expr) {
            int balance = 0;
            for (char ch : expr.toCharArray()) {
                if (ch == '(') balance++;
                else if (ch == ')') balance--;
                if (balance < 0) return false;
            }
            return balance == 0;
        }

        // Подсчёт числа операндов (слагаемых)
        private int countOperands(String expr) {
            Matcher matcher = Pattern.compile("[+\\-*/^]?(?<![a-zA-Z])\\d+(\\.\\d+)?").matcher(expr);
            int count = 0;
            while (matcher.find()) count++;
            return count;
        }

        // Предобработка выражения: замена ** на ^, log на log2, exp оставляем
        private String preprocess(String expr) {
            expr = expr.replace("**", "^");
            expr = expr.replaceAll("log\\(", "log2(");
            expr = expr.replaceAll("exp\\(", "exp(");
            return expr;
        }

        // Оценка выражения: разбор и вычисление
        private double evaluateExpression(String expr) throws Exception {
            // Замена функций log2, exp и ! на числовые значения
            expr = replaceFunctions(expr);
            return new ExpressionParser().parse(expr);
        }

        // Обработка log2(), exp(), ! вручную
        private String replaceFunctions(String expr) throws Exception {
            // log2()
            Matcher logMatcher = Pattern.compile("log2\\(([^()]+)\\)").matcher(expr);
            while (logMatcher.find()) {
                String inside = logMatcher.group(1);
                double val = Math.log(Double.parseDouble(inside)) / Math.log(2);
                expr = expr.replace(logMatcher.group(), Double.toString(val));
                logMatcher = Pattern.compile("log2\\(([^()]+)\\)").matcher(expr);
            }

            // exp()
            Matcher expMatcher = Pattern.compile("exp\\(([^()]+)\\)").matcher(expr);
            while (expMatcher.find()) {
                String inside = expMatcher.group(1);
                double val = Math.exp(Double.parseDouble(inside));
                expr = expr.replace(expMatcher.group(), Double.toString(val));
                expMatcher = Pattern.compile("exp\\(([^()]+)\\)").matcher(expr);
            }

            // факториал (например, 5!)
            Matcher factMatcher = Pattern.compile("(\\d+)!").matcher(expr);
            while (factMatcher.find()) {
                int n = Integer.parseInt(factMatcher.group(1));
                long fact = factorial(n);
                expr = expr.replace(factMatcher.group(), Long.toString(fact));
                factMatcher = Pattern.compile("(\\d+)!").matcher(expr);
            }

            return expr;
        }

        // Вычисление факториала
        private long factorial(int n) throws Exception {
            if (n < 0) throw new Exception("Факториал только для неотрицательных чисел.");
            long result = 1;
            for (int i = 2; i <= n; i++) result *= i;
            return result;
        }

        // === Простой парсер арифметических выражений ===
        static class ExpressionParser {
            private int pos = -1, ch;
            private String input;

            public double parse(String str) throws Exception {
                input = str;
                pos = -1;
                nextChar();
                double x = parseExpression();
                if (pos < input.length()) throw new RuntimeException("Неожиданный символ: " + (char)ch);
                return x;
            }

            // Чтение следующего символа
            private void nextChar() {
                ch = (++pos < input.length()) ? input.charAt(pos) : -1;
            }

            // Проверка и "поедание" символа, если он ожидается
            private boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            // Парсинг выражения (суммирование и вычитание)
            private double parseExpression() throws Exception {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            // Парсинг термов (умножение и деление)
            private double parseTerm() throws Exception {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else if (eat('%')) x %= parseFactor();
                    else return x;
                }
            }

            // Парсинг факторов (числа, скобки, степень, знак)
            private double parseFactor() throws Exception {
                if (eat('+')) return parseFactor(); // унарный плюс
                if (eat('-')) return -parseFactor(); // унарный минус

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Пропущена закрывающая скобка");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(input.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Неожиданный символ: " + (char)ch);
                }

                while (eat('^')) {
                    x = Math.pow(x, parseFactor()); // обработка возведения в степень
                }

                return x;
            }
        }
    }

    // ===== ПРЕДСТАВЛЕНИЕ =====
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

    // ===== КОНТРОЛЛЕР =====
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

    // ===== ТОЧКА ВХОДА =====
    public static void main(String[] args) {
        Model model = new Model();
        View view = new View();
        Controller controller = new Controller(model, view);
        controller.run();
    }
}
