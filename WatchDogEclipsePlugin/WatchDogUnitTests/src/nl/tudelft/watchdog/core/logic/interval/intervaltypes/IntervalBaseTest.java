package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class IntervalBaseTest {

	@Test
	public void equalsContract() {
	    EqualsVerifier.forClass(IntervalBase.class).verify();
	}
}
