package me.zachcheatham.pixelshow.show.exception;

/**
 * Thrown when a show's length is being updated to a shorter length that would erase effects.
 */
public class ShorterLengthException extends ShowException
{
    public ShorterLengthException(String message)
    {
        super(message);
    }
}
