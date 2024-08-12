package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    public void twoDifferentSubtasksWithSameIdAreEqual() {
        Task task1 = new Task("a", "b", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("c", "d", TaskStatus.IN_PROGRESS);
        task2.setId(1);

        Assertions.assertEquals(task1, task2, "Две задачи с одинаковым ИД не считаются одинаковыми");
    }
}
