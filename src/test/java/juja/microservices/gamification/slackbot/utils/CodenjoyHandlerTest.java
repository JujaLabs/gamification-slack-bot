package juja.microservices.gamification.slackbot.utils;

import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Nikol on 3/9/2017.
 */
public class CodenjoyHandlerTest {
    private CodenjoyHandler codenjoyHandler;

    @Before
    public void setup(){
        codenjoyHandler = new CodenjoyHandler();
    }

    @Test
    public void receiveCodenjoyAchievmentTest(){
        //given
        final String command = "/codenjoy -1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3";
        final CodenjoyAchievment expectedCodenjoy = new CodenjoyAchievment("from",
                "@slack_nick_name",
                "@slack_nick_name2",
                "@slack_nick_name3");
        //when
        CodenjoyAchievment codenjoy = codenjoyHandler.recieveCodenjoyAchievment("from", command);
        //then
        assertEquals(expectedCodenjoy.toString(), codenjoy.toString());
    }


}