package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Terms;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transforms a factset to a normalized "long" format.
 * It creates an archive factset with tuples;
 * (timestamp, uuid (per this rule's instance), part, term key, term value)
 */
public class Archiver extends AbstractRule {
   
    private final String uuid;
    private final String timestamp;

    public Archiver() {
        uuid = UUID.randomUUID().toString();
        timestamp = Instant.now().toString();
    }

    @Override
    public String op() {
        return "archive";
    }    

    @Override
    public AstNode ast() {
        return createNode();
    }

    public record ArchiveRecord (
        String timestamp,
        String aanvraag_id,
        String fieldName,
        String fieldValue
    ) {}

    @Override
    public FactSet apply(FactSet factset) {
        List<Fact> archiveFacts = new ArrayList<>();
        for (String part : factset.remove("archive").parts()) {
            factset.get(part).stream()
            .flatMap(f -> Terms.asMap(f.getTerm()).entrySet().stream())
            .forEach(e ->
                    archiveFacts.add(new Fact(MapTerm.of(
                            "timestamp", timestamp,
                            "uuid", uuid,
                            "part", part,
                            "key", e.getKey(),
                            "value", e.getValue().toString()
                    )))
            );
        }
        return FactSets.create("archive", archiveFacts);
    }

}
