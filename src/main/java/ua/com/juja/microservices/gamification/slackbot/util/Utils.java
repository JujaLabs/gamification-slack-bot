package ua.com.juja.microservices.gamification.slackbot.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ua.com.juja.microservices.gamification.slackbot.exceptions.ApiError;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Ivan Shapovalov
 */
@Slf4j
public class Utils {
    public static ApiError convertToApiError(String message) {
        log.debug("Start convert message to ApiError");
        int contentExists = message.indexOf("content:");
        ApiError apiError = new ApiError(
                500, "BotInternalError",
                "I'm, sorry. I cannot parse api error message from remote service :(",
                "Cannot parse api error message from remote service",
                message, Collections.singletonList(message));
        if (contentExists != -1) {
            log.debug("'Content' exists. Try to parse ApiError from message");
            String apiMessage = message.substring(contentExists + 8);
            ObjectMapper mapper = new ObjectMapper();
            try {
                apiError = mapper.readValue(apiMessage, ApiError.class);
            } catch (IOException e) {
                log.debug("Parsing ApiError failed. Create Default ApiError");
                apiError = new ApiError(
                        500, "BotInternalError",
                        "I'm, sorry. I cannot parse api error message from remote service :(",
                        "Cannot parse api error message from remote service",
                        e.getMessage(), Collections.singletonList(message)
                );
            }
        }
        return apiError;
    }
}