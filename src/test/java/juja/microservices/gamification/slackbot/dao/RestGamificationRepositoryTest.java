package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import juja.microservices.gamification.slackbot.model.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;
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
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestGamificationRepositoryTest {

    @Inject
    private GamificationRepository gamificationRepository;

    @Inject
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Value("${gamification.baseURL}")
    private String urlBase;
    @Value("${endpoint.daily}")
    private String urlSendDaily;
    @Value("${endpoint.codenjoy}")
    private String urlSendCodenjoy;
    @Value("${endpoint.thanks}")
    private String urlSendThanks;
    @Value("${endpoint.interview}")
    private String urlSendInterview;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }


    @Test
    public void shouldReturnIdAchievementWhenSendDailyToRemoteGamificationService() {
        //given
        String expectedRequestBody = "{\"from\":\"101\",\"description\":\"description\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(urlBase + urlSendDaily))
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
        String expectedRequestBody = "{\"from\":\"101\",\"description\":\"description\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(urlBase + urlSendDaily))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("bad request"));
        //then
        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Gamification Exchange Error"));
        //when
        gamificationRepository.saveDailyAchievement(new DailyAchievement("101", "description"));
    }

    @Test
    public void shouldReturnIdAchievementWhenSendCodenjoyToRemoteGamificationService() {
        //given
        String expectedRequestBody = "{\"from\":\"Bill\",\"firstPlace\":\"Walter\",\"secondPlace\":\"Bob\",\"thirdPlace\":\"John\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(urlBase + urlSendCodenjoy))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess("1000", MediaType.APPLICATION_JSON));
        //when
        String result = gamificationRepository.saveCodenjoyAchievement(new CodenjoyAchievement("Bill", "Walter", "Bob", "John"));

        // then
        mockServer.verify();
        assertThat(result, equalTo("1000"));
    }

    @Test
    public void shouldThrowExceptionWhenSendCodenjoyToRemoteGamificationServiceThrowException() {
        // given
        String expectedRequestBody = "{\"from\":\"Bill\",\"firstPlace\":\"Walter\",\"secondPlace\":\"Bob\",\"thirdPlace\":\"John\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(urlBase + urlSendCodenjoy))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("bad request"));
        //then
        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Gamification Exchange Error"));
        //when
        gamificationRepository.saveCodenjoyAchievement(new CodenjoyAchievement("Bill", "Walter", "Bob", "John"));
    }

    @Test
    public void shouldReturnIdAchievementWhenSendThanksToRemoteGamificationService() {
        //given
        String expectedRequestBody = "{\"from\":\"Bill\",\"to\":\"Bob\",\"description\":\"Thanks to Bob\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(urlBase + urlSendThanks))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess("1000", MediaType.APPLICATION_JSON));
        //when
        String result = gamificationRepository.saveThanksAchievement(new ThanksAchievement("Bill", "Bob", "Thanks to Bob"));

        // then
        mockServer.verify();
        assertThat(result, equalTo("1000"));
    }

    @Test
    public void shouldThrowExceptionWhenSendThanksToRemoteGamificationServiceThrowException() {
        // given
        String expectedRequestBody = "{\"from\":\"Bill\",\"to\":\"Bob\",\"description\":\"Thanks to Bob\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(urlBase + urlSendThanks))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("bad request"));
        //then
        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Gamification Exchange Error"));
        //when
        gamificationRepository.saveThanksAchievement(new ThanksAchievement("Bill", "Bob", "Thanks to Bob"));
    }

    @Test
    public void shouldReturnIdAchievementWhenSendInterviewToRemoteGamificationService() {
        //given
        String expectedRequestBody = "{\"from\":\"101\",\"description\":\"description\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(urlBase + urlSendInterview))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(),
                        containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess("1000", MediaType.APPLICATION_JSON));
        //when
        String result = gamificationRepository.saveInterviewAchievement(new InterviewAchievement("101", "description"));

        // then
        mockServer.verify();
        assertThat(result, equalTo("1000"));
    }

    @Test
    public void shouldThrowExceptionWhenSendInterviewToRemoteInterviewServiceThrowException() {
        // given
        String expectedRequestBody = "{\"from\":\"101\",\"description\":\"description\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo(urlBase + urlSendInterview))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(),
                        containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("bad request"));
        //then
        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Gamification Exchange Error"));
        //when
        gamificationRepository.saveInterviewAchievement(new InterviewAchievement("101", "description"));
    }
}

