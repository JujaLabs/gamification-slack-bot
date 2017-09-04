package juja.microservices.gamification.slackbot.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.gamification.slackbot.exceptions.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Danil Kuznetsov
 */

public abstract class AbstractRestRepository {

    protected HttpHeaders setupBaseHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    protected ApiError convertToApiError(HttpClientErrorException httpClientErrorException, String serviceName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(httpClientErrorException.getResponseBodyAsString(), ApiError.class);
        } catch (IOException e) {
            return new ApiError(
                    500, "BotInternalError",
                    "I'm, sorry. I cannot parse api error message from remote ".concat(serviceName)
                            .concat(" service :("),
                    "Cannot parse api error message from remote ".concat(serviceName).concat( " service"),
                    e.getMessage(),
                    Arrays.asList(httpClientErrorException.getMessage())
            );
        }
    }
}
