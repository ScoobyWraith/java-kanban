package util;

import java.time.LocalDateTime;

public class EpicUpdatesHolder {
    private int subtasksWithStatusNew;
    private int subtasksWithStatusDone;
    private long epicDuration;
    private LocalDateTime epicStartTime;
    private LocalDateTime epicEndTime;

    public EpicUpdatesHolder() {
        subtasksWithStatusNew = 0;
        subtasksWithStatusDone = 0;
        epicDuration = 0;
        epicStartTime = null;
        epicEndTime = null;
    }

    public int getSubtasksWithStatusNew() {
        return subtasksWithStatusNew;
    }

    public int getSubtasksWithStatusDone() {
        return subtasksWithStatusDone;
    }

    public long getEpicDuration() {
        return epicDuration;
    }

    public LocalDateTime getEpicStartTime() {
        return epicStartTime;
    }

    public void setEpicStartTime(LocalDateTime epicStartTime) {
        this.epicStartTime = epicStartTime;
    }

    public LocalDateTime getEpicEndTime() {
        return epicEndTime;
    }

    public void setEpicEndTime(LocalDateTime epicEndTime) {
        this.epicEndTime = epicEndTime;
    }

    public void incSubtasksWithStatusNew() {
        subtasksWithStatusNew++;
    }

    public void incSubtasksWithStatusDone() {
        subtasksWithStatusDone++;
    }

    public void addEpicDuration(Long minutes) {
        epicDuration += minutes;
    }
}
