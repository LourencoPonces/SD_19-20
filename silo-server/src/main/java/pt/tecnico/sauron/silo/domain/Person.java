package pt.tecnico.sauron.silo.domain;

public class Person extends Type {
	public String _id;

	public Person(String id) { _id = id;}

	/**
	 * Get person id
	 * @return String
	 */
	@Override
	public String getId(){ return this._id; }

	/**
	 * Set person id
	 * @param id
	 */
	public void setId(String id) { this._id = id; }

	/**
	 * Prints to the console the name of the type
	 * @return
	 */
	@Override
	public String toString() { return "person"; }

}