package org.doframework.sample.persistence;

public class DuplicateRecordException extends RuntimeException
{
    public DuplicateRecordException(String message)
    {
        super(message);
    }
}
