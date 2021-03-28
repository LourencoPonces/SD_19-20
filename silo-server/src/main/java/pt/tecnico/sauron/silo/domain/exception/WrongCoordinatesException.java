package pt.tecnico.sauron.silo.domain.exception;

public class WrongCoordinatesException extends Exception {

    private static final long serialVersionUID = 1L;

    public WrongCoordinatesException() { super("The coordinates given can not be accepted"); }
}