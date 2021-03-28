package pt.tecnico.sauron.silo.domain;

import java.time.Instant;


public class Observation implements Comparable<Observation>{
	Type _type;
	Camera _camera;
	Instant _date;
	int _replic;
	int _seqN;

	public Observation(Type type, Camera camera, Instant date, int replic, int seqN) {
		_type = type;
		_camera = camera;
		_date = date;
		_replic = replic;
		_seqN = seqN;
	}

	/**
	 * Get the type of the observation
	 * @return Type
	 */
	public Type getType() { return this._type; }

	/**
	 * Set the type of the observation
	 * @param type
	 */
	public void setType(Type type) { this._type = type; }

	/**
	 * Get the camera
	 * @return Camera
	 */
	public Camera getCamera() { return this._camera; }

	/**
	 * Set the camera
	 * @param camera
	 */
	public void setCamera(Camera camera) { this._camera = camera; }

	/**
	 * Get the date
	 * @return Instant
	 */
	public Instant getDate() { return this._date; }

	/**
	 * Set the date
	 * @param date
	 */
	public void setDate(Instant date) { this._date = date; }

	/**
	 * Get the replic
	 * @return int
	 */
	public int getReplic() { return this._replic; }

	/**
	 * Set the type of the observation
	 * @param replic
	 */
	public void setType(int replic) { this._replic = replic; }

	/**
	 * Get the seq number
	 * @return int
	 */
	public int getSeqN() { return this._seqN; }

	/**
	 * Set the seq number
	 * @param seqN
	 */
	public void setSeqN(int seqN) { this._seqN = seqN; }

	/**
	 * Comparator of dates, used to sort
	 * @param observation
	 * @return int
	 */
	@Override
  	public int compareTo(Observation observation) {
    	return getDate().compareTo(observation.getDate());
  	}
}