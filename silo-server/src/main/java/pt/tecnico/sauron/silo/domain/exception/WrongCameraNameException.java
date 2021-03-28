package pt.tecnico.sauron.silo.domain.exception;

public class WrongCameraNameException extends Exception {

    private static final long serialVersionUID = 1L;

    public WrongCameraNameException() { super("The name of the camera can not be accepted"); }
}