package pt.tecnico.sauron.silo.domain.exception;

public class InvalidCoordinatesException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidCoordinatesException() { super("The Coordinates of camera are invalid."); }
}