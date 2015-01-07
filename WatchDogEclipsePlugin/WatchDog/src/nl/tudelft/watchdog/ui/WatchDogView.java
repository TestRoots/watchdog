package nl.tudelft.watchdog.ui;

import nl.tudelft.watchdog.logic.InitializationManager;
import nl.tudelft.watchdog.logic.interval.IntervalStatistics;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.util.Rotation;

/** A view displaying all the statistics that WatchDog has gathered. */
public class WatchDogView extends ViewPart {
	/** The Id of the view. */
	public static final String ID = "WatchDog.view";

	private IntervalStatistics intervalStatistics;

	private Composite container;
	private Composite parent;

	private double eclipseOpen;
	private double userActive;
	private double userReading;
	private double userTyping;
	private double userProduction;
	private double userTest;
	private double userActiveRest;
	private double perspectiveDebug;
	private double perspectiveJava;
	private double perspectiveOther;
	private double averageTestDuration;

	private int junitRunsCount;

	private Composite oneColumn;

	/** Updates the view by completely repainting it. */
	public void update() {
		oneColumn.dispose();
		createPartControl(parent);
		parent.update();
		parent.layout();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

		oneColumn = UIUtils.createGridedComposite(parent, 1);
		if (!WatchDogGlobals.isActive) {
			createInactiveViewContent();
		} else {
			calculateTimes();
			createActiveView();
		}

		createRefreshLink();
	}

	private void createInactiveViewContent() {
		UIUtils.createBoldLabel("WatchDog is not active in this workspace! \n",
				oneColumn);
		UIUtils.createLabel(
				"Therefore we cannot show you any cool test statistics. \nTo get them, click the WatchDog icon and enable WatchDog.",
				oneColumn);
	}

	private void createActiveView() {
		container = UIUtils.createGridedComposite(oneColumn, 2);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createSWTChart(
				container,
				createBarChart(createDevelopmentBarDataset(),
						"Your Development Activity", "", "minutes"));
		createSWTChart(
				container,
				createPieChart(createDevelopmentPieDataset(),
						"Your Development Activity"));

		UIUtils.createLabel("", container);
		UIUtils.createLabel("", container);

		createSWTChart(
				container,
				createBarChart(createProductionVSTestBarDataset(),
						"Your Production vs. Test Activity", "", "minutes"));
		createSWTChart(
				container,
				createPieChart(createProductionVSTestPieDataset(),
						"Your Production vs. Test Activity"));

		UIUtils.createLabel("", container);
		UIUtils.createLabel("", container);

		createSWTChart(
				container,
				createPieChart(createPerspectiveViewPieDataset(),
						"Your Perspective Activity"));
		createSWTChart(
				container,
				createBarChart(createJunitExecutionBarDataset(),
						"Your Test Run Activity", "", ""));

		UIUtils.createLabel("From " + intervalStatistics.oldestDate + " to "
				+ intervalStatistics.mostRecentDate + ".", oneColumn);
		UIUtils.createLabel(
				"(Including " + intervalStatistics.getNumberOfIntervals()
						+ " intervals.)", oneColumn);
	}

	private void calculateTimes() {
		intervalStatistics = new IntervalStatistics(InitializationManager
				.getInstance().getIntervalManager());

		eclipseOpen = intervalStatistics
				.getPreciseTime(intervalStatistics.eclipseOpen);
		userActive = intervalStatistics
				.getPreciseTime(intervalStatistics.userActive);
		userReading = intervalStatistics
				.getPreciseTime(intervalStatistics.userReading);
		userTyping = intervalStatistics
				.getPreciseTime(intervalStatistics.userTyping);
		userProduction = intervalStatistics
				.getPreciseTime(intervalStatistics.userProduction);
		userTest = intervalStatistics
				.getPreciseTime(intervalStatistics.userTest);
		userActiveRest = userActive - userReading - userTyping;
		perspectiveDebug = intervalStatistics
				.getPreciseTime(intervalStatistics.perspectiveDebug);
		perspectiveJava = intervalStatistics
				.getPreciseTime(intervalStatistics.perspectiveJava);
		perspectiveOther = intervalStatistics
				.getPreciseTime(intervalStatistics.perspectiveOther);
		averageTestDuration = intervalStatistics.averageTestDuration;

		junitRunsCount = intervalStatistics.junitRunsCount;
	}

