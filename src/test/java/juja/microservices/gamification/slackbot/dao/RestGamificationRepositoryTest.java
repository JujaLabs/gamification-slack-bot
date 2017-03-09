package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.GamificationExchangeException;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
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
        mockServer.expect(requestTo("/achieve/daily"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess("1000", MediaType.APPLICATION_JSON));
        //when
        String result = gamificationRepository.saveDailyAchievement(new DailyAchievement("101", "description"));

        // then
        mockServer.verify();
        assertThat(result, equalTo("1000"));
    }

    @Test
    public void shouldThrowExceptionWhenSendDailyToRemoteGamificationServiceThrowException() {
        // given
        String expectedRequestBody = "{\"from\":\"101\",\"description\":\"description\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo("/achieve/daily"))
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
        String expectedRequestBody = "{\"from\":\"Bill\",\"firstPlace\":\"Walter\",\"secondPlace\":\"Bob\",\"thirdPlace\":\"Jonh\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo("/achieve/codenjoy"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess("1000", MediaType.APPLICATION_JSON));
        //when
        String result = gamificationRepository.saveCodenjoyAchievement(new CodenjoyAchievment("Bill", "Walter", "Bob", "Jonh"));

        // then
        mockServer.verify();
        assertThat(result, equalTo("1000"));
    }

    @Test
    public void shouldThrowExceptionWhenSendCodenjoyToRemoteGamificationServiceThrowException() {
        // given
        String expectedRequestBody = "{\"from\":\"Bill\",\"firstPlace\":\"Walter\",\"secondPlace\":\"Bob\",\"thirdPlace\":\"Jonh\"}";
        String expectedRequestHeader = "application/json";
        mockServer.expect(requestTo("/achieve/codenjoy"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString(expectedRequestHeader)))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("bad request"));
        //then
        thrown.expect(GamificationExchangeException.class);
        thrown.expectMessage(containsString("Gamification Exchange Error"));
        //when
        gamificationRepository.saveCodenjoyAchievement(new CodenjoyAchievment("Bill", "Walter", "Bob", "Jonh"));
    }

}

