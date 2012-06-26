package interval.activityCheckers;

import exceptions.EditorClosedPrematurelyException;


public interface IUpdateChecker {

	public abstract boolean hasChanged() throws EditorClosedPrematurelyException;

}