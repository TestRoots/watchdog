package exceptions;

@SuppressWarnings("serial")
public class UpdateCheckerTaskNotCompletedException extends Exception {

	public UpdateCheckerTaskNotCompletedException(){
		super();
	}
	public UpdateCheckerTaskNotCompletedException(String message){
		super(message);
	}
}
