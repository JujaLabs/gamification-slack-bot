package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.TeamRepository;
import juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(TeamService.class)
public class DefaultTeamServiceTest {

    @Inject
    private TeamService service;

    @MockBean
    private TeamRepository repository;

    @Test
    public void getTeamByUuid(){
        //given
        TeamDTO expectedTeam = new TeamDTO(new LinkedHashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        System.out.println(expectedTeam.getMembers());

        //when
        when(repository.getTeamByUserUuid("uuid")).thenReturn(expectedTeam);
        TeamDTO actualTeam = service.getTeamByUserUuid("uuid");

        //then
        assertEquals(expectedTeam, actualTeam);
    }
}