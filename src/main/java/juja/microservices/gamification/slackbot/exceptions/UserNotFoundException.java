package juja.microservices.gamification.slackbot.exceptions;

/**
 * Created by Nikolay on 4/3/2017.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
