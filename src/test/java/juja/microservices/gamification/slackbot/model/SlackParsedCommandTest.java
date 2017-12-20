package juja.microservices.gamification.slackbot.model;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static juja.microservices.gamification.slackbot.model.SlackParsedCommand.convertSlackUserInFullSlackFormat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
public class SlackParsedCommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserDTO userFrom;
    private UserDTO user1;
    private UserDTO user2;
    private UserDTO user3;

    private static final String SLACK_USER_FROM = "SLACK_USER_FROM";
    private static final String SLACK_USER1 = "slack1";
    private static final String SLACK_USER2 = "slack2";
    private static final String SLACK_USER3 = "slack3";

    @Before
    public void setup() {
        userFrom = new UserDTO("uuid0", SLACK_USER_FROM);
        user1 = new UserDTO("uuid1", SLACK_USER1);
        user2 = new UserDTO("uuid2", SLACK_USER2);
        user3 = new UserDTO("uuid3", SLACK_USER3);
    }

    @Test
    public void getUsersByTokens1() {
        //given
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();

        String[] tokens = new String[]{"-t1", "-t2"};

        String text = String.format("-t1 %s -t2 %s",
                convertSlackUserInFullSlackFormat(SLACK_USER1),
                convertSlackUserInFullSlackFormat(SLACK_USER2)
        );

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slackUser=slack1), -t2=UserDTO(uuid=uuid2, slackUser=slack2)}",
                result.toString());
    }

    @Test
    public void getUsersByTokens2() {
        //given
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();

        String[] tokens = new String[]{"-t1", "-t2"};

        String text = String.format("-t2 %s -t1 %s",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1)
        );

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slackUser=slack1), -t2=UserDTO(uuid=uuid2, slackUser=slack2)}",
                result.toString());
    }

    @Test
    public void getUsersByTokens3() {
        //given
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();

        String[] tokens = new String[]{"-t1", "-t2"};

        String text = String.format("text -t2 %s -t1 %s text",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1)
        );
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slackUser=slack1), -t2=UserDTO(uuid=uuid2, slackUser=slack2)}",
                result.toString());
    }

    @Test
    public void getUsersByTokens4() {
        //given
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();

        String[] tokens = new String[]{"-t1", "-t2"};

        String text = String.format("text -t2 %s text -t1 %s text",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1)
        );
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slackUser=slack1), -t2=UserDTO(uuid=uuid2, slackUser=slack2)}",
                result.toString());
    }

    @Test
    public void getUsersByTokens5() {
        //given
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = String.format("text -t2 %s  -t1text %s text",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1)
        );
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slackUser=slack1), -t2=UserDTO(uuid=uuid2, slackUser=slack2)}",
                result.toString());
    }

    @Test
    public void getUsersByTokens6() {
        //given
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();
        users.put(SLACK_USER3, user3);

        String[] tokens = new String[]{"-t1", "-t2", "-t3"};
        String text = String.format("text -t2 %s  -t1 text %s text -t3 %s",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1),
                convertSlackUserInFullSlackFormat(SLACK_USER3)
        );
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slackUser=slack1), -t2=UserDTO(uuid=uuid2, slackUser=slack2)," +
                " -t3=UserDTO(uuid=uuid3, slackUser=slack3)}", result.toString());
    }


    @Test
    public void getUsersByTokensThrowExceptionIfTokenNotFound() {
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = String.format("text-t2%s text%s text",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1)
        );
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);

        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("Token '-t1' didn't find in the string 'text-t2<@slack2|slack2> text<@slack1|slack1> text'"));
        slackParsedCommand.getUsersWithTokens(tokens);
    }

    @Test
    public void getUsersByTokensError1() {
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();

        String[] tokens = new String[]{"-t1", "-t2"};

        String text = String.format("text-t2 %s text -t1 text",
                convertSlackUserInFullSlackFormat(SLACK_USER2)
        );

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);

        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("The text 'text-t2 <@slack2|slack2> text -t1 text' doesn't " +
                "contain slackUser for token '-t1'"));

        slackParsedCommand.getUsersWithTokens(tokens);
    }

    @Test
    public void getUsersByTokensError2() {
        //given
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = String.format("text-t2 -t1%s text text",
                convertSlackUserInFullSlackFormat(SLACK_USER2)
        );
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("The text 'text-t2 -t1<@slack2|slack2> text text' doesn't contain " +
                "slackUser for token '-t2'"));
        //when
        slackParsedCommand.getUsersWithTokens(tokens);
    }

    @Test
    public void getUsersByTokensErrorTextContainsMoreThanOneToken() {
        //given
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();

        String[] tokens = new String[]{"-t1", "-t2"};

        String text = String.format("text-t2 -t1%s text -t1 text",
                convertSlackUserInFullSlackFormat(SLACK_USER2)
        );

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("The text 'text-t2 -t1<@slack2|slack2> text -t1 text' contains 2 tokens '-t1'," +
                " but expected 1"));
        //when
        slackParsedCommand.getUsersWithTokens(tokens);
    }

    @Test
    public void getFirstUserInText() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put(SLACK_USER_FROM, userFrom);
        users.put(SLACK_USER1, user1);

        List<String> slackUsers = Collections.singletonList(SLACK_USER1);

