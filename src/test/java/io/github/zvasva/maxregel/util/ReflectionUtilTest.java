package io.github.zvasva.maxregel.util;


import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflectionUtilTest {

    @Test
    public void testAssignToEmpty(){
        Person p = new Person();
        p.age = 14;
        p.favorites = List.of();
        System.out.println("\n...and a new person with age = 14 and favorites = []");
        System.out.println("p = " + p);

        Person p2 = new Person();

        System.out.println("\nA copy of p by assigning it to an empty person");
        ReflectionUtil.assignFields(p2, p);
        System.out.println("p2 = " + p2);
        assertEquals(p, p2);
    }

    @Test
    public void testAssignJohn(){
        Person john = new Person();
        john.name = "John";

        Person p = new Person();
        p.age = 14;
        p.favorites = List.of();
        System.out.println("\nAssign John");
        ReflectionUtil.assignFields(p, john, true);
        assertEquals(p, john);
    }

    @Test
    public void testAssignMary(){
        Person mary = new Person();
        mary.name = "Mary";
        mary.age = 68;
        mary.favorites = List.of("Beethoven", "Chocolate");

        Person p = new Person();
        p.age = 14;
        p.favorites = List.of();

        ReflectionUtil.assignFields(p, mary);
        System.out.println("\nAssign Mary");
        System.out.println("p = " + p);
        assertEquals(p, mary);
    }

    @Test
    public void testAnnaFromMap(){
        final Person anna = ReflectionUtil.assignFieldsFromMap(new Person(), Map.of("name", "Anna", "age", 30, "likes", 3));
        System.out.println("\nCopy Anna from map");
        System.out.println("anna = " + anna);
        assertEquals("Anna", anna.name);
        assertEquals(30, anna.age);
        assertEquals(3, anna.likes);
    }


    @Test
    public void testAnnaCoerceLikeInteger(){
        final Person anna = ReflectionUtil.assignFields(new Person(), Map.of("name", "Anna", "age", 30, "likes", 3));

        final Person p4 = ReflectionUtil.assignFields(new Person(), Map.of("name", "Anna", "age", 30, "likes", Integer.valueOf(3)));
        System.out.println("\nCopy Anna from map, with 'likes' field coerced");
        System.out.println("p4 = " + p4);
        assertEquals(anna, p4);
    }


    @Test
    public void testAnnaCoerceLikeString(){
        final Person anna = ReflectionUtil.assignFields(new Person(), Map.of("name", "Anna", "age", 30, "likes", 3));

        final Person p5 = ReflectionUtil.assignFields(new Person(), Map.of("name", "Anna", "age", "30", "likes", "3"));
        System.out.println("\nCopy Anna from map, with age coerced from string");
        System.out.println("p5 = " + p5);
        assertEquals(anna, p5);
    }


    @Test
    public void testAnnaToMap(){
        final Person anna = ReflectionUtil.assignFieldsFromMap(new Person(), Map.of("name", "Anna", "age", 30, "likes", 3));

        System.out.println("\nMake Anna a map again...");
        Map<String, Object> map = ReflectionUtil.asMap(anna);
        System.out.println("ReflectionUtil.toMap(anna) = " + map);
        assertEquals("Anna", map.get("name"));
        assertEquals(30, map.get("age"));
    }


    private static class Person {
        public String name;
        public int age;

        public Integer likes;
        public List<String> favorites;

        public String getName() {
            return name;
        }

        public Person setName(String name) {
            this.name = name;
            return this;
        }

        public int getAge() {
            return age;
        }

        public Integer getLikes() {
            return likes;
        }

        public Person setLikes(Integer likes) {
            this.likes = likes;
            return this;
        }

        public List<String> getFavorites() {
            return favorites;
        }

        public Person setFavorites(List<String> favorites) {
            this.favorites = favorites;
            return this;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Person person)) return false;
            return age == person.age && Objects.equals(name, person.name) && Objects.equals(likes, person.likes) && Objects.equals(favorites, person.favorites);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age, likes, favorites);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                    .add("name='" + getName() + "'")
                    .add("age=" + getAge())
                    .add("likes=" + getLikes())
                    .add("favorites=" + getFavorites())
                    .toString();
        }
    }

}
