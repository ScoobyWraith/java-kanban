package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {
    @Test
    public void twoDifferentEpicsWithSameIdAreEqual() {
        int sameId = 1;
        Epic epic1 = new Epic("a", "b");
        epic1.setStatus(TaskStatus.NEW);
        epic1.setId(sameId);
        Epic epic2 = new Epic("c", "d");
        epic2.setId(sameId);
        epic1.setStatus(TaskStatus.IN_PROGRESS);

        Assertions.assertEquals(epic1, epic2, "Два эпика с одинаковым ИД не считаются одинаковыми");
    }

    @Test
    public void cantAddEpicAsSubtaskToItself() {
        int epicId = 1;
        Epic epic = new Epic("a", "b");
        epic.setId(epicId);
        epic.addSubtaskId(epicId);

        Assertions.assertFalse(epic.getAllSubtaskIds().contains(epicId), "Эпик стал своей подзадачей");
    }

    @Test
    public void independenceBetweenCopies() {
        int subtaskId = 1;
        Epic task = new Epic(
                "a",
                "b"
        );
        task.setDuration(Duration.ofMinutes(10));
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 0, 0));
        Epic copy = task.getCopy();
        copy.setTitle("aa");
        copy.setDescription("bb");
        copy.addSubtaskId(subtaskId);
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
        Assertions.assertFalse(task.getAllSubtaskIds().contains(subtaskId), "У копии зависимый пул подзадач");
    }
}
