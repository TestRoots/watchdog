package nl.tudelft.watchdog.core.logic.ui.events;

import java.util.Date;

public interface WatchDogEventTypeInterface {
    void process(Date forcedDate, Object source);

    default void process() {
        this.process(new Object());
    }

    default void process(Object source) {
        this.process(new Date(), source);
    }
}
