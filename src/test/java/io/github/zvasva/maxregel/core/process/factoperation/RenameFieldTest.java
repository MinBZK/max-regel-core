package io.github.zvasva.maxregel.core.process.factoperation;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.SinglePartFactSet;
import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.factoperation.RenameField;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Terms;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RenameFieldTest {
    
    @Test
    public void testFactOperation() {
        // Create a test fact with a field to rename
        Map<String, Object> factData = new HashMap<>();
        factData.put("fieldOld", "testValue");
        factData.put("bla", "otherValue");
        
        Fact testFact = new Fact(new MapTerm(factData) );
        
        // Create the rename operation
        RenameField renameField = new RenameField("fieldOld", "fieldNew");
        UnaryOperation<Fact> operation = renameField.factOperation();
        
        // Apply the operation
        Fact resultFact = operation.apply(testFact);
        
        // Get the result as a map for verification
        Map<String, Object> resultMap = Terms.asMap(resultFact.getTerm());
        
        // Verify the field was renamed correctly
        assertTrue(resultMap.containsKey("fieldNew"));
        assertEquals("testValue", resultMap.get("fieldNew"));
        
        // Verify the old field was removed
        assertFalse(resultMap.containsKey("fieldOld"));
        
        // Verify other fields remain untouched
        assertEquals("otherValue", resultMap.get("bla"));
    }
    
    @Test
    public void testApplyToFactSet() {
        // Create a test FactSet with a fact that has a field to rename
        Map<String, Object> factData = new HashMap<>();
        factData.put("oldName", "testValue");
        
        Fact testFact = new Fact(new MapTerm(factData) );
        FactSet factSet = new SinglePartFactSet(List.of(testFact), "test");
        
        // Apply the rename rule
        RenameField renameField = new RenameField("oldName", "newName");
        FactSet resultSet = renameField.apply(factSet);
        
        // Verify the operation was added to the FactSet
        assertNotEquals(factSet, resultSet);
    }
    
    @Test
    public void testRenameNonExistentField() {
        // Create a test fact without the field to rename
        Map<String, Object> factData = new HashMap<>();
        factData.put("existingField", "testValue");
        
        Fact testFact = new Fact(new MapTerm(factData) );
        
        // Create the rename operation for a non-existent field
        RenameField renameField = new RenameField("missingField", "newName");
        UnaryOperation<Fact> operation = renameField.factOperation();
        
        // Apply the operation
        Fact resultFact = operation.apply(testFact);
        
        // Get the result as a map for verification
        Map<String, Object> resultMap = Terms.asMap(resultFact.getTerm());
        
        // Verify the new field was added with null value
        assertTrue(resultMap.containsKey("newName"));
        assertNull(resultMap.get("newName"));
        
        // Verify existing fields remain untouched
        assertEquals("testValue", resultMap.get("existingField"));
    }
    
    @Test
    public void testAstNodeCreation() {
        RenameField renameField = new RenameField("oldField", "newField");
        assertNotNull(renameField.ast());
        assertEquals("rename_field", renameField.op());
    }
}
