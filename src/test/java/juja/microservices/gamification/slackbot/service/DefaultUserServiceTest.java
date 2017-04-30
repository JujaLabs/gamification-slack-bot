package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.model.ThanksAchievement;
import juja.microservices.gamification.slackbot.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Created by Nikol on 4/30/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultUserServiceTest {

    @Inject
    UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void returnUserBySlackName() {
        //given
        String slackName = "slack.name";
        User user = new User("uuid", "mail@gmail.com", "slack.name", "skype", "linkedin", "facebook", "twitter");
        given(userRepository.findUserBySlack(slackName)).willReturn(user);

        //when
        User result = userService.findUserBySlack(slackName);

        //then
        assertThat(result, equalTo(user));
        verify(userRepository).findUserBySlack(slackName);
    }

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