package pt.tecnico.sauron.silo.domain.exception;

public class WrongCameraCoordinatesException extends Exception {

    private static final long serialVersionUID = 1L;

    public WrongCameraCoordinatesException() { super("Wrong coordinates for the camera given"); }
}