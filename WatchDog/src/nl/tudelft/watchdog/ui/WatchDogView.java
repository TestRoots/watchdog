package nl.tudelft.watchdog.ui;

import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import nl.tudelft.watchdog.logic.InitializationManager;
import nl.tudelft.watchdog.logic.interval.IntervalStatistics;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** A view displaying all the statistics that WatchDog has gathered. */
public class WatchDogView extends ViewPart {
	private Composite container;
	private IntervalStatistics intervalStatistics;

	@Override
	public void createPartControl(Composite parent) {

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent,
				SWT.H_SCROLL | SWT.V_SCROLL);

		Color white = new Color(this.getViewSite().getShell().getDisplay(),
				255, 255, 255);
		container = UIUtils.createZeroMarginGridedComposite(scrolledComposite,
				1);
		container.setBackground(white);
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		scrolledComposite.setContent(container);

		// GridDataFactory.fillDefaults().grab(true, true).hint(400, 400)
		// .applyTo(text);
		container.setLayout(new GridLayout(1, false));

		if (!WatchDogGlobals.isActive) {
			UIUtils.createBoldLabel(
					"WatchDog is not active in this workspace! \n", container);
			UIUtils.createLabel(
					"Therefore we cannot show you any cool test statistics. \nTo get them, click the WatchDog icon and enable WatchDog.",
					container);
		} else {
			intervalStatistics = new IntervalStatistics(InitializationManager
					.getInstance().getIntervalManager());

			UIUtils.createLabel(
					"Statistics on your last hour of development, starting at "
							+ intervalStatistics.mostRecentDate
							+ " and comprise "
							+ intervalStatistics.getNumberOfIntervals()
							+ " recorded intervals.", container);

			Composite statisticsContainer = UIUtils
					.createZeroMarginGridedComposite(container, 2);

			createGraph(statisticsContainer);
			createGraph(statisticsContainer);
		}

		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

	}

	private void createGraph(Composite container) {
		final FXCanvas fxCanvas = new FXCanvas(container, SWT.NONE) {
			public Point computeSize(int wHint, int hHint, boolean changed) {
				getScene().getWindow().sizeToScene();
				int width = (int) getScene().getWidth();
				int height = (int) getScene().getHeight();
				return new Point(width, height);
			}
		};

		final NumberAxis xAxis = new NumberAxis();
		final CategoryAxis yAxis = new CategoryAxis();
		final BarChart<Number, String> bc = new BarChart<Number, String>(xAxis,
				yAxis);
		bc.setTitle("Reading vs. Writing");
		xAxis.setTickLabelRotation(90);
		Series<Number, String> series1 = new XYChart.Series<Number, String>();
		series1.getData().add(
				new XYChart.Data<Number, String>(intervalStatistics.eclipseOpen
						.getStandardSeconds(), "Eclipse Open"));
		series1.getData().add(
				new XYChart.Data<Number, String>(intervalStatistics.userActive
						.getStandardSeconds(), "User Activity"));
		series1.getData().add(
				new XYChart.Data<Number, String>(intervalStatistics.userReading
						.getStandardSeconds(), "Reading Code"));
		series1.getData().add(
				new XYChart.Data<Number, String>(intervalStatistics.userTyping
						.getStandardSeconds(), "Writing Code"));

		Scene scene = new Scene(bc, 400, 300);
		ObservableList<Series<Number, String>> data = bc.getData();
		data.add(series1);

		fxCanvas.setScene(scene);
	}

	@Override
	public void setFocus() {
		container.setFocus();
	}
}