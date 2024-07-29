import model.Epic;
import model.Task;

public class Main {

    public static void main(String[] args) {
        Task task = new Epic("1", "1");
        System.out.println(task.getClass());
    }
}
