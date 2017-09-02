package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
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
    @Value("${gamification.rest.api.version}")
    private String gamificationRestApiVersion;
    @Value("${gamification.baseURL}")
    private String gamificationBaseUrl;
    @Value("${gamification.endpoint.daily}")
    private String gamificationDailyUrl;
    @Value("${gamification.endpoint.codenjoy}")
    private String gamificationCodenjoyUrl;
    @Value("${gamification.endpoint.thanks}")
    private String gamificationThanksUrl;
    @Value("${gamification.endpoint.interview}")
    private String gamificationInterviewUrl;
    private String gamificationFullThanksUrl;
    private String gamificationFullDailyUrl;
    private String gamificationFullCodenjoyUrl;
    private String gamificationFullInterviewUrl;

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

        gamificationFullThanksUrl = gamificationBaseUrl + "/" + gamificationRestApiVersion + gamificationThanksUrl;
        gamificationFullDailyUrl = gamificationBaseUrl + "/" + gamificationRestApiVersion + gamificationDailyUrl;
        gamificationFullCodenjoyUrl = gamificationBaseUrl + "/" + gamificationRestApiVersion + gamificationCodenjoyUrl;
        gamificationFullInterviewUrl = gamificationBaseUrl + "/" + gamificationRestApiVersion + gamificationInterviewUrl;
    }

    @Test
    public void shouldReturnIdAchievementWhenSendDailyToRemoteGamificationService() {
        //given
        String expectedRequestBody = "{\"description\":\"description\",\"from\":\"101\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(gamificationFullDailyUrl))
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
        mockServer.expect(requestTo(gamificationFullDailyUrl))
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
        mockServer.expect(requestTo(gamificationFullCodenjoyUrl))
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
        mockServer.expect(requestTo(gamificationFullCodenjoyUrl))
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
        mockServer.expect(requestTo(gamificationFullThanksUrl))
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
        mockServer.expect(requestTo(gamificationFullThanksUrl))
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
        mockServer.expect(requestTo(gamificationFullInterviewUrl))
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
        mockServer.expect(requestTo(gamificationFullInterviewUrl))
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
}

