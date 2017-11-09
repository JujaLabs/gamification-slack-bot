package ua.com.juja.microservices.gamification.slackbot.exceptions;

import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * @author Danil Kuznetsov
 */
@RestControllerAdvice
public class ExceptionsHandler {
    private RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ThreadLocal<String> responseUrl = new ThreadLocal<>();

    @Inject
    public ExceptionsHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @ExceptionHandler(Exception.class)
    public void handleAllOtherExceptions(Exception ex) {
        logger.warn("Other Exception': {}", ex.getMessage());
        sendErrorResponseAsRichMessage(new RichMessage(ex.getMessage()));
    }

    @ExceptionHandler(WrongCommandFormatException.class)
    public void handleWrongCommandFormatException(Exception ex) {
        logger.warn("WrongCommandFormatException: {}", ex.getMessage());
        sendErrorResponseAsRichMessage(new RichMessage(ex.getMessage()));
    }

    @ExceptionHandler(UserExchangeException.class)
    public void handleUserExchangeException(UserExchangeException ex) {
        logger.warn("UserExchangeException: {}", ex.detailMessage());
        sendErrorResponseAsRichMessage(new RichMessage(ex.getExceptionMessage()));
    }

    @ExceptionHandler(GamificationExchangeException.class)
    public void handleGamificationExchangeException(GamificationExchangeException ex) {
        logger.warn("GamificationExchangeException : {}", ex.detailMessage());
        sendErrorResponseAsRichMessage(new RichMessage(ex.getMessage()));
    }

    @ExceptionHandler(TeamExchangeException.class)
    public void handleTeamExchangeException(TeamExchangeException ex) {
        logger.warn("TeamExchangeException : {}", ex.detailMessage());
        sendErrorResponseAsRichMessage(new RichMessage(ex.getMessage()));
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl.set(responseUrl);
    }

    private void sendErrorResponseAsRichMessage(RichMessage richMessage) {
        try {
            restTemplate.postForObject(responseUrl.get(), richMessage, String.class);
        } catch (Exception ex) {
            logger.warn("Nested exception : '{}' with text '{}' . Unable to send response to slack", ex.getMessage(),
                    richMessage.getText());
        }
    }
}