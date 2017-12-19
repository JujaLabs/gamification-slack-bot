package juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static juja.microservices.gamification.slackbot.model.SlackParsedCommand.convertSlackUserInFullSlackFormat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

public class ThanksAchievementTest {
    private ObjectMapper objectMapper;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String SLACK_USER_FROM = "from";
    private static final String SLACK_USER1 = "slack1";
    private static final String SLACK_USER2 = "slack2";

    private UserDTO userFrom;
    private UserDTO user1;
    private UserDTO user2;

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
        userFrom = new UserDTO("uuid0", SLACK_USER_FROM);
        user1 = new UserDTO("uuid1", SLACK_USER1);
        user2 = new UserDTO("uuid2", SLACK_USER2);
    }

    @Test
    public void createAchievement() throws Exception {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put(SLACK_USER_FROM, userFrom);
        users.put(SLACK_USER1, user1);

        String expectedJson = String.format("{\"description\":\"Thanks for help\",\"from\":\"%s\",\"to\":\"%s\"}",
                userFrom.getUuid(),
                user1.getUuid());

        String text = String.format("Thanks %s for help",
                convertSlackUserInFullSlackFormat(user1.getSlackUser())
        );

        //when
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        ThanksAchievement thanks = new ThanksAchievement(slackParsedCommand);
        String actualJson = objectMapper.writeValueAsString(thanks);
        //then
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void createAchievementThrowExceptionIfWithoutSlackUser() throws Exception {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put(SLACK_USER_FROM, userFrom);
        String text = "Thanks without slack user";
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("We didn't find slack user in your command. 'Thanks without slack user'" +
                " You must write user's slack user for 'thanks'."));
        //when
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        new ThanksAchievement(slackParsedCommand);
    }

    @Test
    public void createAchievementThrowExceptionIfMoreThanOneSlackUser() throws Exception {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put(SLACK_USER_FROM, userFrom);
        users.put(SLACK_USER1, user1);
        users.put(SLACK_USER2, user2);

        String text = String.format("Thanks %s text %s",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser())
        );

        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString(String.format("We found 2 slack user in your command: 'Thanks %s text %s'  You can't send thanks more than one user.",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser())))
        );
        //when
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        new ThanksAchievement(slackParsedCommand);
    }
}