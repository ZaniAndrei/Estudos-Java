package br.com.gubee.interview.core.features.hero;


import br.com.gubee.interview.core.features.hero.exception.NameNotFoundException;
import br.com.gubee.interview.core.features.hero.exception.ResourceNotFoundException;
import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.HeroComparison;
import br.com.gubee.interview.model.PowerStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.when;

@WebMvcTest(HeroService.class)
public class HeroServiceUN {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HeroService heroService;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private HeroRepository heroRepository;
    @MockBean
    private PowerStatsService powerStatsService;
    @MockBean
    private HeroComparison heroComparison;

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenHeroIdNotFound()
            throws Exception {
        UUID id = UUID.randomUUID();
        when(heroRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> heroService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    public void shouldThrowNameNotFoundExceptionWhenHeroNameNotFound()
            throws Exception {
        String name = "NomeTeste";
        when(heroRepository.findHeroByName(name)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> heroService.findHeroByName(name))
                .isInstanceOf(NameNotFoundException.class);

    }

    @Test
    public void shouldCompareHeroes() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        PowerStats ps1 = PowerStats.builder()
                .agility(10)
                .strength(2)
                .intelligence(7)
                .dexterity(6)
                .build();
        PowerStats ps2 = PowerStats.builder()
                .agility(10)
                .strength(9)
                .intelligence(5)
                .dexterity(10)
                .build();

        Hero hero1 = Hero.builder()
                .id(id1)
                .name("Hero1")
                .powerStats(ps1)
                .build();
        Hero hero2 = Hero.builder()
                .id(id2)
                .name("Hero2")
                .powerStats(ps2)
                .build();


        HeroComparison heroComparison = heroService.heroComparison(id1, id2);

        assertEquals(id1, heroComparison.getId1());
        assertEquals(id2, heroComparison.getId2());
        assertEquals( 0, heroComparison.getAgility());
        assertEquals(-7,heroComparison.getStrength());
        assertEquals(2, heroComparison.getIntelligence());
        assertEquals(-4, heroComparison.getDexterity());
    }

}
