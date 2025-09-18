package br.com.gubee.interview.core.features.powerstats;

import br.com.gubee.interview.model.PowerStats;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PowerStatsRepository {

    private static final String CREATE_POWER_STATS_QUERY = "INSERT INTO power_stats" +
        " (strength, agility, dexterity, intelligence)" +
        " VALUES (:strength, :agility, :dexterity, :intelligence) RETURNING id";

    private static final String UPDATE_POWER_STATS_QUERY = "UPDATE power_stats SET strength = :strength, " +
            "agility = :agility, dexterity = :dexterity, intelligence = :intelligence, updated_at = :now" +
            " WHERE id = :id";

    private static final String DELETE_POWER_STATS_QUERY = "DELETE FROM power_stats WHERE id = :id";


    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    UUID create(PowerStats powerStats) {
        return namedParameterJdbcTemplate.queryForObject(
            CREATE_POWER_STATS_QUERY,
            new BeanPropertySqlParameterSource(powerStats),
            UUID.class);
    }

    void update(UUID id, PowerStats powerStats) {
        namedParameterJdbcTemplate.update(
                UPDATE_POWER_STATS_QUERY,
                Map.of("id", id,
                        "strength", powerStats.getStrength(),
                        "agility", powerStats.getAgility(),
                        "dexterity", powerStats.getDexterity(),
                        "intelligence", powerStats.getIntelligence(),
                        "now", Timestamp.from(Instant.now()))
        );
    }

    void delete(UUID id){
        namedParameterJdbcTemplate.update(
                DELETE_POWER_STATS_QUERY,
                Map.of("id", id)
        );


    }
}
