package pt.tecnico.sauron.silo.domain.exception;

public class CamNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public CamNotFoundException() { super("The name of the camera not found."); }
}