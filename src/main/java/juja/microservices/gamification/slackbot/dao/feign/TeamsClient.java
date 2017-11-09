package juja.microservices.gamification.slackbot.dao.feign;

import juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Ivan Shapovalov
 */
@FeignClient(name = "gateway")
public interface TeamsClient {
    @RequestMapping(method = RequestMethod.GET, value = "/v1/teams/users/{uuid}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    TeamDTO getTeamByUserUuid(@RequestParam(value = "uuid") String uuid);
}
