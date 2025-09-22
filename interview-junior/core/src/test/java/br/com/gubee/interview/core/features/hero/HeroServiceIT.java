package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.hero.exception.ResourceNotFoundException;
import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.HeroComparison;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@Transactional
public class HeroServiceIT {

    @Autowired
    private HeroService heroService;

    @Autowired
    private PowerStatsService powerStatsService;


    @Test
    public void createHeroWithAllRequiredArguments() {

        UUID id = heroService.create(createHeroRequest());
        Hero hero = heroService.findById(id);

        assertThat(hero.getId()).isEqualTo(id);
        assertThat(hero.getName()).isEqualTo("Batman");
        assertThat(hero.getPowerStats().getAgility()).isEqualTo(5);
        assertThat(hero.getPowerStats().getStrength()).isEqualTo(6);
        assertThat(hero.getPowerStats().getDexterity()).isEqualTo(8);
        assertThat(hero.getPowerStats().getIntelligence()).isEqualTo(10);
        assertThat(hero.getRace()).isEqualTo(Race.HUMAN);

    }

    @Test
    public void shouldUpdateHero(){
        UUID id = heroService.create(createHeroRequest());

        heroService.update(id, updateHeroRequest());

        Hero hero_updated =  heroService.findById(id);
        assertThat(hero_updated.getId()).isEqualTo(id);
        assertThat(hero_updated.getName()).isEqualTo("Martian Manhunter");
        assertThat(hero_updated.getPowerStats().getAgility()).isEqualTo(3);
        assertThat(hero_updated.getPowerStats().getStrength()).isEqualTo(5);
        assertThat(hero_updated.getPowerStats().getDexterity()).isEqualTo(4);
        assertThat(hero_updated.getPowerStats().getIntelligence()).isEqualTo(9);
        assertThat(hero_updated.getRace()).isEqualTo(Race.ALIEN);
    }

    @Test
    public void shouldDeleteHero(){
        UUID id = heroService.create(createHeroRequest());
        heroService.delete(id);
        String expectedMessage = "Resource not found Id: " + id;
        ResourceNotFoundException e = assertThrows(
                ResourceNotFoundException.class,
                () -> heroService.findById(id)
        );
        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    public void shouldCompareHeroes(){
        UUID id1 =  heroService.create(createHeroRequest());
        UUID id2 =  heroService.create(createHeroRequest2());

        Hero hero1 = heroService.findById(id1);
        Hero hero2 = heroService.findById(id2);

        HeroComparison heroComparison = heroService.heroComparison(id1, id2);

        assertEquals(heroComparison.getAgility(), hero1.getPowerStats().getAgility() - hero2.getPowerStats().getAgility());
        assertEquals(heroComparison.getStrength(), hero1.getPowerStats().getStrength() - hero2.getPowerStats().getStrength());
        assertEquals(heroComparison.getDexterity(), hero1.getPowerStats().getDexterity() - hero2.getPowerStats().getDexterity());
        assertEquals(heroComparison.getIntelligence(), hero1.getPowerStats().getIntelligence() - hero2.getPowerStats().getIntelligence());
    }

    private CreateHeroRequest createHeroRequest() {
        return CreateHeroRequest.builder()
            .name("Batman")
            .agility(5)
            .dexterity(8)
            .strength(6)
            .intelligence(10)
            .race(Race.HUMAN)
            .build();
    }

    private CreateHeroRequest createHeroRequest2(){
        return CreateHeroRequest.builder()
                .name("Mulher Maravilha")
                .agility(6)
                .dexterity(9)
                .strength(10)
                .intelligence(7)
                .race(Race.HUMAN)
                .build();
    }

    private UpdateHeroRequest updateHeroRequest() {
        return UpdateHeroRequest.builder()
            .name("Martian Manhunter")
            .agility(3)
            .dexterity(4)
            .strength(5)
            .intelligence(9)
            .race(Race.ALIEN)
            .build();
    }
}
