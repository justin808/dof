package org.justingordon;

import org.junit.*;

public class ExceptionTest
{

    @Test(expected = java.lang.NullPointerException.class)
    public void testExceptionThrownPasses()
    {
        Object object = null;
        String stringValue = object.toString();
    }

    @Test
    @Ignore
    public void testExceptionThrownFails()
    {
        Object object = null;
        String stringValue = object.toString();
    }


    @Ignore
    @Test
    public void testExceptionThrownFailsIgnored()
    {
        Object object = null;
        String stringValue = object.toString();
    }


}
