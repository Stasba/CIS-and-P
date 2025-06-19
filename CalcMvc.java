import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class CalcMVC {

    // Главный метод, с которого начинается выполнение программы.
    public static void main(String[] args) {
        // объекты Модель, Представление, Контроллер.
        CalculatorModel model = new CalculatorModel();
        CalculatorView view = new CalculatorView();
        CalculatorController controller = new CalculatorController(model, view);

        // Главный цикл работы калькулятора.
        controller.runApplication();
    }

    static class CalculatorModel {
        // Имя файла, где по умолчанию будет храниться история.
        private static final String HISTORY_FILE_NAME = "calculator_history.txt";
        // Список (List) для хранения истории вычислений в памяти.
        private final List<CalculationEntry> history;
        // Карта (Map) для хранения приоритетов операторов.
        private static final Map<String, Integer> OPERATOR_PRECEDENCE = Map.of(
                "+", 1, "-", 1,
                "*", 2, "/", 2, "%", 2, "//", 2,
                "^", 3 // '^' и '**' будут иметь самый высокий приоритет
        );

        // Конструктор
        public CalculatorModel() {
            this.history = new ArrayList<>(); // Создаем пустой список для истории
            loadHistoryFromFile(); // История из файла при запуске
        }

        static class CalculationEntry {
            private final String expression;
            private final String result;

            public CalculationEntry(String expression, String result) {
                this.expression = expression;
                this.result = result;
            }

            @Override
            public String toString() {
                return this.expression + " = " + this.result;
            }
        }

        public String evaluate(String expression) {
            try {
                // 1. Преобразуем выражение в более простой формат ОПН
                List<String> rpnExpression = infixToRpn(expression);
                // 2. Вычисляем результат из ОПН
                double resultValue = evaluateRpn(rpnExpression);

                // Форматируем результат, чтобы убрать ".0" у целых чисел
                String resultString;
                if (resultValue == (long) resultValue) {
                    resultString = String.format("%d", (long) resultValue);
                } else {
                    resultString = String.format("%s", resultValue);
                }

                // 3. Сохраняем в историю и в файл
                history.add(new CalculationEntry(expression, resultString));
                saveHistoryToFile();

                return resultString;

            } catch (Exception e) {
                // Если что-то пошло не так, возвращаем сообщение об ошибке.
                return "Ошибка: " + e.getMessage();
            }
        }


        private List<String> infixToRpn(String expression) {
            // Заменяем все варианты операторов на один
            expression = expression.replace("**", "^").replace(",", ".");

            List<String> outputQueue = new ArrayList<>();
            Stack<String> operatorStack = new Stack<>();
            // Разбиваем строку на числа и операторы
            String[] tokens = expression.split("(?<=[-+*/%^()])|(?=[-+*/%^()])");

            for (String token : tokens) {
                token = token.trim();
                if (token.isEmpty()) continue;

                if (isNumber(token)) {
                    outputQueue.add(token);
                } else if (token.equals("(")) {
                    operatorStack.push(token);
                } else if (token.equals(")")) {
                    while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                        outputQueue.add(operatorStack.pop());
                    }
                    if (operatorStack.isEmpty()) throw new IllegalArgumentException("Несогласованные скобки");
                    operatorStack.pop(); // Выкидываем открывающую скобку
                } else { // Это оператор
                    while (!operatorStack.isEmpty() && isOperator(operatorStack.peek()) &&
                            OPERATOR_PRECEDENCE.get(operatorStack.peek()) >= OPERATOR_PRECEDENCE.get(token)) {
                        outputQueue.add(operatorStack.pop());
                    }
                    operatorStack.push(token);
                }
            }

            while (!operatorStack.isEmpty()) {
                if (operatorStack.peek().equals("(")) throw new IllegalArgumentException("Несогласованные скобки");
                outputQueue.add(operatorStack.pop());
            }

            return outputQueue;
        }

        private double evaluateRpn(List<String> rpnTokens) {
            Stack<Double> valueStack = new Stack<>();

            for (String token : rpnTokens) {
                if (isNumber(token)) {
                    valueStack.push(Double.parseDouble(token));
                } else if (isOperator(token)) {
                    if (valueStack.size() < 2) throw new IllegalArgumentException("Недостаточно операндов для операции");
                    double rightOperand = valueStack.pop();
                    double leftOperand = valueStack.pop();
                    double result = 0;

                    switch (token) {
                        case "+": result = leftOperand + rightOperand; break;
                        case "-": result = leftOperand - rightOperand; break;
                        case "*": result = leftOperand * rightOperand; break;
                        case "/":
                            if (rightOperand == 0) throw new ArithmeticException("Деление на ноль");
                            result = leftOperand / rightOperand;
                            break;
                        case "%": result = leftOperand % rightOperand; break;
                        case "^": result = Math.pow(leftOperand, rightOperand); break;
                        case "//":
                            if (rightOperand == 0) throw new ArithmeticException("Деление на ноль");
                            result = Math.floor(leftOperand / rightOperand);
                            break;
                    }
                    valueStack.push(result);
                }
            }
            if (valueStack.size() != 1) throw new IllegalArgumentException("Некорректное выражение");
            return valueStack.pop();
        }

        // Методы для проверки, является ли токен числом или оператором
        private boolean isNumber(String token) {
            try {
                Double.parseDouble(token);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        private boolean isOperator(String token) {
            return OPERATOR_PRECEDENCE.containsKey(token);
        }

        public List<CalculationEntry> getHistory() {
            return this.history;
        }


        private void loadHistoryFromFile() {
            File historyFile = new File(HISTORY_FILE_NAME);
            if (!historyFile.exists()) {
                return; // Если файла нет, ничего не делаем
            }

            // Автоматически закроет reader
            try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Разделяем строку "5 + 3 = 8" на две части по знаку " = "
                    String[] parts = line.split(" = ", 2);
                    if (parts.length == 2) {
                        history.add(new CalculationEntry(parts[0], parts[1]));
                    }
                }
            } catch (IOException e) {
                System.err.println("Не удалось загрузить историю: " + e.getMessage());
            }
        }

        private void saveHistoryToFile() {
            // Используем метод для записи, передавая ему путь по умолчанию и всю историю
            writeEntriesToFile(new File(HISTORY_FILE_NAME), this.history);
        }

        public String exportHistory(String userInput) {
            File targetFile = determineTargetFile(userInput);
            return writeEntriesToFile(targetFile, this.history);
        }

        public String exportSelectedHistory(List<Integer> indexes, String userInput) {
            File targetFile = determineTargetFile(userInput);

            // Собираются выбранные записи с помощью обычного цикла for
            List<CalculationEntry> selectedEntries = new ArrayList<>();
            for (int index : indexes) {
                if (index >= 0 && index < history.size()) {
                    selectedEntries.add(history.get(index));
                }
            }

            if (selectedEntries.isEmpty()) {
                return "Не выбрано ни одной корректной записи для экспорта.";
            }

            return writeEntriesToFile(targetFile, selectedEntries);
        }

        private File determineTargetFile(String userInput) {
            // Пользователь ничего не ввел
            if (userInput == null || userInput.trim().isEmpty()) {
                return new File(HISTORY_FILE_NAME);
            }

            File file = new File(userInput);

            // Указан абсолютный путь с именем файла (например, C:\Users\Admin\doc.txt)
            if (file.isAbsolute() && !file.isDirectory()) {
                return file;
            }

            // Указан путь к папке (например, C:\Users\Admin или my_logs/)
            if (file.isDirectory()) {
                return new File(file, "log.log");
            }

            // Указано только имя файла или относительный путь
            return file;
        }

        private String writeEntriesToFile(File file, List<CalculationEntry> entries) {
            // Создание родительских папок, если их не существует
            File parentDir = file.getParentFile();
            if (parentDir != null) {
                parentDir.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (CalculationEntry entry : entries) {
                    writer.write(entry.toString());
                    writer.newLine(); // Переход на новую строку
                }
                // Возвращает полный путь к файлу
                return "Файл успешно сохранен: " + file.getAbsolutePath();
            } catch (IOException e) {
                return "Ошибка при сохранении файла: " + e.getMessage();
            }
        }
    }

    // Представление
    static class CalculatorView {
        // Scanner используется для чтения ввода пользователя из консоли.
        private final Scanner scanner;

        public CalculatorView() {
            this.scanner = new Scanner(System.in);
        }

        public void displayMenu() {
            System.out.println("\n--- Калькулятор ---");
            System.out.println("1. Ввести выражение для расчета");
            System.out.println("2. Посмотреть историю");
            System.out.println("3. Сохранить всю историю в файл");
            System.out.println("4. Сохранить часть истории в файл");
            System.out.println("5. Выход");
            System.out.print("Ваш выбор: ");
        }

        public String getUserInput(String prompt) {
            System.out.print(prompt);
            return scanner.nextLine();
        }

        public void showMessage(String message) {
            System.out.println(message);
        }

        public void showResult(String result) {
            System.out.println("-> Результат: " + result);
        }

        public void showHistory(List<CalculatorModel.CalculationEntry> history) {
            if (history.isEmpty()) {
                System.out.println("История вычислений пуста.");
            } else {
                System.out.println("\n--- История ---");
                for (int i = 0; i < history.size(); i++) {
                    System.out.println(i + ") " + history.get(i).toString());
                }
                System.out.println("---------------");
            }
        }

        public List<Integer> getSelectedIndexesFromUser(int historySize) {
            List<Integer> selectedIndexes = new ArrayList<>();
            if (historySize == 0) {
                showMessage("История пуста, выбирать нечего.");
                return selectedIndexes;
            }

            showMessage("Введите номера записей для сохранения через запятую (например: 0, 2, 5)");
            String input = getUserInput("Номера: ");

            String[] parts = input.split(","); // Разделяем строку "0, 2, 5" на массив ["0", " 2", " 5"]

            for (String part : parts) {
                try {
                    // Убираем пробелы и преобразуем строку в число
                    int index = Integer.parseInt(part.trim());
                    // Проверка номера
                    if (index >= 0 && index < historySize) {
                        selectedIndexes.add(index);
                    } else {
                        showMessage("Номер " + index + " вне диапазона истории. Пропущен.");
                    }
                } catch (NumberFormatException e) {
                    // Если введено не число, сообщаем об этом
                    showMessage("'" + part + "' не является числом. Пропущено.");
                }
            }
            return selectedIndexes;
        }
    }
    // Контроллер
    static class CalculatorController {
        private final CalculatorModel model;
        private final CalculatorView view;

        public CalculatorController(CalculatorModel model, CalculatorView view) {
            this.model = model;
            this.view = view;
        }

        // Главный метод, управляющий работой калькулятора.
        public void runApplication() {
            boolean isRunning = true;
            // Бесконечный цикл, который прервется только при выборе "Выход".
            while (isRunning) {
                view.displayMenu();
                String choice = view.getUserInput("");

                switch (choice) {
                    case "1":
                        calculate();
                        break;
                    case "2":
                        displayHistory();
                        break;
                    case "3":
                        exportAllHistory();
                        break;
                    case "4":
                        exportSelected();
                        break;
                    case "5":
                        isRunning = false; // Завершаем цикл
                        view.showMessage("Программа завершена.");
                        break;
                    default:
                        view.showMessage("Неверный ввод. Пожалуйста, выберите от 1 до 5.");
                        break;
                }
            }
        }

        private void calculate() {
            String expression = view.getUserInput("Введите выражение: ");
            if (expression.trim().isEmpty()) {
                view.showMessage("Выражение не может быть пустым.");
                return;
            }
            String result = model.evaluate(expression);
            view.showResult(result);
        }

        private void displayHistory() {
            view.showHistory(model.getHistory());
        }

        private void exportAllHistory() {
            view.showMessage("Экспорт всей истории.");
            view.showMessage("Оставьте поле пустым, чтобы увидеть путь к файлу истории по умолчанию.");
            String path = view.getUserInput("Введите имя файла или путь для сохранения: ");

            String message;
            if (path.trim().isEmpty()) {
                File defaultFile = new File(CalculatorModel.HISTORY_FILE_NAME);
                message = "Файл истории по умолчанию: " + defaultFile.getAbsolutePath();
            } else {
                message = model.exportHistory(path);
            }
            view.showMessage(message);
        }

        private void exportSelected() {
            view.showMessage("Экспорт выбранных записей.");
            displayHistory();

            List<Integer> indexes = view.getSelectedIndexesFromUser(model.getHistory().size());

            if (indexes.isEmpty()) {
                view.showMessage("Не выбрано ни одной записи. Экспорт отменен.");
                return;
            }

            String path = view.getUserInput("Введите имя файла или путь для сохранения: ");
            String message = model.exportSelectedHistory(indexes, path);
            view.showMessage(message);
        }
    }
}
