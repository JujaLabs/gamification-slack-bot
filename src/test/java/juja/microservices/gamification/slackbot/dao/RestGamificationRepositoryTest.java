package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.achievements.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Danil Kuznetsov
 * @author Nikolay Horushko
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestGamificationRepositoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Inject
    private GamificationRepository gamificationRepository;
    @Inject
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    @Value("${gamification.endpoint.daily}")
    private String gamificationDailyUrl;
    @Value("${gamification.endpoint.codenjoy}")
    private String gamificationCodenjoyUrl;
    @Value("${gamification.endpoint.thanks}")
    private String gamificationThanksUrl;
    @Value("${gamification.endpoint.interview}")
    private String gamificationInterviewUrl;
    @Value("${gamification.endpoint.team}")
    private String gamificationTeamUrl;

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void shouldReturnIdAchievementWhenSendDailyToRemoteGamificationService() {
        //given
        String expectedRequestBody = "{\"description\":\"description\",\"from\":\"101\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(gamificationDailyUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess("[\"1000\"]", MediaType.APPLICATION_JSON));
        //when
        String[] result = gamificationRepository.saveDailyAchievement(new DailyAchievement("101", "description"));

        // then
        mockServer.verify();
        assertEquals(result.length, 1);
        assertEquals("[1000]", Arrays.toString(result));
    }

    @Test
    public void shouldThrowExceptionWhenSendDailyToRemoteGamificationServiceThrowException() {
        // given
        String expectedRequestBody = "{\"description\":\"description\",\"from\":\"101\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(gamificationDailyUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("{\"httpStatus\":400,\"internalErrorCode\":1," +
                        "\"clientMessage\":\"Oops something went wrong :(\"," +
                        "\"developerMessage\":\"General exception for this service\"," +
                        "\"exceptionMessage\":\"very big and scare error\",\"detailErrors\":[]}"));
        //then
        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Oops something went wrong :("));
        //when
        gamificationRepository.saveDailyAchievement(new DailyAchievement("101", "description"));
    }

    @Test
    public void shouldReturnIdAchievementWhenSendCodenjoyToRemoteGamificationService() {
        //given
        String expectedRequestBody = "{\"from\":\"Bill\",\"firstPlace\":\"Walter\",\"secondPlace\":\"Bob\",\"thirdPlace\":\"John\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(gamificationCodenjoyUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess("[\"1000\", \"1001\", \"1002\"]", MediaType.APPLICATION_JSON));
        //when
        String[] result = gamificationRepository.saveCodenjoyAchievement(new CodenjoyAchievement("Bill", "Walter", "Bob", "John"));

        // then
        mockServer.verify();
        assertEquals(3, result.length);
        assertEquals("[1000, 1001, 1002]", Arrays.toString(result));
    }

    @Test
    public void shouldThrowExceptionWhenSendCodenjoyToRemoteGamificationServiceThrowException() {
        // given
        String expectedRequestBody = "{\"from\":\"Bill\",\"firstPlace\":\"Walter\",\"secondPlace\":\"Bob\",\"thirdPlace\":\"John\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(gamificationCodenjoyUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("{\"httpStatus\":400,\"internalErrorCode\":1," +
                        "\"clientMessage\":\"Oops something went wrong :(\"," +
                        "\"developerMessage\":\"General exception for this service\"," +
                        "\"exceptionMessage\":\"very big and scare error\",\"detailErrors\":[]}"));
        //then
        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Oops something went wrong :("));
        //when
        gamificationRepository.saveCodenjoyAchievement(new CodenjoyAchievement("Bill", "Walter", "Bob", "John"));
    }

    @Test
    public void shouldReturnIdAchievementWhenSendThanksToRemoteGamificationService() {
        //given
        String expectedRequestBody = "{\"description\":\"Thanks to Bob\",\"from\":\"Bill\",\"to\":\"Bob\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(gamificationThanksUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess("[\"1000\"]", MediaType.APPLICATION_JSON));
        //when
        String[] result = gamificationRepository.saveThanksAchievement(new ThanksAchievement("Bill", "Bob", "Thanks to Bob"));

        // then
        mockServer.verify();
        assertEquals(1, result.length);
        assertEquals("[1000]", Arrays.toString(result));
    }

    @Test
    public void shouldThrowExceptionWhenSendThanksToRemoteGamificationServiceThrowException() {
        // given
        String expectedRequestBody = "{\"description\":\"Thanks to Bob\",\"from\":\"Bill\",\"to\":\"Bob\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(gamificationThanksUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("{\"httpStatus\":400,\"internalErrorCode\":1," +
                        "\"clientMessage\":\"Oops something went wrong :(\"," +
                        "\"developerMessage\":\"General exception for this service\"," +
                        "\"exceptionMessage\":\"very big and scare error\",\"detailErrors\":[]}"));
        //then
        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Oops something went wrong :("));
        //when
        gamificationRepository.saveThanksAchievement(new ThanksAchievement("Bill", "Bob", "Thanks to Bob"));
    }

    @Test
    public void shouldReturnIdAchievementWhenSendInterviewToRemoteGamificationService() {
        //given
        String expectedRequestBody = "{\"description\":\"description\",\"from\":\"bill\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(gamificationInterviewUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(),
                        containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess("[\"1000\"]", MediaType.APPLICATION_JSON));
        //when
        String[] result = gamificationRepository.saveInterviewAchievement(new InterviewAchievement("bill", "description"));

        // then
        mockServer.verify();

        assertEquals(1, result.length);
        assertEquals("[1000]", Arrays.toString(result));
    }

    @Test
    public void shouldThrowExceptionWhenSendInterviewToRemoteInterviewServiceThrowException() {
        // given
        String expectedRequestBody = "{\"description\":\"description\",\"from\":\"101\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(gamificationInterviewUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(),
                        containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("{\"httpStatus\":400,\"internalErrorCode\":1," +
                        "\"clientMessage\":\"Oops something went wrong :(\"," +
                        "\"developerMessage\":\"General exception for this service\"," +
                        "\"exceptionMessage\":\"very big and scare error\",\"detailErrors\":[]}"));
        //then
        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Oops something went wrong :("));
        //when
        gamificationRepository.saveInterviewAchievement(new InterviewAchievement("101", "description"));
    }

    @Test
    public void shouldReturnIdAchievementWhenSendTeamToRemoteGamificationService() {
        //given
        String expectedRequestBody = "{\"from\":\"uuid1\",\"members\":[\"uuid1\",\"uuid2\",\"uuid3\",\"uuid4\"]}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(gamificationTeamUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(),
                        containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess("[\"1000\", \"1001\", \"1002\", \"1003\"]", MediaType.APPLICATION_JSON));
        //when
        String[] result = gamificationRepository.saveTeamAchievement(new TeamAchievement("uuid1",
                new LinkedHashSet(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4"))));

        // then
        mockServer.verify();
        assertEquals(4, result.length);
        assertEquals("[1000, 1001, 1002, 1003]", Arrays.toString(result));
    }

    @Test
    public void shouldThrowExceptionWhenGamificationRepositoryThrowException() {
        //given
        mockServer.expect(requestTo(gamificationTeamUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest().body("bad request"));

        //then
        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("I'm, sorry. I cannot parse api error message from remote service :("));

        //when
        gamificationRepository.saveTeamAchievement(new TeamAchievement("uuid1",
                new HashSet(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4"))));
    }
}