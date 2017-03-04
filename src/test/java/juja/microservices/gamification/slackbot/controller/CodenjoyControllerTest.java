package juja.microservices.gamification.slackbot.controller;

import org.junit.Before;
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
public class CodenjoyControllerTest {
    private static final String EXPECTED_REQUEST = "{\"from\":\"Bill\",\"firstPlace\":\"Walter\",\"secondPlace\":\"Bob\",\"thirdPlace\":\"Jonh\"}";
    private static final String EXPECTED_RESPONSE = "[\"Walter\",\"Bob\",\"Jonh\"]";
    private static final String CODENJOY_URL = "/achieve/codenjoy";
    private CodenjoyController codenjoyController;
    private RestTemplate restTemplate;

    @Before
    public void setup(){
        restTemplate = new RestTemplate();
        codenjoyController = new CodenjoyController(restTemplate);
    }

    @Test
    public void sendCodenjoy() throws Exception {
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(manyTimes(), requestTo(CODENJOY_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(createRequestMatcher())
                .andRespond(withSuccess(EXPECTED_RESPONSE, MediaType.TEXT_HTML));

        String actualResponse = codenjoyController.sendCodenjoy("Bill", "Walter", "Bob", "Jonh");

        assertEquals(EXPECTED_RESPONSE, actualResponse);
        server.verify();
    }

    private RequestMatcher createRequestMatcher() {
        return request -> assertEquals(EXPECTED_REQUEST, request.getBody().toString());
    }
}