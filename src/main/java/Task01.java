import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Task01 {
    public static void main(String[] args) {
        Task01 task01 = new Task01();
        String fileName = "input_01.txt";

        ClassLoader classLoader = task01.getClass().getClassLoader();

        File inputFile = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());

        int result = 0;

        try (Scanner scanner = new Scanner(inputFile)) {
            while (scanner.hasNextLine()) {
                result += Integer.parseInt(scanner.nextLine().trim());
            }
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
