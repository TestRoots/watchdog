package nl.tudelft.watchdog.ui;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import nl.tudelft.watchdog.logic.InitializationManager;
import nl.tudelft.watchdog.logic.interval.IntervalStatistics;
import nl.tudelft.watchdog.ui.util.UIUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** A view displaying all the statistics that WatchDog has gathered. */
public class WatchDogView extends ViewPart {
	final static String austria = "Austria";
	final static String brazil = "Brazil";
	final static String france = "France";
	final static String italy = "Italy";
	final static String usa = "USA";
	private Composite container;
	private IntervalStatistics intervalStatistics;

	@Override
	public void createPartControl(Composite parent) {
		intervalStatistics = new IntervalStatistics(InitializationManager
				.getInstance().getIntervalsStatisticsPersister());

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

		UIUtils.createLabel(
				"Statistics based on your last hour of development, starting at "
						+ intervalStatistics.mostRecentDate + " and comprise "
						+ intervalStatistics.getNumberOfIntervals()
						+ " recorded intervals.", container);

		Composite statisticsContainer = UIUtils
				.createZeroMarginGridedComposite(container, 2);

		createGraph(statisticsContainer);
		createGraph(statisticsContainer);

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

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis,
				yAxis);
		bc.setTitle("Country Summary");
		xAxis.setLabel("Country");
		yAxis.setLabel("Value");

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("2003");
		series1.getData().add(new XYChart.Data(austria, 25601.34));
		series1.getData().add(new XYChart.Data(brazil, 20148.82));
		series1.getData().add(new XYChart.Data(france, 10000));
		series1.getData().add(new XYChart.Data(italy, 35407.15));
		series1.getData().add(new XYChart.Data(usa, 12000));

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("2004");
		series2.getData().add(new XYChart.Data(austria, 57401.85));
		series2.getData().add(new XYChart.Data(brazil, 41941.19));
		series2.getData().add(new XYChart.Data(france, 45263.37));
		series2.getData().add(new XYChart.Data(italy, 117320.16));
		series2.getData().add(new XYChart.Data(usa, 14845.27));

		XYChart.Series series3 = new XYChart.Series();
		series3.setName("2005");
		series3.getData().add(new XYChart.Data(austria, 45000.65));
		series3.getData().add(new XYChart.Data(brazil, 44835.76));
		series3.getData().add(new XYChart.Data(france, 18722.18));
		series3.getData().add(new XYChart.Data(italy, 17557.31));
		series3.getData().add(new XYChart.Data(usa, 92633.68));

		Scene scene = new Scene(bc, 400, 400);
		bc.getData().addAll(series1, series2, series3);

		fxCanvas.setScene(scene);
	}

	@Override
	public void setFocus() {
		container.setFocus();
	}
}