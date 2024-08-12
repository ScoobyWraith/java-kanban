package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    @Test
    public void twoDifferentSubtasksWithSameIdAreEqual() {
        Subtask subtask1 = new Subtask("a", "b", TaskStatus.NEW, 1);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("c", "d", TaskStatus.IN_PROGRESS, 2);
        subtask2.setId(1);

        Assertions.assertNotEquals(subtask1, subtask2, "Две подзадачи с одинаковым ИД не считаются одинаковыми");
    }

    @Test
    public void cantAddSubtaskAsEpicToItself() {
        Subtask subtask = new Subtask("a", "b", TaskStatus.NEW, 1);
        subtask.setId(2);
        subtask.setId(1);

        Assertions.assertNotEquals(subtask.getEpicId(), subtask.getId(), "Подзадача стала своим эпиком");
    }
}
