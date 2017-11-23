package juja.microservices.gamification.slackbot.model;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Nikolay Horushko
 */
public class SlackParsedCommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getUsersByTokens1() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = "-t1 @slack1 -t2 @slack2";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slack=@slack1), -t2=UserDTO(uuid=uuid2, slack=@slack2)}",
                result.toString());
    }

    @Test
    public void getUsersByTokens2() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = "-t2 @slack2 -t1 @slack1";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slack=@slack1), -t2=UserDTO(uuid=uuid2, slack=@slack2)}",
                result.toString());
    }

    @Test
    public void getUsersByTokens3() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = "text -t2 @slack2 text -t1 @slack1 text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slack=@slack1), -t2=UserDTO(uuid=uuid2, slack=@slack2)}",
                result.toString());
    }

    @Test
    public void getUsersByTokens4() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = "text -t2 @slack2 text -t1 @slack1 text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slack=@slack1), -t2=UserDTO(uuid=uuid2, slack=@slack2)}",
                result.toString());
    }

    @Test
    public void getUsersByTokens5() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = "text -t2 @slack2 -t1text @slack1 text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slack=@slack1), -t2=UserDTO(uuid=uuid2, slack=@slack2)}",
                result.toString());
    }

    @Test
    public void getUsersByTokens6() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));
        users.put("@slack3", new UserDTO("uuid3", "@slack3"));

        String[] tokens = new String[]{"-t1", "-t2", "-t3"};
        String text = "text -t2 @slack2 -t1 text @slack1 text -t3 @slack3";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //when
        Map<String, UserDTO> result = slackParsedCommand.getUsersWithTokens(tokens);
        //then
        assertEquals("{-t1=UserDTO(uuid=uuid1, slack=@slack1), -t2=UserDTO(uuid=uuid2, slack=@slack2)," +
                " -t3=UserDTO(uuid=uuid3, slack=@slack3)}", result.toString());
    }

    @Test
    public void getUsersByTokensThrowExceptionIfTokenNotFound() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = "text-t2@slack2 text@slack1 text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("Token '-t1' didn't find in the string 'text-t2@slack2 text@slack1 text'"));
        //when
        slackParsedCommand.getUsersWithTokens(tokens);
    }

    @Test
    public void getUsersByTokensError1() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = "text-t2 @slack2 text -t1 text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("The text 'text-t2 @slack2 text -t1 text' doesn't " +
                "contain slackName for token '-t1'"));
        //when
        slackParsedCommand.getUsersWithTokens(tokens);
    }

    @Test
    public void getUsersByTokensError2() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = "text-t2 -t1@slack2 text text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("The text 'text-t2 -t1@slack2 text text' doesn't contain " +
                "slackName for token '-t2'"));
        //when
        slackParsedCommand.getUsersWithTokens(tokens);
    }

    @Test
    public void getUsersByTokensErrorTextContainsMoreThanOneToken() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));

        String[] tokens = new String[]{"-t1", "-t2"};
        String text = "text-t2 -t1@slack2 text -t1 text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("The text 'text-t2 -t1@slack2 text -t1 text' contains 2 tokens '-t1'," +
                " but expected 1"));
        //when
        slackParsedCommand.getUsersWithTokens(tokens);
    }

    @Test
    public void getFirstUserInText() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));

        List<String> slackNames = Arrays.asList("@slack1");
        String text = "text text @slack1 text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //when
        UserDTO result = slackParsedCommand.getFirstUser();
        //then
        assertEquals("UserDTO(uuid=uuid1, slack=@slack1)", result.toString());
    }

    @Test
    public void getFirstUserInTextThrowExceptionIfNotUser() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));

        String text = "text text text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //then
        thrown.expect(WrongCommandFormatException.class);
        thrown.expectMessage(containsString("The text 'text text text' doesn't contains slackName"));
        //when
        slackParsedCommand.getFirstUser();
    }

    @Test
    public void getAllUsers() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));
        users.put("@slack1", new UserDTO("uuid1", "@slack1"));
        users.put("@slack2", new UserDTO("uuid2", "@slack2"));
        users.put("@slack3", new UserDTO("uuid3", "@slack3"));

        String text = "text @slack2 text@slack1 text @slack3";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //when
        List<UserDTO> result = slackParsedCommand.getAllUsers();
        //then
        assertEquals("[UserDTO(uuid=uuid3, slack=@slack3), UserDTO(uuid=uuid2, slack=@slack2), " +
                "UserDTO(uuid=uuid1, slack=@slack1)]", result.toString());
    }

    @Test
    public void getText() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));

        String text = "text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("@from", text, users);
        //when
        String result = slackParsedCommand.getText();
        //then
        assertEquals("text", result);
    }

    @Test
    public void getFromUser() {
        //given
        Map<String, UserDTO> users = new HashMap<>();
        users.put("@from", new UserDTO("uuid0", "@from"));

        String text = "text";
        SlackParsedCommand slackParsedCommand = new SlackParsedCommand("from", text, users);
        //when
        UserDTO result = slackParsedCommand.getFromUser();
        //then
        assertEquals("UserDTO(uuid=uuid0, slack=@from)", result.toString());
    }
}