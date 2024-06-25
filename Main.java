public class Main {

    public static String removeVowels(String input) {
        // Определение строки гласных
        String vowels = "AEIOUaeiouАЕЁИОУЫЭЮЯаеёиоуыэюя";
        
        
        StringBuilder result = new StringBuilder();
        
        
        for (char ch : input.toCharArray()) {
            // Сагласная добовляется в вывод
            if (vowels.indexOf(ch) == -1) {
                result.append(ch);
            }
        }
        
        return result.toString();
    }

    public static void main(String[] args) {
        String input = "Удаление всех гласных из строки.";
        
        String output = removeVowels(input);
        
        System.out.println("Результат: " + output);
    }
}
