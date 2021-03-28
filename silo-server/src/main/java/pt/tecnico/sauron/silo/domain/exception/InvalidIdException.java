package pt.tecnico.sauron.silo.domain.exception;

public class InvalidIdException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidIdException() { super("The id is invalid."); }
}