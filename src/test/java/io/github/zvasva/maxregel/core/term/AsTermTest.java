package io.github.zvasva.maxregel.core.term;

import io.github.zvasva.maxregel.util.ReflectionUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static io.github.zvasva.maxregel.util.PrettyPrint.print;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the AsTerm class, which provides a reflective way to access properties of a wrapped object.
 * <p>
 * This test class verifies the functionality of the AsTerm class by testing the following scenarios:
 * <p>
 * - Retrieval of existing and non-existing fields from the wrapped object.
 * - Handling null and empty field names.
 * - Equality checks between different AsTerm instances.
 * - Interoperability with MapTerm instances.
 * - Hash code generation consistency for different orders of map keys.
 * - Union function utility with non-overlapping keys.
 */
public class AsTermTest {

    @Test
    void testGetWithExistingField() {
        // Arrange
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        // Act
        Object result = asTerm.get("name");

        // Assert
        assertEquals("John Doe", result);
    }

    @Test
    void testGetWithNonExistingField() {
        // Arrange
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        // Act
        Object result = asTerm.get("nonExistingField");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetWithNullField() {
        // Arrange
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        // Act
        Object result = asTerm.get(null);

        // Assert
        assertNull(result);
    }

    @Test
    public void testEqualsWithDifferentPersons() {
        Persooon person1 = new Persooon("John Doe", 25);
        Persooon person2 = new Persooon("Jane Doe", 30);

        ObjectAsTerm obj1 = new ObjectAsTerm(person1);
        ObjectAsTerm obj2 = new ObjectAsTerm(person2);

        // Test that the two objects are not considered equal because their contents are different
        assertNotEquals(obj1, obj2);
    }

    @Test
    public void testEqualsWithSamePersons() {
        Persooon person1 = new Persooon("John Doe", 25);
        Persooon person2 = new Persooon("John Doe", 25);

        ObjectAsTerm obj1 = new ObjectAsTerm(person1);
        ObjectAsTerm obj2 = new ObjectAsTerm(person2);

        // Test that the two objects are considered equal because their contents are the same
        assertEquals(obj1, obj2);
    }


    @Test
    void testGetWithExistingKey() {
        // Arrange
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        // Act
        Object result = asTerm.get("name");

        // Assert
        assertEquals("John Doe", result);
    }

    void testInteroperabilityWithMapTerm() {
        // Arrange
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        MapTerm mapTerm = MapTerm.of("name", "John Doe", "age", 25);

        // Act & Assert
        assertEquals(asTerm, mapTerm);
        assertEquals(asTerm.get("name"), mapTerm.get("name"));
        assertEquals(asTerm.get("age"), mapTerm.get("age"));
    }


    @Test
    void testInteroperabilityWithMapTermDifferentOrder() {
        // Arrange
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        MapTerm mapTerm = MapTerm.of("age", 25, "name", "John Doe");

        // Act & Assert
        assertEquals(asTerm, mapTerm);
        assertEquals(asTerm.get("name"), mapTerm.get("name"));
        assertEquals(asTerm.get("age"), mapTerm.get("age"));
    }

    @Test
    void testGetWithNonExistingKey() {
        // Arrange
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        // Act
        Object result = asTerm.get("nonExisting");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetWithNullKey() {
        // Arrange
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        // Act
        Object result = asTerm.get(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetWithEmptyKey() {
        // Arrange
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        // Act
        Object result = asTerm.get("");

        // Assert
        assertNull(result);
    }

    @Test
    void testEqualsWithDifferentMapOrders() {
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        MapTerm mapTerm1 = MapTerm.of("name", "John Doe", "age", 25);
        MapTerm mapTerm2 = MapTerm.of("age", 25, "name", "John Doe");

        assertEquals(mapTerm1, mapTerm2);
    }

    @Test
    void testHashCodeWithDifferentMapOrders() {
        Persooon person = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm = new ObjectAsTerm(person);

        MapTerm mapTerm1 = MapTerm.of("name", "John Doe", "age", 25);
        MapTerm mapTerm2 = MapTerm.of("age", 25, "name", "John Doe");

        assertEquals(mapTerm1.hashCode(), mapTerm2.hashCode());
    }

    @Test
    void testEqualsWithDifferentContent() {
        Persooon person1 = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm1 = new ObjectAsTerm(person1);

        Persooon person2 = new Persooon("Jane Doe", 30);
        ObjectAsTerm asTerm2 = new ObjectAsTerm(person2);

        assertNotEquals(asTerm1, asTerm2);
    }

    @Test
    void testUnionWithNonOverlappingKeys() {
        Persooon person1 = new Persooon("John Doe", 25);
        ObjectAsTerm asTerm1 = new ObjectAsTerm(person1);

        MapTerm mapTerm = MapTerm.of("address", "123 Main St", "city", "New York");

        Term union = Terms.union(asTerm1, mapTerm);

        print(union);


        assertEquals(union.get("name"), "John Doe");
        assertEquals(union.get("address"), "123 Main St");
    }


    @Test
    void testPersonRecord() {
        PersonRecord person1 = new PersonRecord("John Doe", 25);
        ObjectAsTerm asTerm1 = new ObjectAsTerm(person1);

        System.out.println("Terms.toString(asTerm1) = " + Terms.toString(asTerm1));
        MapTerm mapTerm1 = MapTerm.of("name", "John Doe", "age", 25);

        assertEquals(asTerm1, mapTerm1);
    }

    @Test
    void testAbstractTerm() {
        PersonAbstractTerm person1 = new PersonAbstractTerm("John Doe", 25);

        System.out.println("Terms.toString(asTerm1) = " + Terms.toString(person1));
        MapTerm mapTerm1 = MapTerm.of("name", "John Doe", "age", 25);

        Term intersection = Terms.intersection(person1, mapTerm1);

        assertEquals(intersection, mapTerm1);
    }

    @Test
    void testExtendsObjectAsTerm() {
        Foo foo = new Foo("John Doe", 25);

        System.out.println("Terms.toString(foo) = " + Terms.toString(foo));

        MapTerm mapTerm1 = MapTerm.of("name", "John Doe", "age", 25);

        Term intersection = Terms.intersection(foo, mapTerm1);

        assertEquals(intersection, mapTerm1);
    }


    public static class PersonAbstractTerm extends AbstractTerm {
        public String name;
        public int age;

        public PersonAbstractTerm(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public boolean has(String key) {
            return ReflectionUtil.hasField(this, key);
        }

        @Override
        public Object get(String key) {
            return  ReflectionUtil.getValue(this, key);
        }

        @Override
        public List<String> keys() {
            return ReflectionUtil.allGetLikeNames(this);
        }
    }



    record PersonRecord(String name, int age){}


    public static class Persooon {
        private String name;
        private int age;

        public Persooon(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Persooon person = (Persooon) o;

            if (age != person.age) return false;
            return Objects.equals(name, person.name);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + age;
            return result;
        }
    }

    class Foo extends ObjectAsTerm {
        String name;
        int age;

        public Foo(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}