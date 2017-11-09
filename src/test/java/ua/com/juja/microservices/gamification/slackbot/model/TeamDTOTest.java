package ua.com.juja.microservices.gamification.slackbot.model;

import ua.com.juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TeamDTOTest {

    @Test
    public void shouldReturnTeam() {
        Set<String> expectedMembers = new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4"));
        TeamDTO team = new TeamDTO(expectedMembers);
        assertNotNull(team);
        assertEquals(expectedMembers, team.getMembers());
    }
}
