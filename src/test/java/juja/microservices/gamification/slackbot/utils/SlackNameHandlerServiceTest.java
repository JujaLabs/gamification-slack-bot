package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.model.User;
import juja.microservices.gamification.slackbot.service.SlackNameHandlerService;
import juja.microservices.gamification.slackbot.service.UserService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Nikol on 3/18/2017.
 */
public class SlackNameHandlerServiceTest {
    private SlackNameHandlerService slackNameHandlerService;
    private UserService userService;
    private User defaultUser;

    @Before
    public void setup(){
        userService = mock(UserService.class);
        slackNameHandlerService = new SlackNameHandlerService(userService);
        defaultUser = new User("uuid", "gmail", "slack", "skype", "linkedin", "facebook", "twitter");
    }

    @Test
    public void changeSlackNamesToUuidEmptyString() throws Exception {
        //given
        final String TEXT = "";
        when(userService.findUserBySlack("")).thenReturn(defaultUser);
        //when
        String preparedText = slackNameHandlerService.handle(TEXT);
        //then
        assertEquals("", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidStringWithoutSlackNames() throws Exception {
        //given
        final String TEXT = "string without slack names";
        when(userService.findUserBySlack("")).thenReturn(defaultUser);
        //when
        String preparedText = slackNameHandlerService.handle(TEXT);
        //then
        assertEquals("string without slack names", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidStringWithSlackName() throws Exception {
        //given
        final String TEXT = "text text @slack_name";
        defaultUser.setUuid("slack_nameUser");
        when(userService.findUserBySlack("@slack_name")).thenReturn(defaultUser);
        //when
        String preparedText = slackNameHandlerService.handle(TEXT);
        //then
        assertEquals("text text @#slack_nameUser#@", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidStringWithSlackNames() throws Exception {
        //given
        when(userService.findUserBySlack("@slack_name1")).thenReturn(new User("uuid1", "gmail", "slack", "skype", "linkedin", "facebook", "twitter"));
        when(userService.findUserBySlack("@slack_name2")).thenReturn(new User("uuid2", "gmail", "slack", "skype", "linkedin", "facebook", "twitter"));
        final String TEXT = "text text @slack_name1 text text @slack_name2 text";
        //when
        String preparedText = slackNameHandlerService.handle(TEXT);
        //then
        assertEquals("text text @#uuid1#@ text text @#uuid2#@ text", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidStringSlackNameNotSeparateWithSpace() throws Exception {
        //given
        final String TEXT = "text text@slack_name1 text text@slack_name2 text";
        when(userService.findUserBySlack("@slack_name1")).thenReturn(new User("uuid1", "gmail", "slack", "skype", "linkedin", "facebook", "twitter"));
        when(userService.findUserBySlack("@slack_name2")).thenReturn(new User("uuid2", "gmail", "slack", "skype", "linkedin", "facebook", "twitter"));
        //when
        String preparedText = slackNameHandlerService.handle(TEXT);
        //then
        assertEquals("text text@#uuid1#@ text text@#uuid2#@ text", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidWhenSlackNameCorrectButUseUpcaseForSlackNames(){
        //given
        final String TEXT = "Text texT @Slack_Name1 teXt text@Slack_Name2 text";
        when(userService.findUserBySlack("@slack_name1")).thenReturn(new User("uuid1", "gmail", "slack", "skype", "linkedin", "facebook", "twitter"));
        when(userService.findUserBySlack("@slack_name2")).thenReturn(new User("uuid2", "gmail", "slack", "skype", "linkedin", "facebook", "twitter"));
        //when
        String preparedText = slackNameHandlerService.handle(TEXT);
        //then
        assertEquals("Text texT @#uuid1#@ teXt text@#uuid2#@ text", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidWhenTextIsNull(){
        //given
        final String TEXT = null;
        //when
        String preparedText = slackNameHandlerService.handle(TEXT);
        //then
        assertEquals("", preparedText);
    }
}