package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import juja.microservices.gamification.slackbot.model.Command;
import juja.microservices.gamification.slackbot.model.User;
import juja.microservices.gamification.slackbot.service.SlackNameHandlerService;
import juja.microservices.gamification.slackbot.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Nikol on 3/18/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SlackNameHandlerServiceTest {
    @Inject
    private SlackNameHandlerService slackNameHandlerService;
    @MockBean
    private UserService userService;
    private User defaultUser;

    @Before
    public void setup() {
        defaultUser = new User("uuid", "gmail", "slack", "skype", "linkedin", "facebook", "twitter");
    }

    @Test
    public void changeSlackNamesToUuidWhenTextWithoutSlackNames() throws Exception {
        //given
        String text = "textWithoutSlackNames";
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("textWithoutSlackNames", preparedText);
    }


    @Test
    public void changeSlackNamesToUuid() throws Exception {
        //given
        String text = "text @slack.name TexT text.";
        when(userService.findUserBySlack("@slack.name")).thenReturn(defaultUser);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text @#uuid#@ TexT text.", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidWhenTwoSlackNamesInText() throws Exception {
        //given
        String text = "text @slack.name TexT @slack.name text.";
        when(userService.findUserBySlack("@slack.name")).thenReturn(defaultUser);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text @#uuid#@ TexT @#uuid#@ text.", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidIfSlackNameWithoutSpace() throws Exception {
        String text = "text@slack.name TexT text.";
        when(userService.findUserBySlack("@slack.name")).thenReturn(defaultUser);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text@#uuid#@ TexT text.", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidIfTwoSlackNameWithoutSpace() throws Exception {
        String text = "text@slack.name TexT@slack.name text.";
        when(userService.findUserBySlack("@slack.name")).thenReturn(defaultUser);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text@#uuid#@ TexT@#uuid#@ text.", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidIfSlackNameInUpperCase() throws Exception {
        //given
        String text = "text @SLACK.NAME TexT text.";
        when(userService.findUserBySlack("@slack.name")).thenReturn(defaultUser);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text @#uuid#@ TexT text.", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidIfSomeCharSlackNameInUpperCase() throws Exception {
        //given
        String text = "text @SlACK.NaME TexT text.";
        when(userService.findUserBySlack("@slack.name")).thenReturn(defaultUser);
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("text @#uuid#@ TexT text.", preparedText);
    }


    @Test
    public void changeSlackNamesToUuidIfTextNull() throws Exception {
        String text = null;
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidIfTextEmpty() throws Exception {
        String text = "";
        //when
        String preparedText = slackNameHandlerService.replaceSlackNamesToUuids(text);
        //then
        assertEquals("", preparedText);
    }

//    todo add tests if userService not found uuid
}