package nl.tudelft.watchdog.logic.interval;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class LexicographicalTestOrderRunner extends BlockJUnit4ClassRunner {
    
	public LexicographicalTestOrderRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected List<FrameworkMethod> getChildren() {
		List<FrameworkMethod> children = super.getChildren();
		Collections.sort(children, new Comparator<FrameworkMethod>() {

			@Override
			public int compare(FrameworkMethod o1, FrameworkMethod o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		return children;
	}
}