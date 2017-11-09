package ua.com.juja.microservices.gamification.slackbot.dao.feign;

import ua.com.juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.TeamAchievement;
import ua.com.juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Ivan Shapovalov
 */
@FeignClient(name = "gateway")
public interface GamificationClient {
    @RequestMapping(method = RequestMethod.POST, value = "/v1/gamification/achievements/daily",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String[] saveDailyAchievement(DailyAchievement daily);

    @RequestMapping(method = RequestMethod.POST, value = "/v1/gamification/achievements/codenjoy",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String[] saveCodenjoyAchievement(CodenjoyAchievement codenjoy);

    @RequestMapping(method = RequestMethod.POST, value = "/v1/gamification/achievements/thanks",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String[] saveThanksAchievement(ThanksAchievement thanks);

    @RequestMapping(method = RequestMethod.POST, value = "/v1/gamification/achievements/interview",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String[] saveInterviewAchievement(InterviewAchievement interview);

    @RequestMapping(method = RequestMethod.POST, value = "/v1/gamification/achievements/team",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String[] saveTeamAchievement(TeamAchievement team);
}
