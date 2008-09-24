package org.doframework.sample.component;

public class InvoiceLimitExceededException extends RuntimeException
{
	private static final long serialVersionUID = -89598395471997444L;

	public InvoiceLimitExceededException(String message)
    {
        super(message);
    }
}
