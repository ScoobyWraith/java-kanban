package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {
    @Test
    public void twoDifferentEpicsWithSameIdAreEqual() {
        Epic epic1 = new Epic("a", "b");
        epic1.setStatus(TaskStatus.NEW);
        epic1.setId(1);
        Epic epic2 = new Epic("c", "d");
        epic2.setId(1);
        epic1.setStatus(TaskStatus.IN_PROGRESS);

        Assertions.assertEquals(epic1, epic2, "Два эпика с одинаковым ИД не считаются одинаковыми");
    }

    @Test
    public void cantAddEpicAsSubtaskToItself() {
        Epic epic = new Epic("a", "b");
        epic.setId(1);
        epic.addSubtaskId(1);

        Assertions.assertFalse(epic.getAllSubtaskIds().contains(1), "Эпик стал своей подзадачей");
    }
}
