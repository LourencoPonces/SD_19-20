package pt.tecnico.sauron.silo.domain;

public class Car extends Type {
	public String _id;

	public Car(String id) { _id = id; }

	/**
	 * Get the id of the car
	 * @return String
	 */
	@Override
	public String getId() { return this._id; }

	/**
	 * Set the id of the car
	 * @param id
	 */
	public void setId(String id) { this._id = id; }

	/**
	 * Prints to the console the name of the type
	 * @return String
	 */
	@Override
	public String toString() { return "car"; }
}