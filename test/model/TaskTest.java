package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {
    @Test
    public void twoDifferentTasksWithSameIdAreEqual() {
        int sameId = 1;
        Task task1 = new Task("a", "b", TaskStatus.NEW);
        task1.setId(sameId);
        Task task2 = new Task("c", "d", TaskStatus.IN_PROGRESS);
        task2.setId(sameId);

        Assertions.assertEquals(task1, task2, "Две задачи с одинаковым ИД не считаются одинаковыми");
    }

    @Test
    public void independenceBetweenCopies() {
        Task task = new Task(
                "a",
                "b",
                TaskStatus.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 1, 1, 0, 0)
        );
        Task copy = task.getCopy();
        copy.setTitle("aa");
        copy.setDescription("bb");
        copy.setStatus(TaskStatus.DONE);
        copy.setDuration(Duration.ofMinutes(20));
        copy.setStartTime(LocalDateTime.of(2020, 1, 1, 0, 0));

        Assertions.assertNotEquals(task.getTitle(), copy.getTitle(), "У копии зависимое название");
        Assertions.assertNotEquals(task.getDescription(), copy.getDescription(), "У копии зависимое описание");
        Assertions.assertNotEquals(task.getStatus(), copy.getStatus(), "У копии зависимый статус");
        Assertions.assertNotEquals(
                task.getDuration().get(),
                copy.getDuration().get(),
                "У копии зависимая продолжительность"
        );
        Assertions.assertNotEquals(
                task.getStartTime().get(),
                copy.getStartTime().get(),
                "У копии зависимое начало"
        );
    }
}
