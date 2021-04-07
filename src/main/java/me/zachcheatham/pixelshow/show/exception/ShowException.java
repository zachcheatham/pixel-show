package me.zachcheatham.pixelshow.show.exception;

public class ShowException extends Exception
{
    public final String displayMessage;

    public ShowException(String message)
    {
        displayMessage = message;
    }
}
