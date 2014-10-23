package nl.tudelft.watchdog.ui.view;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * A JavaFx Chart that uses the JavaFX-SWT bindings to be embedded in a
 * {@link Composite}.
 */
public abstract class EmbeddableJavaFXChart {

	/** Creates the graph. */
	public void createGraph(Composite container) {
		final FXCanvas fxCanvas = new FXCanvas(container, SWT.NONE) {
			public Point computeSize(int wHint, int hHint, boolean changed) {
				getScene().getWindow().sizeToScene();
				int width = (int) getScene().getWidth();
				int height = (int) getScene().getHeight();
				return new Point(width, height);
			}
		};

		Chart chart = generateChart();

		Scene scene = new Scene(chart, 400, 300);
		fxCanvas.setScene(scene);
	}

	/**
	 * @return a JavaFX Char. Is called by {@link #createGraph(Composite)}
	 *         (Template method pattern.)
	 */
	abstract protected Chart generateChart();
}