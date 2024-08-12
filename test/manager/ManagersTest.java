package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {
    @Test
    public void notNullTaskManager() {
        Assertions.assertNotNull(Managers.getDefault(), "Менеджер задач не возвращается утилитарным классом");
    }

    @Test
    public void notNullHistoryManager() {
        Assertions.assertNotNull(
                Managers.getDefaultHistory(),
                "Менеджер истории не возвращается утилитарным классом"
        );
    }
}
