package juja.microservices.gamification.slackbot.model;

import juja.microservices.gamification.slackbot.exceptions.WrongCommandFormatException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ThanksAchievementTest {

    private ThanksAchievement expectedAchievement;

    @Before
    public void setup(){
        expectedAchievement = new ThanksAchievement("max", "bob", "Thanks for help");
    }

    @Test
    public void createAchievement() throws Exception {
        //given
        String fromUserUuid = "max";
        String text = "Thanks @#bob#@ for help";
        //when
        ThanksAchievement actualAchievement = new ThanksAchievement(fromUserUuid, text);
        //then
        assertEquals(expectedAchievement.toString(), actualAchievement.toString());
    }

    @Test(expected = WrongCommandFormatException.class)
    public void createAchievementWrongCommand() throws Exception {
        //given
        String fromUserUuid = "max";
        String text = "Thanks @#bob#@ @#max#@ for help";
        //when
        ThanksAchievement actualAchievement = new ThanksAchievement(fromUserUuid, text);
        //then
        fail();
    }

    @Test(expected = WrongCommandFormatException.class)
    public void createAchievementWithoutUserTo() throws Exception {
        //given
        String fromUserUuid = "max";
        String text = "Thanks for help";
        //when
        ThanksAchievement actualAchievement = new ThanksAchievement(fromUserUuid, text);
        //then
        fail();
    }
}