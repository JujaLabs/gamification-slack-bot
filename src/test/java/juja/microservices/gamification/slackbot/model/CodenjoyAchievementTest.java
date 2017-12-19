package juja.microservices.gamification.slackbot.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static juja.microservices.gamification.slackbot.model.SlackParsedCommand.convertSlackUserInFullSlackFormat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
public class CodenjoyAchievementTest {
    private ObjectMapper objectMapper;
    private Map<String, UserDTO> users;

    private static final String SLACK_USER_FROM = "from";
    private static final String SLACK_USER1 = "slack1";
    private static final String SLACK_USER2 = "slack2";
    private static final String SLACK_USER3 = "slack3";

    private UserDTO userFrom;
    private UserDTO user1;
    private UserDTO user2;
    private UserDTO user3;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();

        userFrom = new UserDTO("uuid0", SLACK_USER_FROM);
        user1 = new UserDTO("uuid1", SLACK_USER1);
        user2 = new UserDTO("uuid2", SLACK_USER2);
        user3 = new UserDTO("uuid3", SLACK_USER3);

        users = new HashMap<>();
        users.put(SLACK_USER_FROM, userFrom);
        users.put(SLACK_USER1, user1);
        users.put(SLACK_USER2, user2);
        users.put(SLACK_USER3, user3);
    }

    @Test
    public void createAchievementTest() throws JsonProcessingException {
        //given
        String text = String.format("-1th %s -2th %s -3th %s",
                convertSlackUserInFullSlackFormat(SLACK_USER1),
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER3)
        );
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        String actualJson = objectMapper.writeValueAsString(codenjoy);
        //then
        assertEquals(createExpectedValidJson(), actualJson);
    }

    @Test
    public void ifWrongTokensOrder() throws Exception {
        //given
        String text = String.format("-2th %s -1th %s -3th %s",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1),
                convertSlackUserInFullSlackFormat(SLACK_USER3)
        );
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);

        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        String actualJson = objectMapper.writeValueAsString(codenjoy);

        //then
        assertEquals(createExpectedValidJson(), actualJson);
    }


    @Test
    public void ifWithoutSpaces() throws Exception {
        //given
        String text = String.format("-2th%s-1th%s-3th%s",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1),
                convertSlackUserInFullSlackFormat(SLACK_USER3)
        );


        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);

        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        String actualJson = objectMapper.writeValueAsString(codenjoy);

        //then
        assertEquals(createExpectedValidJson(), actualJson);
    }

    @Test
    public void ifTextInTheCommand() throws Exception {
        //given
        String text = String.format("text -2th %s text text-3th %s -1th %s",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER3),
                convertSlackUserInFullSlackFormat(SLACK_USER1)
        );


        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);

        //when
        CodenjoyAchievement codenjoy = new CodenjoyAchievement(slackParsedCommand);
        String actualJson = objectMapper.writeValueAsString(codenjoy);

        //then
        assertEquals(createExpectedValidJson(), actualJson);
    }

    @Test
    public void ifNotSlackUserForToken() throws Exception {
        //given
        String commandText = String.format("-2th %s -3th -1th %s",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1)
        );

        String expectedExceptionMessage = "'"+commandText+"' doesn't contain slackUser for token '-3th'";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, commandText, users);

        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString(expectedExceptionMessage));

        //when
        new CodenjoyAchievement(slackParsedCommand);
    }

    @Test
    public void ifIsNotToken() throws Exception {
        //given
        String commandText = String.format("-2th %s %s -1th %s",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER3),
                convertSlackUserInFullSlackFormat(SLACK_USER1)
        );

        String expectedExceptionMessage = "Token '-3th' didn't find in the string '"+commandText+"'";

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, commandText, users);

        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString(expectedExceptionMessage));

        //when
        new CodenjoyAchievement(slackParsedCommand);
    }

    @Test
    public void ifUseTwoSameWinnerMarkers() throws Exception {
        //given
        String text = "-2th@slack2 -3th@slack3 -3th @slack4 -1th@slack1";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("The text '-2th@slack2 -3th@slack3 -3th @slack4 -1th@slack1' " +
                "contains 2 tokens '-3th', but expected 1"));
        //when
        new CodenjoyAchievement(slackParsedCommand);
    }

    private String createExpectedValidJson() {
        return String.format("{\"from\":\"%s\",\"firstPlace\":\"%s\",\"secondPlace\":\"%s\",\"thirdPlace\":\"%s\"}",
                userFrom.getUuid(),
                user1.getUuid(),
                user2.getUuid(),
                user3.getUuid()
        );
    }
}