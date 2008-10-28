package sample;

import org.doframework.*;

import javax.persistence.*;
import java.util.*;
import java.math.*;

/** Generic JPA helper methods. */
public class JpaUtility
{
    /**
     * The EntityManagerFactory has a lifetime of the application and is typically a singleton for
     * the application.
     */
    static EntityManagerFactory entityManagerFactory;

    /** The EntityManager is thread specific and used to interact with the database. */
    static EntityManager entityManager;

    private static Map configOverrides;



    /**
     * Create a new Entity Manager. Caller is responsible for closing it.
     *
     * @return new EntityManager
     */
    protected static synchronized EntityManager getEntityManager()
    {
        if (entityManager == null)
        {
            entityManager = getEntityManagerFactory().createEntityManager();
        }
        return entityManager;
    }


    /**
     * Lazily initialize EntityManagerFactory
     *
     * @todo Possibly this method can be inlined and the variable entityManagerFactory can be made locaal
     */
    static synchronized EntityManagerFactory getEntityManagerFactory()
    {
        if (entityManagerFactory == null)
        {
            entityManagerFactory = Persistence.createEntityManagerFactory("hello_dof", getConfigOverrides());
        }
        return entityManagerFactory;
    }


    public static void executeJpa(JpaOperation jpaOperation)
    {
        EntityManager entityManager = getEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        jpaOperation.execute(entityManager);
        tx.commit();
    }

    public static void dropAll()
    {
        executeNativeSql("DROP ALL OBJECTS");
        // Next line is what is used
        //executeNativeSql("drop schema PUBLIC cascade");
    }

    public static boolean tableExists(String tableName)
    {
        EntityManager entityManager = getEntityManager();
        //try
        //{
        Query nativeQuery = entityManager.createNativeQuery("select count(*) from INFORMATION_SCHEMA.TABLES where TABLE_NAME='" + tableName + "'");
        Object o = nativeQuery.getSingleResult();
        return ((BigInteger)o).intValue() == 1;
        //}
        //catch (Exception e)
        //{
        //    return false;
        //}

    }


    /**
     * JPA wrapper to execute some SQL
     * @param sql
     */
    public static void executeNativeSql(String sql)
    {
        EntityManager entityManager = getEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.executeUpdate();
        tx.commit();
    }


    /**
     * JPA wrapper to delete a persisted object
     * @param objectToDelete
     * @return
     */
    public static boolean deleteObject(final Object objectToDelete)
    {
        try
        {
            executeJpa(new JpaOperation()
            {
                public void execute(EntityManager em)
                {
                    em.remove(objectToDelete);
                }
            });
        }
        catch (Exception e)
        {
            System.out.println("Error deleting object e = " + e);
            return false;
        }
        return true;
    }


    protected static void persistObject(final Object objectToPersist)
    {
        executeJpa(new JpaOperation()
        {
            public void execute(EntityManager em)
            {
                em.persist(objectToPersist);
            }
        });
    }


    protected static Object fetchObject(String queryText, Object[] args)
    {
        EntityManager entityManager = getEntityManager();
        Query query = entityManager
                .createQuery(queryText);
        for (int i = 0; i < args.length; i++)
        {
            query.setParameter(i + 1, args[i]);
        }

        List resultList = query.getResultList();
        if (resultList.size() == 1)
        {
            Object o = resultList.get(0);
            return o;
        }
        else
        {
            return null;
        }
    }


    static Map getConfigOverrides()
    {
        return configOverrides;
    }


    /**
     * Call this method to over
     * @param configOverrides
     */
    public static void setConfigOverrides(Map configOverrides)
    {
        JpaUtility.configOverrides = configOverrides;
        entityManagerFactory = null;
        entityManager = null;
    }


    static void createSchema(String schemaFileName)
    {
        System.out.println("Creating Schema");
        String schemaSql = DOFGlobalSettings.getResourceAsStringFromDofDefsDir(schemaFileName);
        for (StringTokenizer stringTokenizer = new StringTokenizer(schemaSql, ";");
             stringTokenizer.hasMoreTokens();)
        {
            String sql = stringTokenizer.nextToken();
            sql = sql.trim();
            if (sql.length() == 0)
            {
                continue;
            }
            //System.out.println("sql = " + sql);
            executeNativeSql(sql.toString());
        }
        System.out.println("Schema completion successful");
    }


    static interface JpaOperation
    {
        void execute(EntityManager em);
    }
}
