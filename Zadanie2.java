import java.util.ArrayList;
import java.util.List;

public class Zadanie2 {

    public static String formatRanges(int[] nums) {
        if (nums == null || nums.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        int start = nums[0];
        int end = nums[0];

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == end + 1) {
                // продолжение текущего диапазона
                end = nums[i];
            } else {
                // закрыть текущий диапазон и добавить его в результат
                if (start == end) {
                    result.append(start).append(",");
                } else {
                    result.append(start).append("-").append(end).append(",");
                }
                // начать новый диапазон
                start = nums[i];
                end = nums[i];
            }
        }

        // добавление последнего диапазона
        if (start == end) {
            result.append(start);
        } else {
            result.append(start).append("-").append(end);
        }

        return result.toString();
    }

    public static void main(String[] args) {
        int[] nums = {-10, -9, -8, -6, -3, -2, -1, 0, 1, 3, 4, 5, 7, 8, 9, 10, 11, 14, 15, 17, 18, 19, 20};
        String formattedRanges = formatRanges(nums);
        System.out.println(formattedRanges); //Выведет "-10--8,-6,-3-1,3-5,7-11,14,15,17-20"
    }
}
