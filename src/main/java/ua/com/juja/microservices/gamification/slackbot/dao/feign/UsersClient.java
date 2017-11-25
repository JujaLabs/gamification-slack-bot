package ua.com.juja.microservices.gamification.slackbot.dao.feign;

import ua.com.juja.microservices.gamification.slackbot.model.DTO.SlackNameRequest;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.UuidRequest;
import ua.com.juja.slack.command.handler.model.UserDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Set;

/**
 * @author Ivan Shapovalov
 */
@FeignClient(name = "gateway")
public interface UsersClient {
    @RequestMapping(method = RequestMethod.POST, value = "/v1/users/usersBySlackNames",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<UserDTO> findUsersBySlackNames(SlackNameRequest request);

    @RequestMapping(method = RequestMethod.POST, value = "/v1/users/usersByUuids",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Set<UserDTO> findUsersByUuids(UuidRequest request);
}
