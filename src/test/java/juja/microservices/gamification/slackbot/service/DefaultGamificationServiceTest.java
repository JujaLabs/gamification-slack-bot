package juja.microservices.gamification.slackbot.service;

import juja.microservices.gamification.slackbot.dao.GamificationRepository;
import juja.microservices.gamification.slackbot.model.CodenjoyAchievment;
import juja.microservices.gamification.slackbot.model.DailyAchievement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author Danil Kuznetsov
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultGamificationServiceTest {

    @MockBean
    private GamificationRepository gamificationRepository;

    @Inject
    private GamificationService gamificationService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSaveNewDailyAndReturnNewAchievementId() {

        //given
        String expectedAchievementId = "100";
        DailyAchievement dailyAchievement = new DailyAchievement("test", "description");
        given(gamificationRepository.saveDailyAchievement(dailyAchievement)).willReturn(expectedAchievementId);

        //when
        String result = gamificationService.sendDailyAchievement(dailyAchievement);

        //then
        assertThat(result, equalTo(expectedAchievementId));
        verify(gamificationRepository).saveDailyAchievement(dailyAchievement);
    }

    @Test
    public void shouldSaveNewCodenjoyAndReturnNewAchievementId() {

        //given
        String expectedAchievementId = "100";
        CodenjoyAchievment codenjoyAchievment = new CodenjoyAchievment("Bill", "Walter", "Bob", "Jonh");
        given(gamificationRepository.saveCodenjoyAchievement(codenjoyAchievment)).willReturn(expectedAchievementId);

        //when
        String result = gamificationService.sendCodenjoyAchievement(codenjoyAchievment);

        //then
        assertThat(result, equalTo(expectedAchievementId));
        verify(gamificationRepository).saveCodenjoyAchievement(codenjoyAchievment);
    }
}
