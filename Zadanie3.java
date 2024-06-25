import java.util.Scanner;

public class Zadanie3 {

    public static String calculateTime(int seconds) {
        if (seconds < 0) {
            return "Ошибка: Время не может быть отрицательным.";
        }
        
        // Константы времени
        final int SECONDS_IN_MINUTE = 60;
        final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;
        final int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;
        final int SECONDS_IN_WEEK = SECONDS_IN_DAY * 7;
        final int SECONDS_IN_MONTH = SECONDS_IN_DAY * 30; // Упрощение для целей примера
        final int SECONDS_IN_YEAR = SECONDS_IN_DAY * 365; // Упрощение для целей примера
        
        // Вычисление времени
        int years = seconds / SECONDS_IN_YEAR;
        seconds %= SECONDS_IN_YEAR;
        
        int months = seconds / SECONDS_IN_MONTH;
        seconds %= SECONDS_IN_MONTH;
        
        int weeks = seconds / SECONDS_IN_WEEK;
        seconds %= SECONDS_IN_WEEK;
        
        int days = seconds / SECONDS_IN_DAY;
        seconds %= SECONDS_IN_DAY;
        
        int hours = seconds / SECONDS_IN_HOUR;
        seconds %= SECONDS_IN_HOUR;
        
        int minutes = seconds / SECONDS_IN_MINUTE;
        seconds %= SECONDS_IN_MINUTE;
        
        // Форматирование результата
        StringBuilder result = new StringBuilder();
        
        if (years > 0) {
            result.append(years).append(" ").append(years == 1 ? "год" : "года").append(", ");
        }
        if (months > 0) {
            result.append(months).append(" ").append(months == 1 ? "месяц" : "месяца").append(", ");
        }
        if (weeks > 0) {
            result.append(weeks).append(" ").append(weeks == 1 ? "неделя" : "недели").append(", ");
        }
        if (days > 0) {
            result.append(days).append(" ").append(days == 1 ? "день" : "дня").append(", ");
        }
        if (hours > 0) {
            result.append(hours).append(" ").append(hours == 1 ? "час" : "часа").append(", ");
        }
        if (minutes > 0) {
            result.append(minutes).append(" ").append(minutes == 1 ? "минута" : "минуты").append(", ");
        }
        if (seconds > 0) {
            result.append(seconds).append(" ").append(seconds == 1 ? "секунда" : "секунды");
        }
        
        // Удаление последней запятой и добавление "и" перед последним элементом
        if (result.length() > 0) {
            int lastIndex = result.lastIndexOf(", ");
            if (lastIndex != -1) {
                result.replace(lastIndex, lastIndex + 2, " и ");
            }
        }
        
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Введите количество секунд: ");
        int seconds = scanner.nextInt();
        
        String formattedTime = calculateTime(seconds);
        System.out.println("Формат времени: " + formattedTime);
        
        scanner.close();
    }
}