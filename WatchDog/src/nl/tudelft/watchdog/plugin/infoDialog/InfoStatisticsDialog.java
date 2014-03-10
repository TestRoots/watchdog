package nl.tudelft.watchdog.plugin.infoDialog;

import java.util.List;

import nl.tudelft.watchdog.interval.ActivityType;
import nl.tudelft.watchdog.interval.IntervalManager;
import nl.tudelft.watchdog.interval.IntervalStatistics;
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.util.WatchDogGlobals;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.Duration;

/**
 * A dialog displaying statistics about the WatchDog recordings. This dialog is
 * needed for the user to benefit from WatchDog and gain insights into how much
 * he's developing, and how much he is testing.
 */
public class InfoStatisticsDialog extends Dialog {

	/** Constructor. */
	public InfoStatisticsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		createGridLayout(container);
		createStatusText(container);

		return container;
	}

	/** Creates a grid layout for the given {@link Composite}. */
	private void createGridLayout(Composite container) {
		final int layoutMargin = 10;

		GridLayout layout = new GridLayout(2, false);
		layout.marginTop = layoutMargin;
		layout.marginLeft = layoutMargin;
		layout.marginBottom = layoutMargin;
		layout.marginRight = layoutMargin;
		container.setLayout(layout);
	}

	/** Creates a label with the status of WatchDog plugin */
	private void createStatusText(Composite container) {
		Color colorRed = new Color(getShell().getDisplay(), 255, 0, 0);
		Color colorGreen = new Color(getShell().getDisplay(), 0, 150, 0);
		createLabel("WatchDog Status: ", container);
		if (WatchDogGlobals.isActive) {
			createLabel(WatchDogGlobals.activeWatchDogUIText, container,
					colorGreen);
		} else {
			createLabel(WatchDogGlobals.inactiveWatchDogUIText, container,
					colorRed);
		}
		createCurrentIntervalSummary(container);
		createTotalIntervalSummary(container);
	}

	/** Creates a summary from the current Eclipse session in WatchDog. */
	private void createCurrentIntervalSummary(Composite container) {
		createIntervalSummary("Current Eclipse Session:", container,
				IntervalManager.getInstance().getRecordedIntervals());
	}

	/** Creates a summary with all the intervals in WatchDog added up. */
	private void createTotalIntervalSummary(Composite container) {
		createIntervalSummary("All Other Recording Sessions: ", container,
				WatchDogUtils.getAllRecordedIntervals());
	}

	/**
	 * Creates a summary from the given intervals, adding their durations up and
	 * listing the times per activity.
	 */
	private void createIntervalSummary(String text, Composite container,
			List<IInterval> intervals) {
		IntervalStatistics intervalStatistics = new IntervalStatistics(
				intervals);
		intervalStatistics.calculateDurations();

		// create some space before each listing
		createLabel("\n", container);
		createLabel("\n", container);
		createLabel(text, container);
		createLabel(WatchDogUtils.makeDurationHumanReadable(intervalStatistics
				.getTotalTimeOverAllActivities()), container);

		for (ActivityType activity : ActivityType.values()) {
			Duration duration = intervalStatistics
					.getDurationOfAcitivity(activity);
			createLabel(activity.toString(), container);

			String labelText = WatchDogUtils
					.makeDurationHumanReadable(duration);

			if (intervalStatistics.getTotalTimeOverAllActivities().getMillis() > 0) {
				int percentageOfActivity = (int) Math.round(((double) duration
						.getMillis() / intervalStatistics
						.getTotalTimeOverAllActivities().getMillis()) * 100);

				labelText = percentageOfActivity + "%" + " (" + labelText + ")";
			}

			createLabel(labelText, container);
		}
	}

	/** Creates and returns a label with the given text and color. */
	private Label createLabel(String text, Composite parent, Color color) {
		Label label = createLabel(text, parent);
		label.setForeground(color);
		return label;
	}

	/** Creates and returns a label with the given text. */
	private Label createLabel(String text, Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	/** Disables the creation of a cancel button in the dialog */
	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		if (id == IDialogConstants.CANCEL_ID) {
			return null;
		}
		return super.createButton(parent, id, label, defaultButton);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("WatchDog Statistics");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 450);
	}

}