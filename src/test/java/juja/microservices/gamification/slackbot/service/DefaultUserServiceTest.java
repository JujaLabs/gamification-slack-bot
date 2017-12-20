package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultUserServiceTest {

    @Inject
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserDTO user1;
    private UserDTO user2;

    private static final String SLACK_USER1 = "slack1";
    private static final String SLACK_USER2 = "slack2";

    @Before
    public void setup() {
        user1 = new UserDTO("uuid1", SLACK_USER1);
        user2 = new UserDTO("uuid2", SLACK_USER2);
    }

    @Test
    public void findUsersBySlackUsers() throws Exception {
        //given
        List<String> slackUsersRequest = Arrays.asList(SLACK_USER1, SLACK_USER2);
        List<UserDTO> usersResponse = Arrays.asList(user1, user2);

        given(userRepository.findUsersBySlackUsers(slackUsersRequest)).willReturn(usersResponse);

        //when
        List<UserDTO> result = userService.findUsersBySlackUsers(slackUsersRequest);

        //then
        assertThat(result,hasSize(2));
        assertThat(result,hasItem(user1));
        assertThat(result,hasItem(user2));

        verify(userRepository).findUsersBySlackUsers(slackUsersRequest);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void findUsersByUuids() throws Exception {
        //given
        Set<String> uuids = new HashSet<>(Arrays.asList(user1.getUuid(),  user2.getUuid()));
        Set<UserDTO> foundUsers = new HashSet<>(Arrays.asList(user1, user2));

        given(userRepository.findUsersByUuids(uuids)).willReturn(foundUsers);

        //when
        Set<UserDTO> result = userService.findUsersByUuids(uuids);

        //then
        assertThat(result,hasSize(2));
        assertThat(result,hasItem(user1));
        assertThat(result,hasItem(user2));

        verify(userRepository).findUsersByUuids(uuids);
        verifyNoMoreInteractions(userRepository);
    }

}