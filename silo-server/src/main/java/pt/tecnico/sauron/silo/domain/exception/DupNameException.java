package pt.tecnico.sauron.silo.domain.exception;

public class DupNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public DupNameException() { super("The name of the camera already exists."); }
}