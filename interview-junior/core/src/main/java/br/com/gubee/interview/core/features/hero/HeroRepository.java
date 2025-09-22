package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.enums.Race;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Repository
@RequiredArgsConstructor

public class HeroRepository{

    private static final String CREATE_HERO_QUERY = "INSERT INTO hero" +
        " (name, race, power_stats_id)" +
        " VALUES (:name, :race, :powerStatsId) RETURNING id";

    /*private static final String FIND_HERO_QUERY = "SELECT * FROM hero " +
            "WHERE id = :id";*/

    private static final String FIND_HERO_QUERY = "SELECT h.*, p.*"+
            " FROM hero h JOIN power_stats p " +
            "ON h.power_stats_id = p.id" +
            " WHERE h.id = :id";

    private static final String FIND_HERO_NAME_QUERY = "SELECT h.*, p.*"+
            " FROM hero h JOIN power_stats p " +
            "ON h.power_stats_id = p.id" +
            " WHERE h.name = :name";

    private static final String UPDATE_HERO_QUERY = "UPDATE hero SET name = :name, race = :race, updated_at = :now " +
            "WHERE id = :id";

    private static final String DELETE_HERO_QUERY = "DELETE FROM hero WHERE id = :id";


    private static final String UPDATE_POWER_STATS_QUERY = "UPDATE power_stats SET strength = :strength " +
            "agility = :agility, dexterity = :dexterity, intelligence = :intelligence, updated_at = :now";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /*private final RowMapper<Hero> heroRowMapper = (rs, rowNum) -> new Hero(
            rs.getObject("id", UUID.class),
            rs.getString("name"),
            Race.valueOf(rs.getString("race")),
            rs.getObject("power_stats_id", UUID.class),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant(),
            rs.getBoolean("enabled")
    );*/

    private final RowMapper<Hero> heroRowMapper = (rs, rouwNum) ->{
        PowerStats powerStats = PowerStats.builder()
                .id(rs.getObject("power_stats_id", UUID.class))
                .strength(rs.getInt("strength"))
                .agility(rs.getInt("agility"))
                .dexterity(rs.getInt("dexterity"))
                .intelligence(rs.getInt("intelligence"))
                .build();


        Hero hero = Hero.builder()
                .id(rs.getObject("id", UUID.class))
                .name(rs.getString("name"))
                .race(Race.valueOf(rs.getString("race")))
                .createdAt(rs.getTimestamp("created_at").toInstant())
                .updatedAt(rs.getTimestamp("updated_at").toInstant())
                .enabled(rs.getBoolean("enabled"))
                .build();


        hero.setPowerStats(powerStats);

        return hero;
    };


    UUID create(Hero hero) {
        final Map<String, Object> params = Map.of("name", hero.getName(),
            "race", hero.getRace().name(),
            "powerStatsId", hero.getPowerStatsId());

        return namedParameterJdbcTemplate.queryForObject(
            CREATE_HERO_QUERY,
            params,
            UUID.class);
    }

   Optional<Hero> findHeroByName(String name){
        List<Hero> results = namedParameterJdbcTemplate.query(
                FIND_HERO_NAME_QUERY,
                Map.of("name", name),
                heroRowMapper
        );
       return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));

   }


    Optional<Hero> findById(UUID id) {
        List<Hero> results= namedParameterJdbcTemplate.query(
                FIND_HERO_QUERY,
                Map.of("id", id),
                heroRowMapper
        );

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));

    }


    void update(UUID id, Hero hero) {
        namedParameterJdbcTemplate.update(UPDATE_HERO_QUERY,
                Map.of("id", id,
                        "name", hero.getName(),
                        "race", hero.getRace().name(),
                        "now", Timestamp.from(Instant.now()))
                );
    }

    void delete(UUID id){
        namedParameterJdbcTemplate.update(
                DELETE_HERO_QUERY,
                Map.of("id", id)
        );
    }
}
