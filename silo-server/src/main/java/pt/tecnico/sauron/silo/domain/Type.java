package pt.tecnico.sauron.silo.domain;

public abstract class Type {
    public String _id;
    public String _name;

    /**
     * Get the id of the type
     * @return
     */
    public String getId() { return this._id; }

    /**
     * Get the type name(car or person)
     * @return
     */
    public String toString() { return this._name; }

}