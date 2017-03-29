package juja.microservices.gamification.slackbot.exceptions;

/**
 * Created by Nikol on 3/13/2017.
 */
public class WrongCommandFormatException extends RuntimeException {
    public WrongCommandFormatException(String message) {
        super(message);
    }
}
