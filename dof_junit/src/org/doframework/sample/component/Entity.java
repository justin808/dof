package org.doframework.sample.component;


/**
 * Base classs of all Entity objects. Provides functionality around the unique ID and whether the entity exists in the
 * database yet.
 * <p/>
 * Note: Entity subclasses are stored in the component package so that component classes have access to package level
 * methods on the entities.
 */
abstract class Entity
{

    protected int id;
    private boolean isNew;


    Entity(int id)
    {
        this.id = id;
    }



    public int getId()
    {
        return id;
    }


    /**
     * Set this flag to configure whether persisting this object will "insert" or "update"
     * @param isNew if true, then persistence will "insert", else will "update"
     */
    public Entity setNew(boolean isNew)
    {
        this.isNew = isNew;
        return this;
    }


    public boolean isNew()
    {
        return isNew;
    }

}