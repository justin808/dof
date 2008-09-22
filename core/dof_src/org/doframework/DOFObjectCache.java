package org.doframework;

import java.util.*;

// TODO: distinguish between scratch and regular


public class DOFObjectCache
{
    LoadedObjectCache referenceObjects;
    LoadedObjectCache scratchObjects;


    public DOFObjectCache()
    {
        referenceObjects = new LoadedObjectCache(DOFGlobalSettings.getInstance().getMaxCachedReferenceObjects());
        scratchObjects = new LoadedObjectCache(DOFGlobalSettings.getInstance().getMaxCachedScratchObjects());
    }

    public void put(Object pk, DOFBuilder dofBuilder, Object storedObject)
    {
        String key = getKeyForHashing(storedObject.getClass(), pk);
        LoadedObject loadedObject = new LoadedObject(dofBuilder, storedObject);
        referenceObjects.put(key, loadedObject);
    }


    public void put(ReferenceBuilder referenceBuilder, Object storedObject)
    {
        String key = getKeyForHashing(storedObject.getClass(), referenceBuilder.getPrimaryKey());
        LoadedObject loadedObject = new LoadedObject(referenceBuilder, storedObject);
        referenceObjects.put(key, loadedObject);
    }


    public void put(ScratchBuilder scratchBuilder, Object storedObject)
    {
        String key = getKeyForHashing(storedObject.getClass(), ((ScratchBuilder)scratchBuilder).extractPrimaryKey(storedObject));
        LoadedObject loadedObject = new LoadedObject(scratchBuilder, storedObject);
        scratchObjects.put(key, loadedObject);
    }


    public Object get(Class clazz, Object pk)
    {
        String key = getKeyForHashing(clazz, pk);
        LoadedObject loadedObject = referenceObjects.get(key);
        if (loadedObject != null)
        {
            return loadedObject.storedObject;
        }
        loadedObject = scratchObjects.get(key);
        return (loadedObject != null ?loadedObject.storedObject : null);
    }


    public Object get(ObjectFileInfo ofi)
    {
        String key = ofi.getKeyForHashing();
        LoadedObject loadedObject = referenceObjects.get(key);
        Object cachedObject = loadedObject != null ? loadedObject.storedObject : null;
        if (cachedObject != null)
        {
            return cachedObject;
        }

        DependentObjectHandler dependentObjectHandler = DOFGlobalSettings.getInstance().
                getDependentObjectHandlerForObjectTypeFileType(ofi.getObjectType(),
                                                               ofi.getFileType());
        Object fetchedObject = dependentObjectHandler.get(ofi);
        if (fetchedObject != null)
        {
            put(ofi, fetchedObject);
        }
        return fetchedObject;
    }


    public Object get(ReferenceBuilder referenceBuilder)
    {
        String key = getReferenceObjectHashKey(referenceBuilder);
        LoadedObject loadedObject = referenceObjects.get(key);
        Object cachedObject = loadedObject != null ? loadedObject.storedObject : null;
        if (cachedObject != null)
        {
            return cachedObject;
        }

        Object fetchedObject = referenceBuilder.fetch();
        if (fetchedObject != null)
        {
            put(referenceBuilder, fetchedObject);
        }
        return fetchedObject;
    }


    public Object get(ScratchBuilder scratchBuilder, String scratchRequestedPk)
    {
        String key = getScratchObjectHashKeyFromPk(scratchBuilder, scratchRequestedPk);
        LoadedObject loadedObject = scratchObjects.get(key);
        Object foundObject = loadedObject != null ? loadedObject.storedObject : null;
        if (foundObject != null)
        {
            return foundObject;
        }

        // else check the DB
        Object fetchedObject = scratchBuilder.fetch(scratchRequestedPk);
        if (fetchedObject != null)
        {
            put(scratchBuilder, fetchedObject);
        }
        return fetchedObject;
    }


    private static String getReferenceObjectHashKey(ReferenceBuilder rb)
    {
        Object pk = rb.getPrimaryKey();
        return getJavaBuilderHashKey(rb, pk);
    }


    private static String getJavaBuilderHashKey(DOFBuilder rb, Object pk)
    {
        return rb.getCreatedClass().getName() + ":" + pk;
    }


    private static String getScratchObjectHashKeyFromPk(ScratchBuilder sb, Object pk)
    {
        return getJavaBuilderHashKey(sb, pk);
    }


    private static String getScratchObjectHashKey(ScratchBuilder sb, Object object)
    {
        return getJavaBuilderHashKey(sb, sb.extractPrimaryKey(object));
    }


    public Object getLoadedObject(Class clazz, Object pk)
    {
        String key = getKeyForHashing(clazz, pk);
        return referenceObjects.get(key);
    }


    /**
     * @return objectType + ":" + pk
     */
    public static String getKeyForHashing(Class clazz, Object pk)
    {
        return clazz.getName() + ":" + pk;
    }


    public boolean remove(Object objectToRemove, Object objectPrimaryKey)
    {
        String key = getKeyForHashing(objectToRemove.getClass(), objectPrimaryKey);
        boolean removedFromReferenceObjects = referenceObjects.remove(key) != null;
        if (!removedFromReferenceObjects)
        {
            return scratchObjects.remove(key) != null;
        }
        return removedFromReferenceObjects;
    }


    public boolean remove(ObjectFileInfo objectFileInfo)
    {
        String key = objectFileInfo.getKeyForHashing();
        Map map = objectFileInfo.isScratchMode() ? scratchObjects : referenceObjects;
        return (map.remove(key) != null);
    }


    public void put(ObjectFileInfo objectFileInfo, Object resultObject)
    {
        String key = objectFileInfo.getKeyForHashing();
        DOFObjectCache.LoadedObject loadedObject =
                new DOFObjectCache.LoadedObject(objectFileInfo.getFileToLoad(), resultObject);
        Map map = objectFileInfo.isScratchMode() ? scratchObjects : referenceObjects;
        map.put(key, loadedObject);
    }


    public boolean containsKey(ObjectFileInfo dependencyObjectFileInfo)
    {
        String key = dependencyObjectFileInfo.getKeyForHashing();
        Map map = dependencyObjectFileInfo.isScratchMode() ? scratchObjects : referenceObjects;
        return map.containsKey(key);
    }


    public Collection<LoadedObject> getScratchLoadedObjects()
    {
        return scratchObjects.values();
    }


    public Collection<LoadedObject> getReferenceLoadedObjects()
    {
        return referenceObjects.values();
    }


    static class LoadedObjectCache extends LinkedHashMap<String, LoadedObject>
    {
        int maxSize;


        public LoadedObjectCache(int maxSize)
        {
            super(16, (float) .75, true);
            this.maxSize = maxSize;
        }


        protected boolean removeEldestEntry(Map.Entry<String, LoadedObject> eldest)
        {
            return size() > maxSize;
        }
    }

    public static class LoadedObject
    {
        // The actual object created by text or Java
        Object storedObject;

        // One of these will be null

        // The builder class for Java objects
        DOFBuilder dofBuilder;

        // The file for Java objects
        String fileName;

        LoadedObject(String fileName, Object storedObject)
        {
            this.fileName = fileName;
            this.storedObject = storedObject;
        }


        LoadedObject(DOFBuilder dofBuilder, Object storedObject)
        {
            this.dofBuilder = dofBuilder;
            this.storedObject = storedObject;
        }


    }
}
