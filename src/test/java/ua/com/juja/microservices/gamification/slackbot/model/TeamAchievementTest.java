package ua.com.juja.microservices.gamification.slackbot.model;

import ua.com.juja.microservices.gamification.slackbot.model.achievements.TeamAchievement;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TeamAchievementTest {

    @Test
    public void shouldReturnTeamAchievement() {

        String expectedUserFrom = "uuid1";
        Set<String> expectedMembers = new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4"));

        TeamAchievement teamAchievement = new TeamAchievement("uuid1",
                new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));

        assertNotNull(teamAchievement);
        assertEquals(expectedUserFrom, teamAchievement.getFromUuid());
        assertEquals(expectedMembers, teamAchievement.getMembers());
    }
}