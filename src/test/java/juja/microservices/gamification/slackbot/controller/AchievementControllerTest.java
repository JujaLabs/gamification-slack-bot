package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.entities.CodenjoyRequest;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by Nikol on 3/4/2017.
 */
public class AchievementControllerTest {
    private static final String EXPECTED_REQUEST = "{\"from\":\"Bill\",\"firstPlace\":\"Walter\",\"secondPlace\":\"Bob\",\"thirdPlace\":\"Jonh\"}";
    private static final String RESPONSE = "[\"Walter\",\"Bob\",\"Jonh\"]";


    @Test
    public void sendCodenjoy() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(manyTimes(), requestTo("/achieve/codenjoy"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(createRequestMatcher())
                .andRespond(withSuccess(RESPONSE, MediaType.TEXT_HTML));
        CodenjoyRequest codenjoyRequest = new CodenjoyRequest("Bill", "Walter", "Bob", "Jonh");

        String actualResponse = restTemplate.postForObject("/achieve/codenjoy", codenjoyRequest, String.class);

        assertEquals(RESPONSE, actualResponse);
        server.verify();
    }

    private RequestMatcher createRequestMatcher() {
        return request -> assertEquals(EXPECTED_REQUEST, request.getBody().toString());
    }

}