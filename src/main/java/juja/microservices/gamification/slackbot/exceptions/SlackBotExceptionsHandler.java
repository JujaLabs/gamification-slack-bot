package juja.microservices.gamification.slackbot.exceptions;

import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Danil Kuznetsov
 */
@RestControllerAdvice
public class SlackBotExceptionsHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public RichMessage handleAll(Exception ex) {
        logger.warn("Other Exception': {}", ex.getMessage());
        return new RichMessage(ex.getMessage());
    }

    @ExceptionHandler(WrongCommandFormatException.class)
    public RichMessage handleWrongCommandFormatException(Exception ex){
        logger.warn("WrongCommandFormatException: {}", ex.getMessage());
        return new RichMessage(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public RichMessage handleUserNotFoundException(Exception ex){
        logger.warn("UserNotFoundException: {}", ex.getMessage());
        return new RichMessage(ex.getMessage());
    }

    @ExceptionHandler(GamificationExchangeException.class)
    public RichMessage handleGamificationException(Exception ex){
        logger.warn("GamificationExchangeException : {}", ex.getMessage());
        return new RichMessage(ex.getMessage());
    }

}
