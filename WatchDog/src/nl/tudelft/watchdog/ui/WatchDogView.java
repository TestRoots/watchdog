package nl.tudelft.watchdog.ui;

import nl.tudelft.watchdog.logic.InitializationManager;
import nl.tudelft.watchdog.logic.interval.IntervalStatistics;
import nl.tudelft.watchdog.ui.util.UIUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.swtchart.Chart;
import org.swtchart.IAxisTick;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesSet;

/** A view displaying all the statistics that WatchDog has gathered. */
public class WatchDogView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		IntervalStatistics intervalStatistics = new IntervalStatistics(
				InitializationManager.getInstance()
						.getIntervalsStatisticsPersister());

		Composite basicGrid = UIUtils
				.createZeroMarginGridedComposite(parent, 1);
		UIUtils.createLabel(
				"Statistics are based on your last hour of development, starting at "
						+ intervalStatistics.mostRecentDate + " and comprise "
						+ intervalStatistics.getNumberOfIntervals()
						+ " recorded intervals.", basicGrid);

		// create a chart
		Chart chart = new Chart(basicGrid, SWT.NONE);
		double[] ySeries = { 0.3, 1.4, 1.3, 1.9, 2.1 };
		ISeriesSet seriesSet = chart.getSeriesSet();
		ISeries series = seriesSet.createSeries(SeriesType.LINE, "line series");
		series.setYSeries(ySeries);
		IAxisTick xTick = chart.getAxisSet().getXAxis(0).getTick();
		IAxisTick yTick = chart.getAxisSet().getYAxis(0).getTick();
		// chart.getTitle().setVisible(false);
		chart.getAxisSet().getXAxis(0).getTitle().setVisible(false);
		chart.getAxisSet().getYAxis(0).getTitle().setVisible(false);
		xTick.setVisible(false);
		yTick.setVisible(false);

	}

	@Override
	public void setFocus() {
	}
}