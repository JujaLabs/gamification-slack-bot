package ua.com.juja.microservices.gamification.slackbot.exceptions;

public class TeamExchangeException extends BaseBotException {
    public TeamExchangeException(ApiError error, Exception ex) {
        super(error, ex);
    }
}