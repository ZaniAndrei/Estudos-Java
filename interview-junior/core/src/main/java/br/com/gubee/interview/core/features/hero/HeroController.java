package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.HeroComparison;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/heroes", produces = APPLICATION_JSON_VALUE)
public class HeroController {

    private final HeroService heroService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@Validated
                                       @RequestBody CreateHeroRequest createHeroRequest) {
        final UUID id = heroService.create(createHeroRequest);
        return created(URI.create(format("/api/v1/heroes/%s", id))).build();
    }

    /*@GetMapping
    public ResponseEntity<List<Hero>> findAll() {
        List<Hero> list= heroService.findAll();
        return ResponseEntity.ok().body(list);
    }*/

    @GetMapping(value = "/{id}")
    public ResponseEntity<Hero> findById(@PathVariable UUID id) {
        Hero hero = heroService.findById(id);
        return ResponseEntity.ok().body(hero);
    }
    @GetMapping(value = "/name/{name}")
    public ResponseEntity<Hero> findByName(@PathVariable String name) {
        Hero hero = heroService.findHeroByName(name);
        return ResponseEntity.ok().body(hero);
    }

    @GetMapping(value = "compare/{id1}/{id2}")
    public ResponseEntity<HeroComparison> heroComparison(@PathVariable UUID id1, @PathVariable UUID id2) {
        HeroComparison heroCompare = heroService.heroComparison(id1, id2);
        return ResponseEntity.ok().body(heroCompare);
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<Void>  update(@PathVariable UUID id, @Validated @RequestBody UpdateHeroRequest heroUpdateRequest) {
        heroService.update(id, heroUpdateRequest);
        return created(URI.create(format("/api/v1/heroes/%s", id))).build();

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        heroService.delete(id);
        return created(URI.create(format("/api/v1/heroes/%s", id))).build();

    }
}
