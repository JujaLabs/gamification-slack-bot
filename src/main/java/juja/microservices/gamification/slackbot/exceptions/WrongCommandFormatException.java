package juja.microservices.gamification.slackbot.exceptions;

/**
 * @author Nikolay Horushko
 */
public class WrongCommandFormatException extends RuntimeException {
    public WrongCommandFormatException(String message) {
        super(message);
    }
}
