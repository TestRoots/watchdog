package nl.tudelft.watchdog.ui.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import nl.tudelft.watchdog.logic.InitializationManager;
import nl.tudelft.watchdog.logic.interval.IntervalStatistics;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** A view displaying all the statistics that WatchDog has gathered. */
public class WatchDogView extends ViewPart {
	/** The Id of the view. */
	public static final String ID = "WatchDog.view";

	private Composite container;
	private IntervalStatistics intervalStatistics;
	private Composite parent;
	private ScrolledComposite scrolledComposite;
	private long eclipseOpen;
	private long userActive;
	private long userReading;
	private long userTyping;
	private long userProduction;
	private long userTesting;
	private long userActiveRest;

	/** Updates the view by completely repainting it. */
	public void update() {
		scrolledComposite.dispose();
		parent.update();
		parent.layout();
		createPartControl(parent);
		parent.update();
		parent.layout();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL
				| SWT.V_SCROLL);

		Color white = new Color(this.getViewSite().getShell().getDisplay(),
				255, 255, 255);
		container = UIUtils.createZeroMarginGridedComposite(scrolledComposite,
				1);
		container.setBackground(white);
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		scrolledComposite.setContent(container);

		container.setLayout(new GridLayout(1, false));

		if (!WatchDogGlobals.isActive) {
			UIUtils.createBoldLabel(
					"WatchDog is not active in this workspace! \n", container);
			UIUtils.createLabel(
					"Therefore we cannot show you any cool test statistics. \nTo get them, click the WatchDog icon and enable WatchDog.",
					container);
			createRefreshLink();
		} else {
			intervalStatistics = new IntervalStatistics(InitializationManager
					.getInstance().getIntervalManager());

			eclipseOpen = intervalStatistics.eclipseOpen.getStandardSeconds();
			userActive = intervalStatistics.userActive.getStandardSeconds();
			userReading = intervalStatistics.userReading.getStandardSeconds();
			userTyping = intervalStatistics.userTyping.getStandardSeconds();

			UIUtils.createLabel("Statistics from "
					+ intervalStatistics.oldestDate + " to "
					+ intervalStatistics.mostRecentDate + " and comprise "
					+ intervalStatistics.getNumberOfIntervals()
					+ " recorded intervals.", container);
			createRefreshLink();
			UIUtils.createLabel("", container);

			Composite chartsContainer = UIUtils
					.createZeroMarginGridedComposite(container, 2);
			new UserActivityGeneralBarChart().createGraph(chartsContainer);
			new UserActivityGeneralPieChart().createGraph(chartsContainer);
			new ProductionVsTestingBarChart().createGraph(chartsContainer);
			new ProductionVsTestingPieChart().createGraph(chartsContainer);
		}

		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
	}

	private void createRefreshLink() {
		UIUtils.createLinkedLabel(container, new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		}, "Refresh.", "");
	}

	private class UserActivityGeneralBarChart extends EmbeddableJavaFXChart {

		protected BarChart<Number, String> generateChart() {
			final NumberAxis xAxis = new NumberAxis();
			final CategoryAxis yAxis = new CategoryAxis();
			final BarChart<Number, String> chart = new BarChart<Number, String>(
					xAxis, yAxis);
			chart.setTitle("Your General Development Activity");
			xAxis.setTickLabelRotation(90);
			xAxis.setLabel("minutes");
			Series<Number, String> series1 = new XYChart.Series<Number, String>();
			series1.getData().add(
					new XYChart.Data<Number, String>(eclipseOpen,
							"Eclipse Open"));
			series1.getData().add(
					new XYChart.Data<Number, String>(userActive, "Activity"));
			series1.getData().add(
					new XYChart.Data<Number, String>(userReading,
							"Reading Code"));
			series1.getData()
					.add(new XYChart.Data<Number, String>(userTyping,
							"Writing Code"));

			chart.setLegendVisible(false);
			ObservableList<Series<Number, String>> data = chart.getData();
			data.add(series1);

			return chart;
		}
	}

	private class UserActivityGeneralPieChart extends EmbeddableJavaFXChart {

		protected PieChart generateChart() {
			userActiveRest = userActive - userReading - userTyping;
			final NumberAxis xAxis = new NumberAxis();

			long divisor = userReading + userTyping + userActiveRest;

			xAxis.setTickLabelRotation(90);
			ObservableList<PieChart.Data> pieChartData = FXCollections
					.observableArrayList(
							new PieChart.Data("Reading"
									+ printPercent(userReading, divisor),
									userReading),
							new PieChart.Data("Writing"
									+ printPercent(userTyping, divisor),
									userTyping),
							new PieChart.Data("Other activities"
									+ printPercent(userActiveRest, divisor),
									userActiveRest));
			final PieChart chart = new PieChart(pieChartData);
			chart.setTitle("Your General Development Activity");

			return chart;
		}
	}

	private class ProductionVsTestingBarChart extends EmbeddableJavaFXChart {

		protected BarChart<Number, String> generateChart() {
			final NumberAxis xAxis = new NumberAxis();
			final CategoryAxis yAxis = new CategoryAxis();
			final BarChart<Number, String> chart = new BarChart<Number, String>(
					xAxis, yAxis);

			userProduction = intervalStatistics.userProduction
					.getStandardSeconds();
			userTesting = intervalStatistics.userTesting.getStandardSeconds();

			chart.setTitle("Effort On Production Vs. Test Code");
			xAxis.setTickLabelRotation(90);
			xAxis.setLabel("minutes");
			Series<Number, String> series1 = new XYChart.Series<Number, String>();
			series1.getData().add(
					new XYChart.Data<Number, String>(userTesting, "Test Code"));
			series1.getData().add(
					new XYChart.Data<Number, String>(userProduction,
							"Production Code"));
			chart.setLegendVisible(false);
			ObservableList<Series<Number, String>> data = chart.getData();
			data.add(series1);

			return chart;
		}
	}

	private class ProductionVsTestingPieChart extends EmbeddableJavaFXChart {

		protected PieChart generateChart() {
			final NumberAxis xAxis = new NumberAxis();
			xAxis.setTickLabelRotation(90);

			long divisor = userProduction + userTesting;
			ObservableList<PieChart.Data> pieChartData = FXCollections
					.observableArrayList(new PieChart.Data("Production Code"
							+ printPercent(userProduction, divisor),
							userProduction), new PieChart.Data("Test Code"
							+ printPercent(userTesting, divisor), userTesting));
			final PieChart chart = new PieChart(pieChartData);
			chart.setTitle("Effort On Production Vs. Test Code");

			return chart;
		}
	}

	private String printPercent(long dividend, long divisor) {
		if (divisor == 0) {
			return " (--)";
		}
		return " (" + dividend / divisor * 100 + "%)";
	}

	@Override
	public void setFocus() {
		container.setFocus();
	}

}