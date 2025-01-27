import java.util.ArrayList;
import java.util.List;

public class CitySum {
    
    public static int chooseBestSum(int maxDistance, int numCities, List<Integer> distances) {
        // Хранение наилучшего расстояния
        int bestSum = -1;

        // Получение и подсчёт всех комбинаций
        List<List<Integer>> combinations = generateCombinations(distances, numCities);
        
        
        for (List<Integer> combination : combinations) {
            int sum = 0;
            for (int distance : combination) {
                sum += distance;
            }
            if (sum <= maxDistance && sum > bestSum) {
                bestSum = sum;
            }
        }
        
        return bestSum;
    }
    
    // Генерация всех комбинаций
    public static List<List<Integer>> generateCombinations(List<Integer> list, int len) {
        List<List<Integer>> result = new ArrayList<>();
        if (len == 0) {
            result.add(new ArrayList<>());
            return result;
        }
        if (list.isEmpty()) {
            return result;
        }
        Integer head = list.get(0);
        List<Integer> rest = list.subList(1, list.size());
        for (List<Integer> combination : generateCombinations(rest, len - 1)) {
            combination.add(0, head);
            result.add(combination);
        }
        result.addAll(generateCombinations(rest, len));
        return result;
    }

    public static void main(String[] args) {
        List<Integer> ts = List.of(50, 55, 56, 57, 58);
        int maxDistance = 163;
        int numCities = 3;

        int bestSum = chooseBestSum(maxDistance, numCities, ts);
        
        System.out.println("Лучшее суммарное расстояние: " + bestSum);
    }
}
