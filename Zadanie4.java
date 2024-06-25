import java.util.*;

public class Zadanie4 {

    public static int countNumbers(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Интервалы по начальному числу
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

        int totalCount = 0;
        int currentStart = intervals[0][0];
        int currentEnd = intervals[0][1];

        // Находим общее количество чисел между ними
        for (int i = 1; i < intervals.length; i++) {
            int nextStart = intervals[i][0];
            int nextEnd = intervals[i][1];

            if (nextStart > currentEnd + 1) {
                totalCount += nextStart - currentEnd - 1;
            }

            // Обновляем текущий интервал
            currentStart = nextStart;
            currentEnd = Math.max(currentEnd, nextEnd);
        }

        return totalCount;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<int[]> intervals = new ArrayList<>();

        // Ввод с клавиатуры
        System.out.println("Введите интервалы в формате [начало, конец] (например, [1, 2]):");
        String input = scanner.nextLine().trim();

        // Обработка строки
        if (input.startsWith("[") && input.endsWith("]")) {
            input = input.substring(1, input.length() - 1).trim(); // убираем квадратные скобки

            if (!input.isEmpty()) {
                String[] pairs = input.split("\\],\\s*\\[");
                for (String pair : pairs) {
                    pair = pair.trim();
                    String[] values = pair.split(",\\s*");
                    if (values.length == 2) {
                        int start = Integer.parseInt(values[0].trim());
                        int end = Integer.parseInt(values[1].trim());
                        intervals.add(new int[]{start, end});
                    }
                }
            }
        }

        // Вызов функции подсчета чисел между интервалами
        int result = countNumbers(intervals.toArray(new int[0][]));
        System.out.println("Количество чисел между интервалами: " + result);

        scanner.close();
    }
}