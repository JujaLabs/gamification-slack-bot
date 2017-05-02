package juja.microservices.gamification.slackbot.exceptions;

/**
 * @author Nikolay Horushko
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
