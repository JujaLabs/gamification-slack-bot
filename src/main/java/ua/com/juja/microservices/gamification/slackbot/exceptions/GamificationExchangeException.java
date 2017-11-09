package ua.com.juja.microservices.gamification.slackbot.exceptions;

/**
 * @author Danil Kuznetsov
 */
public class GamificationExchangeException extends BaseBotException {
    public GamificationExchangeException(ApiError error, Exception ex) {
        super(error, ex);
    }
}