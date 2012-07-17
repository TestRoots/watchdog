package nl.tudelft.watchdog.interval.active;

import java.util.Timer;
import java.util.TimerTask;

import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentAttentionEvent;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentNotifier;
import nl.tudelft.watchdog.interval.ActivityType;
import nl.tudelft.watchdog.interval.activityCheckers.RunCallBack;

import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;

public class ActiveReadingInterval extends ActiveInterval {
	
	private final StyledText styledText;
	private Timer timer;
	private boolean stillActive;
	private CaretListener caretListener;
	private PaintListener paintListener;
	
	/**
	 * @param editor
	 * 		the editor in this interval
	 */
	public ActiveReadingInterval(ITextEditor editor){
		super(editor);
		timer = new Timer();
		stillActive = true;
		
		styledText = (StyledText) editor.getAdapter(Control.class);
		
		createListeners();
	}

	private void createListeners() {
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				caretListener = new CaretListener() { //cursor place changes		
					@Override
					public void caretMoved(CaretEvent event) {
						stillActive = true;
						styledText.removeCaretListener(this); //listen just once to not get millions of events fired
					}
				};
				
				paintListener = new PaintListener() { //for redraws of the view, e.g. when scrolled		
					@Override
					public void paintControl(PaintEvent e) {
						stillActive = true;
						styledText.removePaintListener(this); //listen just once to not get millions of events fired
					}
				};
			}
		});
	}

	@Override
	public void addTimeoutListener(long timeout, final RunCallBack callbackWhenFinished) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(stillActive){
					stillActive = false;
					
					Display.getDefault().asyncExec(new Runnable() {						
						@Override
						public void run() {
							styledText.addCaretListener(caretListener);
							styledText.addPaintListener(paintListener);
						}
					});
					
				}else{
					this.cancel();
					removeListeners();
					listenForReactivation();
					callbackWhenFinished.onInactive();
				}
			}
		}, 0, timeout);
	}
	
	private void removeListeners() {		
		Display.getDefault().asyncExec(new Runnable() {			
			@Override
			public void run() {
				styledText.removeCaretListener(caretListener);
				styledText.removePaintListener(paintListener);
			}
		});
	}
	
	private void listenForReactivation(){
		Display.getDefault().asyncExec(new Runnable() {			
			@Override
			public void run() {
				styledText.addCaretListener(new CaretListener() { //cursor place changes		
					@Override
					public void caretMoved(CaretEvent event) {
						DocumentNotifier.fireDocumentStartFocusEvent(new DocumentAttentionEvent(editor));
						styledText.removeCaretListener(this); //listen just once to not get millions of events fired
					}
				});
				
				styledText.addPaintListener(new PaintListener() { //for redraws of the view, e.g. when scrolled		
					@Override
					public void paintControl(PaintEvent e) {
						DocumentNotifier.fireDocumentStartFocusEvent(new DocumentAttentionEvent(editor));
						styledText.removePaintListener(this); //listen just once to not get millions of events fired
					}
				});
			}
		});
	}

	@Override
	public ActivityType getActivityType() {
		return ActivityType.Reading;
	}
}
