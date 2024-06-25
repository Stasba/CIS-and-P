import java.util.Scanner;

public class Zavod {

    public static int countEmployees(String input) {
        
        String[] entries = input.replaceAll("[\\[\\]\\s]", "").split(",");
        
        
        int employeeCount = 0;
        for (String entry : entries) {
            if (entry.equals("1")) {
                employeeCount++;
            }
        }
        
        return employeeCount;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Ввести массив");
        String input = scanner.nextLine();
        
        int employeeCount = countEmployees(input); 
        
        System.out.println("Количество сотрудников, зашедших на предприятие: " + employeeCount);
        
        scanner.close();
    }
}
