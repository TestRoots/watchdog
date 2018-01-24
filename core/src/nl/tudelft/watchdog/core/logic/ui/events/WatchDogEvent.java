package nl.tudelft.watchdog.core.logic.ui.events;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.document.EditorWrapperBase;
import nl.tudelft.watchdog.core.logic.interval.IDEIntervalManagerBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.*;
import nl.tudelft.watchdog.core.logic.ui.InactivityNotifier;
import nl.tudelft.watchdog.core.logic.ui.UserInactivityNotifier;

import java.util.Date;
import java.util.EventObject;

/** Any event transferred by WatchDog. */
public class WatchDogEvent extends EventObject {

	/** Serial version. */
	private static final long serialVersionUID = 1L;
    private static final int USER_ACTIVITY_TIMEOUT = 16000;

    public static IDEIntervalManagerBase intervalManager;
    public static WatchDogEventEditorSpecificImplementation editorSpecificImplementation;

    private static UserInactivityNotifier userInactivityNotifier = new UserInactivityNotifier(USER_ACTIVITY_TIMEOUT, WatchDogEvent.EventType.USER_INACTIVITY);
    private static InactivityNotifier readingInactivityNotifier = new InactivityNotifier(USER_ACTIVITY_TIMEOUT, WatchDogEvent.EventType.TYPING_INACTIVITY);
    private static InactivityNotifier typingInactivityNotifier = new InactivityNotifier(USER_ACTIVITY_TIMEOUT, WatchDogEvent.EventType.READING_INACTIVITY);

    /** Constructor. */
	public WatchDogEvent(Object source, EventType type) {
		super(source);
		this.type = type;
	}

	/** The type of the event. */
	private final EventType type;

    public void update() {
	    this.update(new Date());
    }

	public void update(Date forcedDate) {
	    this.type.update(forcedDate, this);
    }

    interface EventTypeUpdate {
        void update(Date forcedDate, WatchDogEvent event);
    }

    /** The different type of events. */
    @SuppressWarnings("javadoc")
    public enum EventType implements EventTypeUpdate {

