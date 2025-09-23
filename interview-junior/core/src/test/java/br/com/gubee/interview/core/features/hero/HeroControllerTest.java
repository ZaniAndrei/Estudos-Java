package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.hero.exception.NameNotFoundException;
import br.com.gubee.interview.core.features.hero.exception.ResourceNotFoundException;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.HeroComparison;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HeroController.class)
class HeroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HeroService heroService;

    @BeforeEach
    public void initTest() {
        when(heroService.create(any())).thenReturn(UUID.randomUUID());
    }

    @Test
    void createAHeroWithAllRequiredArguments() throws Exception {
        //given
        // Convert the hero request into a string JSON format stub.
        final String body = objectMapper.writeValueAsString(createHeroRequest());

        //when
        final ResultActions resultActions = mockMvc.perform(post("/api/v1/heroes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        //then
        resultActions.andExpect(status().isCreated()).andExpect(header().exists("Location"));
        verify(heroService, times(1)).create(any());
    }

    @Test
    void shouldReturnHeroIfIdExists() throws Exception {

        UUID id = UUID.randomUUID();
        Hero hero = Hero.builder()
                .id(id)
                .name("Aquaman")
                .race(Race.HUMAN)
                .build();
        when(heroService.findById(id)).thenReturn(hero);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/{id}", id));
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Aquaman")));
        verify(heroService, times(1)).findById(id);
    }

    @Test
    void shouldReturnHeroIfNameExists() throws Exception {
        UUID id = UUID.randomUUID();
        Hero hero = Hero.builder()
                .id(id)
                .name("Aquaman")
                .race(Race.HUMAN)
                .build();
        when(heroService.findHeroByName("Aquaman")).thenReturn(hero);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/name/{name}", hero.getName()));
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Aquaman")));
        verify(heroService, times(1)).findHeroByName("Aquaman");
    }

    @Test
    void shouldReturnNotFoundIfIdDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        ResourceNotFoundException e = new ResourceNotFoundException(id);
        when(heroService.findById(id)).thenThrow(e);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/{id}", id));
        resultActions.andExpect(status().isNotFound());
        verify(heroService, times(1)).findById(id);
    }

    @Test
    void shouldReturnOkIfNameDoesNotExist() throws Exception{
        UUID id = UUID.randomUUID();
        Hero hero = Hero.builder()
                    .name("Batman")
                    .build();
        when(heroService.findHeroByName(hero.getName())).thenThrow(new NameNotFoundException(hero.getName()));

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/name/{name}", hero.getName()));
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void shouldReturnHeroComparisonIfIdExists() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        HeroComparison heroComparison = HeroComparison.builder()
                .id1(id1)
                .id2(id2)
                .build();
        when(heroService.heroComparison(id1, id2)).thenReturn(heroComparison);

        final ResultActions resultActions = mockMvc.perform(
                get("/api/v1/heroes/compare/{id1}/{id2}", id1, id2));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id1", is(id1.toString())))
                .andExpect(jsonPath("$.id2", is(id2.toString())));

        verify(heroService, times(1)).heroComparison(id1, id2);
    }

    @Test
    void shouldReturnNotFoundIfIdDoesNotExistCompare() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        ResourceNotFoundException e = new ResourceNotFoundException(id1);
        when(heroService.heroComparison(id1, id2)).thenThrow(e);

        final ResultActions resultActions = mockMvc.perform(
                get("/api/v1/heroes/compare/{id1}/{id2}", id1, id2));

        resultActions.andExpect(status().isNotFound());
        verify(heroService, times(1)).heroComparison(id1, id2);
    }

    @Test
    public void shouldUpdateAndReturnCreated() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(heroService).update(id, updateHeroRequest());

        final ResultActions resultActions = mockMvc.perform(
                put("/api/v1/heroes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateHeroRequest()))
        );

        resultActions.andExpect(status().isCreated());
        verify(heroService, times(1)).update(id, updateHeroRequest());
    }

    @Test
    public void shouldReturnNotFoundIdUpdate() throws Exception{
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException(id)).when(heroService).update(id, updateHeroRequest());

        final ResultActions resultActions = mockMvc.perform(
                put("/api/v1/heroes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateHeroRequest()))
        );

        resultActions.andExpect(status().isNotFound());
        verify(heroService, times(1)).update(id, updateHeroRequest());
    }

    @Test
    public void shouldReturnCreatedDelete() throws Exception{
        UUID id = UUID.randomUUID();
        doNothing().when(heroService).delete(id);
        final ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/heroes/{id}", id)
        );
        resultActions.andExpect(status().isCreated());
        verify(heroService, times(1)).delete(id);
    }

    @Test
    public void shouldReturnNotFoundDelete() throws Exception{
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException(id)).when(heroService).delete(id);
        final ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/heroes/{id}", id)
        );
        resultActions.andExpect(status().isNotFound());
        verify(heroService, times(1)).delete(id);
    }

    @Test
    public void shouldNotAllowOutOfRangeCreationMax() throws Exception {
        CreateHeroRequest invalidRequestMax = CreateHeroRequest.builder()
                .name("Invalid")
                .race(Race.HUMAN)
                .agility(11)
                .dexterity(11)
                .intelligence(11)
                .strength(11)
                .build();
        final ResultActions resultActions = mockMvc.perform(
                post("/api/v1/heroes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestMax))
        );

        resultActions.andExpect(status().isBadRequest()) // Garante o status 400
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$", containsInAnyOrder(
                        "message.powerstats.strength.max",
                        "message.powerstats.agility.max",
                        "message.powerstats.dexterity.max",
                        "message.powerstats.intelligence.max"
                )));

    }

    @Test
    public void shouldNotAllowOutOfRangeCreationMin() throws Exception {

        CreateHeroRequest invalidRequestMin = CreateHeroRequest.builder()

                .name("Invalid")
                .race(Race.HUMAN)
                .agility(-1)
                .dexterity(-1)
                .intelligence(-1)
                .strength(-1)
                .build();

        final ResultActions resultActions = mockMvc.perform(
                post("/api/v1/heroes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestMin))
        );

        resultActions.andExpect(status().isBadRequest()) // Garante o status 400
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$", containsInAnyOrder(
                        "message.powerstats.strength.min",
                        "message.powerstats.agility.min",
                        "message.powerstats.dexterity.min",
                        "message.powerstats.intelligence.min"
                )));


    }

    @Test
    public void shouldNotAllowOutOfRangeUpdateMax() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateHeroRequest invalidRequestMax = UpdateHeroRequest.builder()
                .name("Invalid")
                .race(Race.HUMAN)
                .agility(11)
                .dexterity(11)
                .intelligence(11)
                .strength(11)
                .build();
        final ResultActions resultActions = mockMvc.perform(
                put("/api/v1/heroes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestMax))
        );

        resultActions.andExpect(status().isBadRequest()) // Garante o status 400
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$", containsInAnyOrder(
                        "message.powerstats.strength.max",
                        "message.powerstats.agility.max",
                        "message.powerstats.dexterity.max",
                        "message.powerstats.intelligence.max"
                )));

    }

    @Test
    public void shouldNotAllowOutOfRangeUpdateMin() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateHeroRequest invalidRequestMin = UpdateHeroRequest.builder()

                .name("Invalid")
                .race(Race.HUMAN)
                .agility(-1)
                .dexterity(-1)
                .intelligence(-1)
                .strength(-1)
                .build();

        final ResultActions resultActions = mockMvc.perform(
                put("/api/v1/heroes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestMin))
        );

        resultActions.andExpect(status().isBadRequest()) // Garante o status 400
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$", containsInAnyOrder(
                        "message.powerstats.strength.min",
                        "message.powerstats.agility.min",
                        "message.powerstats.dexterity.min",
                        "message.powerstats.intelligence.min"
                )));


    }





    @Test
    public void shouldNotAllowMandatoryFieldsMissing() throws Exception {
        CreateHeroRequest invalidRequestMin = CreateHeroRequest.builder()
                .build();
        final ResultActions resultActions = mockMvc.perform(
                post("/api/v1/heroes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestMin))
        );

        resultActions.andExpect(status().isBadRequest()) // Garante o status 400
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$", containsInAnyOrder(
                        "message.powerstats.intelligence.mandatory",
                        "message.powerstats.dexterity.mandatory",
                        "message.name.mandatory",
                        "message.powerstats.agility.mandatory",
                        "message.race.mandatory",
                        "message.powerstats.strength.mandatory"
                )));

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

    private UpdateHeroRequest updateHeroRequest() {
        return UpdateHeroRequest.builder()
                .name("Flash")
                .build();
    }
}