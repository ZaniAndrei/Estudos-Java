package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.hero.exception.NameNotFoundException;
import br.com.gubee.interview.core.features.hero.exception.ResourceNotFoundException;
import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.HeroComparison;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HeroService {

    private final HeroRepository heroRepository;
    private final PowerStatsService powerStatsService;

    @Transactional
    public UUID create(CreateHeroRequest createHeroRequest) {
        //return heroRepository.create(new Hero(createHeroRequest, null));
        UUID powerStatsId = powerStatsService.create(new PowerStats(createHeroRequest));
        return heroRepository.create(new Hero(createHeroRequest, powerStatsId));
    }


    public Hero findById(UUID id) {
        Optional<Hero> obj = heroRepository.findById(id);
        return obj.orElseThrow(()->new ResourceNotFoundException(id));

    }

    public Hero findHeroByName(String name) {
        Optional<Hero> obj = heroRepository.findHeroByName(name);
        return obj.orElseThrow(()->new NameNotFoundException(name));
    }

    public void update(UUID id, UpdateHeroRequest updateHeroRequest){

        Hero hero = findById(id);
        powerStatsService.update(hero.getPowerStats().getId(), new PowerStats(updateHeroRequest));
        heroRepository.update(id, new Hero(updateHeroRequest));

        }

    @Transactional
    public void delete(UUID id) {
        Hero hero = findById(id);
        heroRepository.delete(id);
        powerStatsService.delete(hero.getPowerStats().getId());
    }

    public HeroComparison heroComparison(UUID id1, UUID id2){
        Hero hero1 = findById(id1);
        Hero hero2 = findById(id2);

        var strDiff =  hero1.getPowerStats().getStrength() - hero2.getPowerStats().getStrength();
        var dexDiff = hero1.getPowerStats().getDexterity() - hero2.getPowerStats().getDexterity();
        var agiDiff =  hero1.getPowerStats().getAgility() - hero2.getPowerStats().getAgility();
        var intDiff = hero1.getPowerStats().getIntelligence() - hero2.getPowerStats().getIntelligence();

        return new HeroComparison(hero1.getId(), hero2.getId(), strDiff, agiDiff, dexDiff, intDiff);

    }
}