        ACTIVE_WINDOW {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                IDEActiveInterval interval = intervalManager.getInterval(IDEActiveInterval.class);
                if (isClosed(interval)) {
                    intervalManager.addInterval(new IDEActiveInterval(forcedDate));
                }
                userInactivityNotifier.trigger(forcedDate);
            }
        }, INACTIVE_WINDOW {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                IDEActiveInterval interval = intervalManager.getInterval(IDEActiveInterval.class);
                intervalManager.closeInterval(interval, forcedDate);
            }
        }, START_IDE {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                intervalManager.addInterval(new IDEOpenInterval(forcedDate));
                userInactivityNotifier.trigger(forcedDate);
            }
        }, END_IDE {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                userInactivityNotifier.cancelTimer(forcedDate);
            }
        },

        ACTIVE_FOCUS {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                EditorIntervalBase editorInterval = intervalManager.getEditorInterval();
                Object editor = event.getSource();
                if (needToCreateNewReadingInterval(editorInterval, editor)) {
                    if (!isClosed(editorInterval)) {
                        intervalManager.closeInterval(editorInterval, forcedDate);
                    }

                    ReadingInterval readingInterval = new ReadingInterval(
                            editorSpecificImplementation.createEditorWrapper(editor), forcedDate);
                    readingInterval
                            .setDocument(editorSpecificImplementation.createDocument(editor));
                    intervalManager.addInterval(readingInterval);
                }

                readingInactivityNotifier.trigger();
                userInactivityNotifier.trigger(forcedDate);
            }
        }, INACTIVE_FOCUS {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                EditorIntervalBase editorInterval = intervalManager.getEditorInterval();
                intervalManager.closeInterval(editorInterval, forcedDate);
                readingInactivityNotifier.cancelTimer(forcedDate);
                typingInactivityNotifier.cancelTimer(forcedDate);
            }
        }, SUBSEQUENT_EDIT {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                EditorIntervalBase editorInterval = intervalManager.getEditorInterval();
                Object editor = event.getSource();

                if (isClosed(editorInterval)
                        || !intervalExistsAndIsOfType(editorInterval, IntervalType.TYPING)
                        || isDifferentEditor(editorInterval, editor)) {
                    new WatchDogEvent(event.getSource(),
                            WatchDogEvent.EventType.START_EDIT).update();
                    return;
                }

                // Increase modCount for the current typing interval
                EditorEvent editorEvent = (EditorEvent) event;
                TypingInterval typingInt = (TypingInterval) editorInterval;
                typingInt.increaseModCountWith(editorEvent.getModCount());

                typingInactivityNotifier.trigger();
                userInactivityNotifier.trigger(forcedDate);
            }
        }, START_EDIT {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                EditorIntervalBase editorInterval = intervalManager
                        .getEditorInterval();
                Object editor = event.getSource();

                readingInactivityNotifier.cancelTimer(forcedDate);
                if (intervalExistsAndIsOfType(editorInterval, IntervalType.TYPING)
                        && !isDifferentEditor(editorInterval, editor)) {
                    return;
                }

                intervalManager.closeInterval(editorInterval, forcedDate);

                TypingInterval typingInterval = new TypingInterval(
                        editorSpecificImplementation.createEditorWrapper(editor), forcedDate);
                Document document;
                if (editorInterval != null
                        && !isDifferentEditor(editorInterval, editor)) {
                    document = editorInterval.getDocument();
                } else {
                    document = editorSpecificImplementation.createDocument(editor);
                }
                typingInterval.setDocument(document);
                intervalManager.addInterval(typingInterval);

                typingInactivityNotifier.trigger();
                userInactivityNotifier.trigger(forcedDate);
            }
        }, CARET_MOVED {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                EditorIntervalBase editorInterval = intervalManager.getEditorInterval();
                Object editor = event.getSource();
                if (needToCreateNewReadingInterval(editorInterval, editor)) {
                    if (!isClosed(editorInterval)) {
                        intervalManager.closeInterval(editorInterval, forcedDate);
                    }

                    ReadingInterval readingInterval = new ReadingInterval(
                            editorSpecificImplementation.createEditorWrapper(editor), forcedDate);
                    readingInterval
                            .setDocument(editorSpecificImplementation.createDocument(editor));
                    intervalManager.addInterval(readingInterval);
                }

                readingInactivityNotifier.trigger();
                userInactivityNotifier.trigger(forcedDate);
            }
        }, PAINT {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                EditorIntervalBase editorInterval = intervalManager.getEditorInterval();
                Object editor = event.getSource();
                if (needToCreateNewReadingInterval(editorInterval, editor)) {
                    if (!isClosed(editorInterval)) {
                        intervalManager.closeInterval(editorInterval, forcedDate);
                    }

                    ReadingInterval readingInterval = new ReadingInterval(
                            editorSpecificImplementation.createEditorWrapper(editor), forcedDate);
                    readingInterval
                            .setDocument(editorSpecificImplementation.createDocument(editor));
                    intervalManager.addInterval(readingInterval);
                }

                readingInactivityNotifier.trigger();
                userInactivityNotifier.trigger(forcedDate);
            }
        },
        START_PERSPECTIVE {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                PerspectiveInterval.Perspective perspecitve = (PerspectiveInterval.Perspective) event.getSource();
                createNewPerspectiveInterval(perspecitve, forcedDate);
                userInactivityNotifier.trigger(forcedDate);
            }
        }, JUNIT {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                editorSpecificImplementation.addJUnitInterval(event);
            }
        },

        USER_ACTIVITY {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                UserActiveInterval interval = intervalManager.getInterval(UserActiveInterval.class);
                if (isClosed(interval)) {
                    intervalManager.addInterval(new UserActiveInterval(forcedDate));
                }
                userInactivityNotifier.trigger();
            }
        }, USER_INACTIVITY {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                UserActiveInterval interval = intervalManager.getInterval(UserActiveInterval.class);
                intervalManager.closeInterval(interval, forcedDate);
                typingInactivityNotifier.cancelTimer(forcedDate);
                readingInactivityNotifier.cancelTimer(forcedDate);
            }
        }, TYPING_INACTIVITY {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                EditorIntervalBase editorInterval = intervalManager.getEditorInterval();
                if (intervalExistsAndIsOfType(editorInterval, IntervalType.TYPING)) {
                    intervalManager.closeInterval(editorInterval, forcedDate);
                }
            }
        }, READING_INACTIVITY {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                EditorIntervalBase editorInterval = intervalManager.getEditorInterval();
                if (intervalExistsAndIsOfType(editorInterval, IntervalType.READING)) {
                    intervalManager.closeInterval(editorInterval, forcedDate);
                }
            }
        },

        START_WATCHDOGVIEW {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                WatchDogViewInterval interval = intervalManager.getInterval(WatchDogViewInterval.class);
                if (!intervalExistsAndIsOfType(interval, IntervalType.WATCHDOGVIEW)) {
                    intervalManager
                            .addInterval(new WatchDogViewInterval(forcedDate));
                }
                userInactivityNotifier.trigger(forcedDate);
            }
        }, END_WATCHDOGVIEW {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                WatchDogViewInterval interval = intervalManager.getInterval(WatchDogViewInterval.class);
                if (intervalExistsAndIsOfType(interval, IntervalType.WATCHDOGVIEW)) {
                    intervalManager.closeInterval(interval, forcedDate);
                }
            }
        }, START_DEBUG {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                DebugInterval interval = intervalManager.getInterval(DebugInterval.class);
                if(!intervalExistsAndIsOfType(interval, IntervalType.DEBUG)) {
                    intervalManager.addInterval(new DebugInterval(forcedDate));
                }
                userInactivityNotifier.trigger(forcedDate);
            }
        }, END_DEBUG {
            @Override
            public void update(Date forcedDate, WatchDogEvent event) {
                DebugInterval interval = intervalManager.getInterval(DebugInterval.class);
                if(intervalExistsAndIsOfType(interval, IntervalType.DEBUG)) {
                    intervalManager.closeInterval(interval, forcedDate);
                }
            }
        }

    }
    private static boolean isClosed(IntervalBase interval) {
        return interval == null || interval.isClosed();
    }

    private static boolean needToCreateNewReadingInterval(
            EditorIntervalBase editorInterval, Object editor) {
        return isClosed(editorInterval)
                || isDifferentEditor(editorInterval, editor);
    }

    private static boolean isDifferentEditor(EditorIntervalBase editorInterval,
                                             Object editor) {
        return editorInterval.getEditorWrapper().getEditor() != editor;
    }

    private static boolean intervalExistsAndIsOfType(IntervalBase interval, IntervalType type) {
        return interval != null && interval.getType() == type;
    }

    /** Creates a new perspective Interval of the given type. */
    private static void createNewPerspectiveInterval(
            PerspectiveInterval.Perspective perspecitveType, Date forcedDate) {
        PerspectiveInterval perspectiveInterval = intervalManager
                .getInterval(PerspectiveInterval.class);
        if (perspectiveInterval != null && perspectiveInterval
                .getPerspectiveType() == perspecitveType) {
            // abort if such an interval is already open.
            return;
        }
        intervalManager.closeInterval(perspectiveInterval, forcedDate);
        intervalManager.addInterval(
                new PerspectiveInterval(perspecitveType, forcedDate));
    }

    public interface WatchDogEventEditorSpecificImplementation {
        void addJUnitInterval(WatchDogEvent event);

        EditorWrapperBase createEditorWrapper(Object editor);

        Document createDocument(Object editor);
    }
}