//        String text = "text text @slack1 text";
        String text = String.format("text text %s text",
                convertSlackUserInFullSlackFormat(SLACK_USER1)
        );

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        UserDTO result = slackParsedCommand.getFirstUser();
        //then
        assertEquals("UserDTO(uuid=uuid1, slackUser=slack1)", result.toString());
    }

    @Test
    public void getFirstUserInTextThrowExceptionIfNotUser() {
        //given
        Map<String, UserDTO> users = Collections.singletonMap(SLACK_USER_FROM, userFrom);

        String text = "text text text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("The text 'text text text' doesn't contains slackUser"));
        //when
        slackParsedCommand.getFirstUser();
    }

    @Test
    public void getAllUsers() {
        //given
        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();
        users.put(SLACK_USER3, user3);

        String text = String.format("text %s text%s text %s",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1),
                convertSlackUserInFullSlackFormat(SLACK_USER3)
        );
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        List<UserDTO> result = slackParsedCommand.getAllUsers();
        //then
        assertEquals("[UserDTO(uuid=uuid1, slackUser=slack1), UserDTO(uuid=uuid2, slackUser=slack2), " +
                "UserDTO(uuid=uuid3, slackUser=slack3)]", result.toString());
    }

    @Test
    public void getText() {
        //given
        Map<String, UserDTO> users = Collections.singletonMap(SLACK_USER_FROM, userFrom);

        String text = "text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        String result = slackParsedCommand.getText();
        //then
        assertEquals("text", result);
    }

    @Test
    public void getFromUser() {
        //given
        String text = "text";
        Map<String, UserDTO> users = Collections.singletonMap(SLACK_USER_FROM, userFrom);
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, text, users);
        //when
        UserDTO result = slackParsedCommand.getFromUser();
        //then
        assertEquals("UserDTO(uuid=uuid0, slackUser=SLACK_USER_FROM)", result.toString());
    }

    @Test
    public void getTextWithoutSlackUsers() {

        Map<String, UserDTO> users = createUsersMapWithUserFromAndTwoUser();

        String command = String.format("text %s text%s text %s",
                convertSlackUserInFullSlackFormat(SLACK_USER2),
                convertSlackUserInFullSlackFormat(SLACK_USER1),
                convertSlackUserInFullSlackFormat(SLACK_USER3)
        );
        String expectedText = "text text text";

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(SLACK_USER_FROM, command, users);

        String actualText = slackParsedCommand.getTextWithoutSlackUsers();

        assertEquals(expectedText, actualText);
    }

    private Map<String, UserDTO> createUsersMapWithUserFromAndTwoUser() {
        Map<String, UserDTO> users = new HashMap<>();
        users.put(SLACK_USER_FROM, userFrom);
        users.put(SLACK_USER1, user1);
        users.put(SLACK_USER2, user2);
        return users;
    }
}