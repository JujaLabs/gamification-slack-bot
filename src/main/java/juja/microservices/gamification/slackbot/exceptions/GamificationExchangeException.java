package juja.microservices.gamification.slackbot.exceptions;

import org.springframework.web.client.HttpClientErrorException;

/**
 * @author Danil Kuznetsov
 */
public class GamificationExchangeException extends RuntimeException {
    public GamificationExchangeException(String message, HttpClientErrorException ex) {
        super(message, ex);
    }

    public GamificationExchangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
