package ua.com.juja.microservices.gamification.slackbot.dao;

import feign.FeignException;
import ua.com.juja.microservices.gamification.slackbot.dao.feign.UsersClient;
import ua.com.juja.microservices.gamification.slackbot.exceptions.UserExchangeException;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.SlackNameRequest;
import ua.com.juja.slack.command.handler.model.UserDTO;
import ua.com.juja.microservices.gamification.slackbot.model.DTO.UuidRequest;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Ivan Shapovalov
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestUserRepositoryTest {
    private static UserDTO user1;
    private static UserDTO user2;
    private static UserDTO user3;
    private static UserDTO user4;
    @Rule
    final public ExpectedException expectedException = ExpectedException.none();
    @Inject
    private UserRepository userRepository;
    @MockBean
    private UsersClient usersClient;

    @BeforeClass
    public static void oneTimeSetup() {
        user1 = new UserDTO("uuid1", "@slack1");
        user2 = new UserDTO("uuid2", "@slack2");
        user3 = new UserDTO("uuid3", "@slack3");
        user4 = new UserDTO("uuid4", "@slack4");
    }

    @Test
    public void findUsersBySlackNamesIfUserServerReturnsUsersCorrectly() throws IOException {
        List<String> slackNames = Arrays.asList("@slack1", "@slack2", "@slack3", "@slack4");
        List<UserDTO> expected = Arrays.asList(user1, user2, user3, user4);
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenReturn(expected);

        List<UserDTO> actual = userRepository.findUsersBySlackNames(slackNames);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(actual)
                    .as("expected not equals actual")
                    .isEqualTo(expected);
            soft.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .as("'captorSlackNameRequest' slacknames not contains 'slackNames'")
                    .containsExactlyInAnyOrder(slackNames.toArray(new String[slackNames.size()]));
        });
        verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
        verifyNoMoreInteractions(usersClient);
    }

    @Test
    public void findUsersBySlackNamesIfUserServerReturnsFeignExceptionWithCorrectContent() throws IOException {
        List<String> slackNames = Arrays.asList("@slack1", "@slack2", "@slack3", "@slack4");
        String expectedJsonResponseBody =
                "status 400 reading UsersClient#findUsersBySlackNames(); content:" +
                        "{\n" +
                        "  \"httpStatus\": 400,\n" +
                        "  \"internalErrorCode\": \"TMF-F1-D3\",\n" +
                        "  \"clientMessage\": \"Sorry, User server return an error\",\n" +
                        "  \"developerMessage\": \"Exception - UserExchangeException\",\n" +
                        "  \"exceptionMessage\": \"Something wrong on User server\",\n" +
                        "  \"detailErrors\": []\n" +
                        "}";
        FeignException feignException = mock(FeignException.class);
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        expectedException.expect(UserExchangeException.class);
        expectedException.expectMessage(containsString("Sorry, User server return an error"));

        try {
            userRepository.findUsersBySlackNames(slackNames);
        } finally {
            Assertions.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .containsExactlyInAnyOrder((slackNames.toArray(new String[slackNames.size()])));
            verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
            verifyNoMoreInteractions(usersClient);
        }
    }

    @Test
    public void findUsersBySlackNamesIfUserServerReturnsFeignExceptionWithIncorrectContent() throws IOException {
        List<String> slackNames = Arrays.asList("@slack1", "@slack2", "@slack3", "@slack4");
        String expectedJsonResponseBody =
                "status 400 reading UsersClient#findUsersBySlackNames(); content: \n";
        FeignException feignException = mock(FeignException.class);
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        expectedException.expect(UserExchangeException.class);
        expectedException.expectMessage(
                containsString("I'm, sorry. I cannot parse api error message from remote service :("));

        try {
            userRepository.findUsersBySlackNames(slackNames);
        } finally {
            Assertions.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .containsExactlyInAnyOrder((slackNames.toArray(new String[slackNames.size()])));
            verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
            verifyNoMoreInteractions(usersClient);
        }
    }

    @Test
    public void findUsersBySlackNamesIfUserServerReturnsFeignExceptionWithoutContent() throws IOException {
        List<String> slackNames = Arrays.asList("@slack1", "@slack2", "@slack3", "@slack4");
        String expectedJsonResponseBody = "";
        FeignException feignException = mock(FeignException.class);
        ArgumentCaptor<SlackNameRequest> captorSlackNameRequest = ArgumentCaptor.forClass(SlackNameRequest.class);
        when(usersClient.findUsersBySlackNames(captorSlackNameRequest.capture())).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        expectedException.expect(UserExchangeException.class);
        expectedException.expectMessage(
                containsString("I'm, sorry. I cannot parse api error message from remote service :("));

        try {
            userRepository.findUsersBySlackNames(slackNames);
        } finally {
            Assertions.assertThat(captorSlackNameRequest.getValue().getSlackNames())
                    .containsExactlyInAnyOrder((slackNames.toArray(new String[slackNames.size()])));
            verify(usersClient).findUsersBySlackNames(captorSlackNameRequest.capture());
            verifyNoMoreInteractions(usersClient);
        }
    }

    @Test
    public void findUsersByUuidsIfUserServerReturnsUserCorrectly() throws IOException {
        Set<String> uuids = new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4"));
        Set<UserDTO> expected = new HashSet<>(Arrays.asList(user1, user2, user3, user4));
        ArgumentCaptor<UuidRequest> captorUuidRequest = ArgumentCaptor.forClass(UuidRequest.class);
        when(usersClient.findUsersByUuids(captorUuidRequest.capture())).thenReturn(expected);

        Set<UserDTO> actual = userRepository.findUsersByUuids(uuids);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(actual)
                    .as("expected not equals actual")
                    .isEqualTo(expected);
            soft.assertThat(captorUuidRequest.getValue().getUuids())
                    .as("'captorUuidRequest' uuids not contains 'uuids'")
                    .containsExactlyInAnyOrder(uuids.toArray(new String[uuids.size()]));
        });
        verify(usersClient).findUsersByUuids(captorUuidRequest.capture());
        verifyNoMoreInteractions(usersClient);
    }

    @Test
    public void findUsersByUuidsIfUserServerReturnsException() throws IOException {
        Set<String> uuids = new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4"));
        String expectedJsonResponseBody =
                "status 400 reading UsersClient#findUsersByUuids(); content:" +
                        "{\n" +
                        "  \"httpStatus\": 400,\n" +
                        "  \"internalErrorCode\": \"TMF-F1-D3\",\n" +
                        "  \"clientMessage\": \"Sorry, User server return an error\",\n" +
                        "  \"developerMessage\": \"Exception - UserExchangeException\",\n" +
                        "  \"exceptionMessage\": \"Something wrong on User server\",\n" +
                        "  \"detailErrors\": []\n" +
                        "}";
        FeignException feignException = mock(FeignException.class);
        ArgumentCaptor<UuidRequest> captorUuidRequest = ArgumentCaptor.forClass(UuidRequest.class);
        when(usersClient.findUsersByUuids(captorUuidRequest.capture())).thenThrow(feignException);
        when(feignException.getMessage()).thenReturn(expectedJsonResponseBody);

        expectedException.expect(UserExchangeException.class);
        expectedException.expectMessage(containsString("Sorry, User server return an error"));

        try {
            userRepository.findUsersByUuids(uuids);
        } finally {
            Assertions.assertThat(captorUuidRequest.getValue().getUuids())
                    .containsExactlyInAnyOrder(uuids.toArray(new String[uuids.size()]));
            verify(usersClient).findUsersByUuids(captorUuidRequest.capture());
            verifyNoMoreInteractions(usersClient);
        }
    }
}