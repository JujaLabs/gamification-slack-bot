package juja.microservices.gamification.slackbot.dao;

import feign.FeignException;
import juja.microservices.gamification.slackbot.dao.feign.GamificationClient;
import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.achievements.TeamAchievement;
import juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Danil Kuznetsov
 * @author Nikolay Horushko
 * @author Ivan Shapovalov
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestGamificationRepositoryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Inject
    private GamificationRepository gamificationRepository;
    @MockBean
    private GamificationClient gamificationClient;

    @Test
    public void shouldReturnAchievementIdWhenSendDailyToRemoteGamificationService() {
        //given
        DailyAchievement dailyAchievement = new DailyAchievement("from-uuid", "description");
        String[] expected = {"1000"};
        when(gamificationClient.saveDailyAchievement(dailyAchievement)).thenReturn(expected);

        //when
        String[] actual = gamificationRepository.saveDailyAchievement(dailyAchievement);

        // then
        assertArrayEquals(expected, actual);
        verify(gamificationClient).saveDailyAchievement(dailyAchievement);
        verifyNoMoreInteractions(gamificationClient);
    }

    @Test
    public void shouldThrowExceptionWhenSendDailyToRemoteGamificationServiceThrowsExceptionWithCorrectContent() {
        // given
        String expectedJsonResponseBody =
                "status 400 reading GamificationClient#saveDailyAchievement(); content:" +
                        "{\n" +
                        "  \"httpStatus\": 400,\n" +
                        "  \"internalErrorCode\": \"GMF-F2-D2\",\n" +
                        "  \"clientMessage\": \"Oops something went wrong :(\",\n" +
                        "  \"developerMessage\": \"General exception for this service\",\n" +
                        "  \"exceptionMessage\": \"very big and scare error\",\n" +
                        "  \"detailErrors\": []\n" +
                        "}";
        DailyAchievement dailyAchievement = new DailyAchievement("101", "description");
        FeignException feignException = mock(FeignException.class);
        when(gamificationClient.saveDailyAchievement(dailyAchievement)).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Oops something went wrong :("));

        try {
            //when
            gamificationRepository.saveDailyAchievement(dailyAchievement);

        } finally {
            //then
            verify(gamificationClient).saveDailyAchievement(dailyAchievement);
            verifyNoMoreInteractions(gamificationClient);
        }
    }

    @Test
    public void shouldThrowExceptionWhenSendDailyToRemoteGamificationServiceThrowsExceptionWithIncorrectContent() {
        // given
        DailyAchievement dailyAchievement = new DailyAchievement("from-uuid", "description");
        String expectedJsonResponseBody =
                "status 400 reading GamificationClient#saveDailyAchievement(); content: \n";
        FeignException feignException = mock(FeignException.class);
        when(gamificationClient.saveDailyAchievement(dailyAchievement)).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("I'm, sorry. I cannot parse api error message from remote service :("));

        try {
            //when
            gamificationRepository.saveDailyAchievement(dailyAchievement);

        } finally {
            //then
            verify(gamificationClient).saveDailyAchievement(dailyAchievement);
            verifyNoMoreInteractions(gamificationClient);
        }
    }

    @Test
    public void shouldReturnAchievementIdWhenSendCodenjoyToRemoteGamificationService() {
        //given
        CodenjoyAchievement codenjoyAchievement =
                new CodenjoyAchievement("from-uuid", "first-uuid", "second-uuid", "third-uuid");
        String[] expected = {"1000", "1001", "1002", "1003"};
        when(gamificationClient.saveCodenjoyAchievement(codenjoyAchievement)).thenReturn(expected);

        //when
        String[] actual = gamificationRepository.saveCodenjoyAchievement(codenjoyAchievement);

        // then
        assertArrayEquals(expected, actual);
        verify(gamificationClient).saveCodenjoyAchievement(codenjoyAchievement);
        verifyNoMoreInteractions(gamificationClient);
    }

    @Test
    public void shouldThrowExceptionWhenSendCodenjoyToRemoteGamificationServiceThrowsExceptionWithCorrectContent() {
        // given
        String expectedJsonResponseBody =
                "status 400 reading GamificationClient#saveCodenjoyAchievement(); content:" +
                        "{\n" +
                        "  \"httpStatus\": 400,\n" +
                        "  \"internalErrorCode\": \"GMF-F2-D2\",\n" +
                        "  \"clientMessage\": \"Oops something went wrong :(\",\n" +
                        "  \"developerMessage\": \"General exception for this service\",\n" +
                        "  \"exceptionMessage\": \"very big and scare error\",\n" +
                        "  \"detailErrors\": []\n" +
                        "}";
        CodenjoyAchievement codenjoyAchievement =
                new CodenjoyAchievement("from-uuid", "first-uuid", "second-uuid", "third-uuid");
        FeignException feignException = mock(FeignException.class);
        when(gamificationClient.saveCodenjoyAchievement(codenjoyAchievement)).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Oops something went wrong :("));

        try {
            //when
            gamificationRepository.saveCodenjoyAchievement(codenjoyAchievement);

        } finally {
            //then
            verify(gamificationClient).saveCodenjoyAchievement(codenjoyAchievement);
            verifyNoMoreInteractions(gamificationClient);
        }
    }

    @Test
    public void shouldReturnAchievementIdWhenSendThanksToRemoteGamificationService() {
        //given
        ThanksAchievement thanksAchievement = new ThanksAchievement("from-uuid", "to-uuid", "description");
        String[] expected = {"1000"};
        when(gamificationClient.saveThanksAchievement(thanksAchievement)).thenReturn(expected);

        //when
        String[] actual = gamificationRepository.saveThanksAchievement(thanksAchievement);

        // then
        assertArrayEquals(expected, actual);
        verify(gamificationClient).saveThanksAchievement(thanksAchievement);
        verifyNoMoreInteractions(gamificationClient);
    }

    @Test
    public void shouldThrowExceptionWhenSendThanksToRemoteGamificationServiceThrowsExceptionWithIncorrectContent() {
        // given
        ThanksAchievement thanksAchievement = new ThanksAchievement("from-uuid", "to-uuid", "description");
        String expectedJsonResponseBody =
                "status 400 reading GamificationClient#saveThanksAchievement(); content: \n";
        FeignException feignException = mock(FeignException.class);
        when(gamificationClient.saveThanksAchievement(thanksAchievement)).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("I'm, sorry. I cannot parse api error message from remote service :("));

        try {
            //when
            gamificationRepository.saveThanksAchievement(thanksAchievement);

        } finally {
            //then
            verify(gamificationClient).saveThanksAchievement(thanksAchievement);
            verifyNoMoreInteractions(gamificationClient);
        }
    }

    @Test
    public void shouldReturnAchievementIdWhenSendInterviewToRemoteGamificationService() {
        //given
        InterviewAchievement interviewAchievement = new InterviewAchievement("from-uuid", "description");
        String[] expected = {"1000"};
        when(gamificationClient.saveInterviewAchievement(interviewAchievement)).thenReturn(expected);

        //when
        String[] actual = gamificationRepository.saveInterviewAchievement(interviewAchievement);

        // then
        assertArrayEquals(expected, actual);
        verify(gamificationClient).saveInterviewAchievement(interviewAchievement);
        verifyNoMoreInteractions(gamificationClient);
    }

    @Test
    public void shouldThrowExceptionWhenSendInterviewToRemoteGamificationServiceThrowsExceptionWitIncorrectContent() {
        // given
        InterviewAchievement interviewAchievement = new InterviewAchievement("from-uuid", "description");
        String expectedJsonResponseBody =
                "status 400 reading GamificationClient#saveInterviewAchievement(); content: \n";
        FeignException feignException = mock(FeignException.class);
        when(gamificationClient.saveInterviewAchievement(interviewAchievement)).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("I'm, sorry. I cannot parse api error message from remote service :("));

        try {
            //when
            gamificationRepository.saveInterviewAchievement(interviewAchievement);

        } finally {
            //then
            verify(gamificationClient).saveInterviewAchievement(interviewAchievement);
            verifyNoMoreInteractions(gamificationClient);
        }
    }

    @Test
    public void shouldReturnAchievementIdWhenSendTeamToRemoteGamificationService() {
        //given
        Set<String> members = new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4"));
        TeamAchievement teamAchievement = new TeamAchievement("from-uuid", members);
        String[] expected = {"1000"};
        when(gamificationClient.saveTeamAchievement(teamAchievement)).thenReturn(expected);

        //when
        String[] actual = gamificationRepository.saveTeamAchievement(teamAchievement);

        // then
        assertArrayEquals(expected, actual);
        verify(gamificationClient).saveTeamAchievement(teamAchievement);
        verifyNoMoreInteractions(gamificationClient);
    }

    @Test
    public void shouldThrowExceptionWhenSendTeamToRemoteGamificationServiceThrowsExceptionWithIncorrectContent() {
        // given
        Set<String> members = new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4"));
        TeamAchievement teamAchievement = new TeamAchievement("from-uuid", members);
        String expectedJsonResponseBody =
                "status 400 reading GamificationClient#saveDailyAchievement(); content: \n";
        FeignException feignException = mock(FeignException.class);
        when(gamificationClient.saveTeamAchievement(teamAchievement)).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("I'm, sorry. I cannot parse api error message from remote service :("));

        try {
            //when
            gamificationRepository.saveTeamAchievement(teamAchievement);

        } finally {
            //then
            verify(gamificationClient).saveTeamAchievement(teamAchievement);
            verifyNoMoreInteractions(gamificationClient);
        }
    }
}