	private void createSWTChart(Composite container, JFreeChart chart) {
		ChartComposite chartComposite = new ChartComposite(container, SWT.NONE,
				chart, true);
		chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		Rectangle bounds = chartComposite.getBounds();
		bounds.height = bounds.width;
		chartComposite.setBounds(bounds);
	}

	private void createRefreshLink() {
		UIUtils.createLinkedLabel(oneColumn, new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		}, "Refresh.", "");
	}

	private DefaultCategoryDataset createDevelopmentBarDataset() {
		final DefaultCategoryDataset result = new DefaultCategoryDataset();
		result.setValue(userReading, "1", "Reading");
		result.setValue(userTyping, "1", "Writing");
		result.setValue(userActive, "1", "User Active");
		result.setValue(eclipseOpen, "1", "Eclipse Open");
		return result;
	}

	private PieDataset createDevelopmentPieDataset() {
		double divisor = userReading + userTyping + userActiveRest;
		final DefaultPieDataset result = new DefaultPieDataset();
		result.setValue("Reading" + printPercent(userReading, divisor),
				userReading);
		result.setValue("Writing" + printPercent(userTyping, divisor),
				userTyping);
		result.setValue(
				"Other activities" + printPercent(userActiveRest, divisor),
				userActiveRest);
		return result;
	}

	private DefaultCategoryDataset createProductionVSTestBarDataset() {
		final DefaultCategoryDataset result = new DefaultCategoryDataset();
		result.setValue(userProduction, "1", "Production Code");
		result.setValue(userTest, "1", "Test Code");
		return result;
	}

	private PieDataset createProductionVSTestPieDataset() {
		double divisor = userProduction + userTest;
		final DefaultPieDataset result = new DefaultPieDataset();
		result.setValue(
				"Production Code" + printPercent(userProduction, divisor),
				userProduction);
		result.setValue("Test Code" + printPercent(userTest, divisor), userTest);
		return result;
	}

	private JFreeChart createPieChart(final PieDataset dataset,
			final String title) {
		final JFreeChart chart = ChartFactory.createPieChart3D(title, dataset,
				true, true, false);
		final PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.8f);
		return chart;
	}

	private JFreeChart createBarChart(final DefaultCategoryDataset dataset,
			final String title, final String xAxisName, final String yAxisName) {
		final JFreeChart chart = ChartFactory.createBarChart3D(title,
				xAxisName, yAxisName, dataset);
		chart.getLegend().setVisible(false);
		return chart;
	}

	private String printPercent(double dividend, double divisor) {
		if (divisor == 0) {
			return " (--)";
		}
		return " (" + String.format("%.1f", dividend * 100 / divisor) + "%)";
	}

	@Override
	public void setFocus() {
		parent.setFocus();
	}

	private PieDataset createPerspectiveViewPieDataset() {
		double divisor = perspectiveDebug + perspectiveJava + perspectiveOther;
		final DefaultPieDataset result = new DefaultPieDataset();
		result.setValue("Java" + printPercent(perspectiveJava, divisor),
				perspectiveJava);
		result.setValue("Debug" + printPercent(perspectiveDebug, divisor),
				perspectiveDebug);
		result.setValue("Other" + printPercent(perspectiveOther, divisor),
				perspectiveOther);
		return result;
	}

	private DefaultCategoryDataset createJunitExecutionBarDataset() {
		final DefaultCategoryDataset result = new DefaultCategoryDataset();
		result.setValue(junitRunsCount, "1", "Number of Test Runs");
		result.setValue(averageTestDuration, "1", "Test Run Average Duration");
		return result;
	}

}