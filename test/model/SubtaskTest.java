package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class SubtaskTest {
    @Test
    public void twoDifferentSubtasksWithSameIdAreEqual() {
        int sameId = 1;
        Subtask subtask1 = new Subtask("a", "b", TaskStatus.NEW, 1);
        subtask1.setId(sameId);
        Subtask subtask2 = new Subtask("c", "d", TaskStatus.IN_PROGRESS, 2);
        subtask2.setId(sameId);

        Assertions.assertNotEquals(subtask1, subtask2, "Две подзадачи с одинаковым ИД не считаются одинаковыми");
    }

    @Test
    public void cantAddSubtaskAsEpicToItself() {
        int epicId = 1;
        int subtaskId = 2;
        Subtask subtask = new Subtask("a", "b", TaskStatus.NEW, epicId);
        subtask.setId(subtaskId);
        subtask.setId(epicId);

        Assertions.assertNotEquals(subtask.getEpicId(), subtask.getId(), "Подзадача стала своим эпиком");
    }

    @Test
    public void independenceBetweenCopies() {
        Subtask task = new Subtask(
                "a",
                "b",
                TaskStatus.NEW,
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 1, 1, 0, 0)
        );
        Subtask copy = task.getCopy();
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
