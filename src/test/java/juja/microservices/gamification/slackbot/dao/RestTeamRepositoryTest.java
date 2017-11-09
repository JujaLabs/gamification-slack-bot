package juja.microservices.gamification.slackbot.dao;

import feign.FeignException;
import juja.microservices.gamification.slackbot.dao.feign.TeamsClient;
import juja.microservices.gamification.slackbot.exceptions.TeamExchangeException;
import juja.microservices.gamification.slackbot.model.DTO.TeamDTO;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Petr Kramar
 * @author Ivan Shapovalov
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestTeamRepositoryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Inject
    private TeamRepository teamRepository;
    @MockBean
    private TeamsClient teamsClient;

    @Test
    public void findTeamByUserUuidReturnsCorrectTeam() {
        //given
        TeamDTO expected = new TeamDTO(new LinkedHashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        when(teamsClient.getTeamByUserUuid("uuid1")).thenReturn(expected);

        //when
        TeamDTO actual = teamRepository.getTeamByUserUuid("uuid1");

        //then
        assertEquals(expected.getMembers(), actual.getMembers());
        verify(teamsClient).getTeamByUserUuid("uuid1");
        verifyNoMoreInteractions(teamsClient);
    }

    @Test
    public void findTeamByUserUuidWhenTeamServerReturnsFeignExceptionWithCorrectContent() {
        //given
        String expectedJsonResponseBody =
                "status 400 reading TeamsClient#getTeamByUserUuid(uuid1); content:" +
                        "{\n" +
                        "  \"httpStatus\": 400,\n" +
                        "  \"internalErrorCode\": \"TMF-F2-D2\",\n" +
                        "  \"clientMessage\": \"You cannot get/deactivate team if the user not a member of any team!\",\n" +
                        "  \"developerMessage\": \"The reason of the exception is that user not in team\",\n" +
                        "  \"exceptionMessage\": \"The reason of the exception is that user not in team\",\n" +
                        "  \"detailErrors\": []\n" +
                        "}";
        FeignException feignException = mock(FeignException.class);
        when(teamsClient.getTeamByUserUuid("uuid1")).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        thrown.expect(TeamExchangeException.class);
        thrown.expectMessage(containsString("You cannot get/deactivate team if the user not a member of any team!"));

        try {
            //when
            teamRepository.getTeamByUserUuid("uuid1");
        } finally {
            //then
            verify(teamsClient).getTeamByUserUuid("uuid1");
            verifyNoMoreInteractions(teamsClient);
        }
    }

    @Test
    public void findTeamByUserUuidWhenTeamServerReturnsFeignExceptionWithIncorrectContent() {
        //given
        RuntimeException runtimeException =
                new RuntimeException("I'm, sorry. I cannot parse api error message from remote service :(");
        when(teamsClient.getTeamByUserUuid("uuid1")).thenThrow(runtimeException);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("I'm, sorry. I cannot parse api error message from remote service :("));

        try {
            //when
            teamRepository.getTeamByUserUuid("uuid1");
        } finally {
            //then
            verify(teamsClient).getTeamByUserUuid("uuid1");
            verifyNoMoreInteractions(teamsClient);
        }
    }
}