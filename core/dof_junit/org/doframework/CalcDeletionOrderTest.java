package org.doframework;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

public class CalcDeletionOrderTest
{

    @Test
    public void testCalcObjectDeletionOrder()
    {
        Map<String, ObjectDeletionHelper> classNameToScratchObjectDeletionHelper =
                new HashMap<String, ObjectDeletionHelper>();
        int countClasses = 0;
        classNameToScratchObjectDeletionHelper
                .put(A.class.getName(),
                     new StubScratchObjectDeletionHelper(new Class[]{B.class, C.class}));
        countClasses++;

        classNameToScratchObjectDeletionHelper
                .put(B.class.getName(),
                     new StubScratchObjectDeletionHelper(new Class[]{D.class}));
        countClasses++;


        classNameToScratchObjectDeletionHelper
                .put(C.class.getName(),
                     new StubScratchObjectDeletionHelper(new Class[]{}));
        countClasses++;

        classNameToScratchObjectDeletionHelper
                .put(D.class.getName(), new StubScratchObjectDeletionHelper(new Class[]{}));
        countClasses++;

        classNameToScratchObjectDeletionHelper
                .put(E.class.getName(), new StubScratchObjectDeletionHelper(new Class[]{B.class, C.class}));
        countClasses++;

        classNameToScratchObjectDeletionHelper
                .put(F.class.getName(), new StubScratchObjectDeletionHelper(new Class[]{A.class, E.class}));
        countClasses++;

        classNameToScratchObjectDeletionHelper
                .put(G.class.getName(), new StubScratchObjectDeletionHelper(new Class[]{F.class, E.class, D.class}));
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
                            .get(aClass.getName());
            // Check that all dependencies are lower
            Class[] parentDependencyClasses = ssodh.getDependencyClasses();
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


    static class StubScratchObjectDeletionHelper implements ObjectDeletionHelper
    {
        Class[] dependencies;


        StubScratchObjectDeletionHelper(Class[] dependencies)
        {
            this.dependencies = dependencies;
        }


        public boolean delete(Object object) { return false; }

        public Object[] getDependencies(Object object) { return new Object[0]; }

        public Class[] getDependencyClasses()
        {
            return dependencies;
        }


        public Object extractPrimaryKey(Object object)
        {
            return null;
        }
    }






}
