package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.UserRepository;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

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
    public void returnUsersListBySlacks() throws Exception {
        //given
        List<String> slackNamesRequest = Arrays.asList(new String[]{"@slack1", "@slack2"});
        List<UserDTO> usersResponse = Arrays.asList(new UserDTO[]{new UserDTO("uuid1", "@slack1"),
                new UserDTO("uuid2", "slack2")});
        given(userRepository.findUsersBySlackNames(slackNamesRequest)).willReturn(usersResponse);
        //when
        List<UserDTO> result = userService.findUsersBySlackNames(slackNamesRequest);
        //then
        assertEquals("[UserDTO(uuid=uuid1, slack=@slack1), UserDTO(uuid=uuid2, slack=slack2)]", result.toString());
    }
}