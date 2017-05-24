package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author Nikolay Horushko
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultUserServiceTest {

    @Inject
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void returnUuidUserBySlack() throws Exception {
        //given
        String slackName = "slack.name";
        String uuid = "uuid";
        given(userRepository.findUuidUserBySlack(slackName)).willReturn(uuid);

        //when
        String result = userService.findUuidUserBySlack(slackName);

        //then
        assertThat(result, equalTo(uuid));
        verify(userRepository).findUuidUserBySlack(slackName);
    }
}