package pt.tecnico.sauron.silo.domain.exception;

public class InvalidNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidNameException() { super("The name of the camera is invalid."); }

}