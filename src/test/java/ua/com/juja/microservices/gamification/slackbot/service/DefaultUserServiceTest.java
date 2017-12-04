package ua.com.juja.microservices.gamification.slackbot.service;

import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import ua.com.juja.microservices.gamification.slackbot.dao.UserRepository;
import ua.com.juja.slack.command.handler.model.UserDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

    @Captor
    private ArgumentCaptor<Set<String>> argumentCaptor;

    private UserDTO user1;
    private UserDTO user2;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        user1 = new UserDTO("uuid1", "userSlackId1");
        user2 = new UserDTO("uuid2", "userSlackId2");
    }

    @Test
    public void returnUsersListByUuids() throws Exception {
        //given
        Set<String> uuidsRequest = new LinkedHashSet<>(Arrays.asList("uuid1", "uuid2"));
        Set<UserDTO> usersResponse = new LinkedHashSet<>(Arrays.asList(user1, user2));
        given(userRepository.findUsersByUuids(any())).willReturn(usersResponse);
        //when
        Set<UserDTO> result = userService.receiveUsersByUuids(uuidsRequest);
        //then
        verify(userRepository, times(1)).findUsersByUuids(argumentCaptor.capture());
        Set<String> actualUuidsRequest = argumentCaptor.getValue();
        assertEquals(actualUuidsRequest, uuidsRequest);
        assertEquals(usersResponse, result);
    }
}