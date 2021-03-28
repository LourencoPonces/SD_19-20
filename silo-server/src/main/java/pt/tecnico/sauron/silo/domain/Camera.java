package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.domain.exception.WrongCameraNameException;
import pt.tecnico.sauron.silo.domain.exception.WrongCoordinatesException;

public class Camera {
	private static final int MIN_CAMERA_NAME_SIZE = 3;
	private static final int MAX_CAMERA_NAME_SIZE = 15;
	private static final double MAX_LATITUDE = 90;
	private static final double MIN_LATITUDE = -90;
	private static final double MAX_LONGITUDE = 180;
	private static final double MIN_LONGITUDE = -180;

	String _name;
	double _latitude, _longitude;

	public Camera(String name, double latitude, double longitude) throws WrongCameraNameException, WrongCoordinatesException{
		checkCameraName(name);
		checkCoordinates(latitude, longitude);
		_name = name;
		_latitude = latitude;
		_longitude = longitude;
	}

	/**
	 * Get camera name
	 * @return String
	 */
	public String getName() { return this._name; }

	/**
	 * Set camera name
	 * @param name
	 */
	public void setName(String name) { this._name = name; }

	/**
	 * Get camera latitude
	 * @return
	 */
	public double getLatitude() { return this._latitude; }

	/**
	 * Set camera latitude
	 * @param latitude
	 */
	public void setLatitude(double latitude) { this._latitude = latitude; }

	/**
	 * Get camera longitude
	 * @return
	 */
	public double getLongitude() { return this._longitude; }

	/**
	 * Set camera longitude
	 * @param longitude
	 */
	public void setLongitude(double longitude) { this._longitude = longitude; }

	/**
	 * Verify if the camera name is valid
	 * @param cameraName
	 * @throws WrongCameraNameException
	 */
	private void checkCameraName(String cameraName) throws WrongCameraNameException {
		if ((cameraName.length() < MIN_CAMERA_NAME_SIZE || cameraName.length() > MAX_CAMERA_NAME_SIZE) || !cameraName.matches("[A-Za-z0-9]+")){
			throw new WrongCameraNameException();
		}
	}

	/**
	 * Verify if the coordinates are valid
	 * @param latitude
	 * @param longitude
	 * @throws WrongCoordinatesException
	 */
	private void checkCoordinates(double latitude, double longitude) throws WrongCoordinatesException {
		if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE){
			throw new WrongCoordinatesException();
		}
	}

}