package io.github.zvasva.maxregel.core.process.factoperation;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.SinglePartFactSet;
import io.github.zvasva.maxregel.core.process.factoperation.ComputeAge;
import io.github.zvasva.maxregel.core.process.rule.Rule;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.util.Iters;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComputeAgeTest {
    
    public FactSet fake_people() {
        return new SinglePartFactSet(
            List.of(
                    new Fact(MapTerm.of("name", "Rens", "bsn", "1", "adresseerbaarobject", "12345665432", "geboortedatum", "1989-01-01")),
                    new Fact(MapTerm.of("name", "Marieke", "bsn", "2", "adresseerbaarobject", "12345665432", "geboortedatum", "1989-01-01")),
                    new Fact(MapTerm.of("name", "Sef", "bsn", "3", "adresseerbaarobject", "12345665432", "geboortedatum", "2022-01-01")),
                    new Fact(MapTerm.of("name", "Fien", "bsn", "4", "adresseerbaarobject", "12345665432", "geboortedatum", "2024-01-01")),
                    new Fact(MapTerm.of("name", "Menno", "bsn", "99", "adresseerbaarobject", "09876765456", "geboortedatum", "2022-01-01"))
            ), "personen"
        );
    }

    @Test
    public void testComputeAge() {
        LocalDate dobRens = LocalDate.of(1989, 1, 1);
        Rule test_rule = new ComputeAge("geboortedatum");
        FactSet result = test_rule.apply(fake_people());
        int ageRens = (int) Iters.first(result).get("leeftijd");
        assertEquals(dobRens.until(LocalDate.now()).getYears(), ageRens);
    }}
