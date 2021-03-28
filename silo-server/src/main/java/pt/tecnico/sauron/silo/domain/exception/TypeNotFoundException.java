package pt.tecnico.sauron.silo.domain.exception;

public class TypeNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public TypeNotFoundException() { super("The type is invalid."); }
}