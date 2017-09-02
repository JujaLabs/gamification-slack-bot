package juja.microservices.gamification.slackbot.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Ivan Shapovalov
 */
@Slf4j
public class Utils {

    public static HttpHeaders setupJsonHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    public static ApiError convertToApiError(HttpClientErrorException httpClientErrorException) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(httpClientErrorException.getResponseBodyAsString(), ApiError.class);
        } catch (IOException e) {
            return new ApiError(
                    500, "BotInternalError",
                    "I'm, sorry. I cannot parse api error message from remote service :(",
                    "Cannot parse api error message from remote service",
                    e.getMessage(),
                    Collections.singletonList(httpClientErrorException.getMessage())
            );
        }
    }
}
