package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.dao.DummyUserRepository;
import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Nikol on 3/18/2017.
 */
public class NameHandlerTest {
    private NameHandler nameHandler;

    @Before
    public void setup(){
        nameHandler = new NameHandler(new DummyUserRepository());
    }

    @Test
    public void changeSlackNamesToUuidEmptyString() throws Exception {
        //given
        final String TEXT = "";
        //when
        String preparedText = nameHandler.changeSlackNamesToUuid(TEXT);
        //then
        assertEquals("", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidStringWithoutSlackNames() throws Exception {
        //given
        final String TEXT = "string without slack names";
        //when
        String preparedText = nameHandler.changeSlackNamesToUuid(TEXT);
        //then
        assertEquals("string without slack names", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidStringWithSlackName() throws Exception {
        //given
        final String TEXT = "text text @slack_name";
        //when
        String preparedText = nameHandler.changeSlackNamesToUuid(TEXT);
        //then
        assertEquals("text text @#slack_nameUser#@", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidStringWithSlackNames() throws Exception {
        //given
        final String TEXT = "text text @slack_name1 text text @slack_name2 text";
        //when
        String preparedText = nameHandler.changeSlackNamesToUuid(TEXT);
        //then
        assertEquals("text text @#slack_name1User#@ text text @#slack_name2User#@ text ", preparedText);
    }

    @Test
    public void changeSlackNamesToUuidStringSlackNameNotSeparateWithSpace() throws Exception {
        //given
        final String TEXT = "text text@slack_name1 text text@slack_name2 text";
        //when
        String preparedText = nameHandler.changeSlackNamesToUuid(TEXT);
        //then
        assertEquals("text text@#slack_name1User#@ text text@#slack_name2User#@ text ", preparedText);
    }

    @Test(expected = WrongCommandFormatException.class)
    public void changeSlackNamesToUuidStringIsTextNull() throws Exception {
        //given
        final String TEXT = null;
        //when
        nameHandler.changeSlackNamesToUuid(TEXT);
    }
}