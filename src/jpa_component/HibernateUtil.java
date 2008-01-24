package jpa_component;

import org.hibernate.*;
import org.hibernate.cfg.*;
import entity.*;

public class HibernateUtil
{
    private static final SessionFactory sessionFactory;

    static
    {
        try
        {

            sessionFactory = new AnnotationConfiguration().buildSessionFactory();
        }
        catch (Throwable ex)
        {
            // Log exception!
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException
    {
        return sessionFactory.openSession();
    }
}
