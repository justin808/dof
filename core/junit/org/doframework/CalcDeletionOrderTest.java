package org.doframework;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

public class CalcDeletionOrderTest
{

    @Test
    public void testCalcObjectDeletionOrder()
    {
        Map<Class, DeletionHelper> classNameToScratchObjectDeletionHelper =
                new HashMap<Class, DeletionHelper>();
        int countClasses = 0;
        classNameToScratchObjectDeletionHelper
                .put(A.class,
                     new StubScratchObjectDeletionHelper(new Class[]{B.class, C.class}));
        countClasses++;

        classNameToScratchObjectDeletionHelper
                .put(B.class,
                     new StubScratchObjectDeletionHelper(new Class[]{D.class}));
        countClasses++;


        classNameToScratchObjectDeletionHelper
                .put(C.class,
                     new StubScratchObjectDeletionHelper(new Class[]{}));
        countClasses++;

        classNameToScratchObjectDeletionHelper
                .put(D.class, new StubScratchObjectDeletionHelper(new Class[]{}));
        countClasses++;

        classNameToScratchObjectDeletionHelper
                .put(E.class, new StubScratchObjectDeletionHelper(new Class[]{B.class, C.class}));
        countClasses++;

        classNameToScratchObjectDeletionHelper
                .put(F.class, new StubScratchObjectDeletionHelper(new Class[]{A.class, E.class}));
        countClasses++;

        classNameToScratchObjectDeletionHelper
                .put(G.class, new StubScratchObjectDeletionHelper(new Class[]{F.class, E.class, D.class}));
        countClasses++;


        List<Class> deletionOrder =
                DOFGlobalSettings.calcObjectDeletionOrder(classNameToScratchObjectDeletionHelper);

        assertEquals(countClasses, deletionOrder.size());
        int myPosition = 0;
        for (Iterator classIterator = deletionOrder.iterator(); classIterator.hasNext();)
        {
            Class aClass = (Class) classIterator.next();
            StubScratchObjectDeletionHelper ssodh =
                    (StubScratchObjectDeletionHelper) classNameToScratchObjectDeletionHelper
                            .get(aClass);
            // Check that all dependencies are lower
            Class[] parentDependencyClasses = ssodh.getReferencedClasses();
            if (parentDependencyClasses != null)
            {
                for (int i = 0; i < parentDependencyClasses.length; i++)
                {
                    Class parentDependencyClass = parentDependencyClasses[i];
                    int parentPosition = deletionOrder.indexOf(parentDependencyClass);
                    assertTrue(parentPosition > myPosition);
                }
            }

            myPosition++;
        }
    }


    static class A {}
    static class B {}
    static class C {}
    static class D {}
    static class E {}
    static class F {}
    static class G {}


    static class StubScratchObjectDeletionHelper implements DeletionHelper
    {
        Class[] dependencies;


        StubScratchObjectDeletionHelper(Class[] dependencies)
        {
            this.dependencies = dependencies;
        }


        public boolean delete(Object object) { return false; }


        public boolean okToDelete(Object object)
        {
            return true;
        }


        public Object[] getReferencedObjects(Object object) { return new Object[0]; }

        public Class[] getReferencedClasses()
        {
            return dependencies;
        }


        public Class getCreatedClass()
        {
            return null;
        }


        public Object extractPrimaryKey(Object object)
        {
            return null;
        }
    }






}
