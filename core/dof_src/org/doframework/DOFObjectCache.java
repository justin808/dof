package org.doframework;

import java.util.*;

// TODO: distinguish between scratch and regular


public class DOFObjectCache
{
    MaxSizeObjectCache referenceObjects;
    MaxSizeObjectCache scratchObjects;


    public DOFObjectCache()
    {
        referenceObjects = new MaxSizeObjectCache(DOFGlobalSettings.getInstance().getMaxCachedReferenceObjects());
        scratchObjects = new MaxSizeObjectCache(DOFGlobalSettings.getInstance().getMaxCachedScratchObjects());
    }

    public void put(Object pk, Object storedObject)
    {
        String key = getKeyForHashing(storedObject.getClass(), pk);
        referenceObjects.put(key, storedObject);
    }


    public void put(ReferenceBuilder referenceBuilder, Object storedObject)
    {
        String key = getKeyForHashing(storedObject.getClass(), referenceBuilder.getPrimaryKey());
        referenceObjects.put(key, storedObject);
    }


    public void put(ScratchBuilder scratchBuilder, Object storedObject)
    {
        String key = getKeyForHashing(storedObject.getClass(), ((ScratchBuilder)scratchBuilder).extractPrimaryKey(storedObject));
        scratchObjects.put(key, storedObject);
    }


    public Object get(Class clazz, Object pk)
    {
        String key = getKeyForHashing(clazz, pk);
        Object object = referenceObjects.get(key);
        if (object != null)
        {
            return object;
        }
        object = scratchObjects.get(key);
        return object;
    }


    public Object get(ObjectFileInfo ofi)
    {
        String key = ofi.getKeyForHashing();
        Object cachedObject = referenceObjects.get(key);
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
        Object cachedObject = referenceObjects.get(key);
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
        Object foundObject = scratchObjects.get(key);
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
        boolean removedFromReferenceObjects = (referenceObjects.remove(key) != null);
        if (!removedFromReferenceObjects)
        {
            return (scratchObjects.remove(key) != null);
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
        Map map = objectFileInfo.isScratchMode() ? scratchObjects : referenceObjects;
        map.put(key, resultObject);
    }


    public boolean containsKey(ObjectFileInfo dependencyObjectFileInfo)
    {
        String key = dependencyObjectFileInfo.getKeyForHashing();
        Map map = dependencyObjectFileInfo.isScratchMode() ? scratchObjects : referenceObjects;
        return map.containsKey(key);
    }


    public MaxSizeObjectCache getScratchObjects()
    {
        return scratchObjects;
    }


    public MaxSizeObjectCache getReferenceObjects()
    {
        return referenceObjects;
    }


    static class MaxSizeObjectCache extends LinkedHashMap<String, Object>
    {
        int maxSize;


        public MaxSizeObjectCache(int maxSize)
        {
            super(16, (float) .75, true);
            this.maxSize = maxSize;
        }


        protected boolean removeEldestEntry(Map.Entry<String, Object> eldest)
        {
            return maxSize > 0 && size() > maxSize;
        }
    }

}
