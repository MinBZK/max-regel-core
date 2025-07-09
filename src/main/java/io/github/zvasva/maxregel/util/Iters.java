package io.github.zvasva.maxregel.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class providing various methods for working with Iterators, Iterables, and Streams.
 * @author Arvid Halma
 */
public class Iters {


    /**
     * Convert a Stream to an Iterable.
     * @param stream input
     * @return output
     * @param <T> element type
     */
    public static <T> Iterable<T> iterable(Stream<T> stream) {
        return stream::iterator;
    }

    /**
     * Convert an Iterator to an Iterable.
     * @param iterator input
     * @return output
     * @param <T> element type
     */
    public static <T> Iterable<T> iterable(Iterator<T> iterator) {
        return () -> iterator;
    }

    /**
     * Convert an Iterator to a Stream.
     * @param iterator input
     * @return output
     * @param <T> element type
     */
    public static <T> Stream<T> stream(Iterator<T> iterator) {
        return StreamSupport.stream(iterable(iterator).spliterator(), false);
    }

    /**
     * Convert an Iterable to a Stream.
     * @param iterable input
     * @return output
     * @param <T> element type
     */
    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Convert values to a Set.
     * @param iterator input
     * @return output
     * @param <T> element type
     */
    public static <T> Set<T> toSet(Iterator<T> iterator){
        return stream(iterator).collect(Collectors.toSet());
    }

    /**
     * Convert values to a Set.
     * @param iterable input
     * @return output
     * @param <T> element type
     */
    public static <T> Set<T> toSet(Iterable<T> iterable){
        return stream(iterable).collect(Collectors.toSet());
    }

    /**
     * Convert values to a List.
     * @param iterator input
     * @return output
     * @param <T> element type
     */
    public static <T> List<T> toList(Iterator<T> iterator){
        return stream(iterator).collect(Collectors.toList());
    }

    /**
     * Convert values to a List.
     * @param iterable input
     * @return output
     * @param <T> element type
     */
    public static <T> List<T> toList(Iterable<T> iterable){
        return stream(iterable).collect(Collectors.toList());
    }

    /**
     * Concatenate two Iterators into a Stream.
     * @param as first values
     * @param bs second values
     * @return output
     * @param <T> element type
     */
    public static <T> Stream<T> concatStream(Iterator<T> as, Iterator<T> bs) {
        return concatStream(iterable(as), iterable(bs));
    }

    /**
     * Concatenate two Iterables into a Stream.
     * @param as first values
     * @param bs second values
     * @return output
     * @param <T> element type
     */
    public static <T> Stream<T> concatStream(Iterable<T> as, Iterable<T> bs) {
        return Stream.concat(stream(as), stream(bs));
    }

    /**
     * Concatenate an Iterable of Iterables into a flat Stream.
     * @param xss Iterable of Iterables
     * @return output
     * @param <T> element type
     */
    public static <T> Stream<T> concatStream(Iterable<Iterable<T>> xss) {
        // Use flatMap to flatten each Iterable in xss into a single Stream<T>
        return StreamSupport.stream(xss.spliterator(), false)
                .flatMap(xs -> StreamSupport.stream(xs.spliterator(), false));
    }

    /**
     * Concatenate two Iterators into a flat Iterator.
     * @param as first values
     * @param bs second values
     * @return output
     * @param <T> element type
     */
    public static <T> Iterator<T> concatIterator(Iterator<T> as, Iterator<T> bs) {
        return concatIterator(iterable(as), iterable(bs));
    }

    /**
     * Concatenate two Iterables into a flat Iterator.
     * @param as first values
     * @param bs second values
     * @return output
     * @param <T> element type
     */
    public static <T> Iterator<T> concatIterator(Iterable<T> as, Iterable<T> bs) {
        return concatStream(as, bs).iterator();
    }

    /**
     * Concatenate an Iterable of Iterables into a flat Iterator.
     * @param xss Iterable of Iterables
     * @return output
     * @param <T> element type
     */
    public static <T> Iterator<T> concatIterator(Iterable<Iterable<T>> xss) {
        return concatStream(xss).iterator();
    }

    /**
     * Concatenate two Iterables into a flat Iterable.
     * @param as first values
     * @param bs second values
     * @return output
     * @param <T> element type
     */
    public static <T> Iterable<T> concat(Iterable<T> as, Iterable<T> bs) {
        return iterable(concatIterator(as, bs));
    }

    /**
     * Concatenate an Iterable of Iterables into a flat Iterable.
     * @param xss Iterable of Iterables
     * @return output
     * @param <T> element type
     */
    public static <T> Iterable<T> concat(Iterable<Iterable<T>> xss) {
        return iterable(concatIterator(xss));
    }

    /**
     * Concatenate a stream of Iterators into a flat Iterator.
     * @param iteratorStream a stream of Iterators
     * @return output
     * @param <T> element type
     */
    public static <T> Iterator<T> concat(Stream<Iterator<T>> iteratorStream) {
        // Concatenate all iterators in the stream
        return iteratorStream.flatMap(it -> {
            Iterable<T> iterable = () -> it;
            return StreamSupport.stream(iterable.spliterator(), false);
        }).iterator();
    }

    /**
     * Create an empty Iterable.
     * @return new Iterable
     * @param <T> element type
     */
    public static  <T> Iterable<T>  empty() {
        return Collections::emptyIterator;
    }

    /**
     * Check if an Iterable is empty.
     * @param iterable input
     * @return true if empty, false otherwise
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return !iterable.iterator().hasNext();
    }

    /**
     * Count the number of elements in an Iterable.
     * @param iterable input
     * @return number of elements
     */
    public static long count(Iterable<?> iterable) {
        return stream(iterable).count();
    }

    /**
     * Get the first element of a Stream.
     * @param stream input
     * @return first element or null if empty
     * @param <T> element type
     */
    public static <T> T first(Stream<T> stream) {
        return stream.findFirst().orElse(null);

    }

    /**
     * Get the first element of an Iterator or return null if empty.
     * @param iterator input
     * @return first element or null if empty
     * @param <T> element type
     */
    public static <T> T first(Iterator<T> iterator) {
        return first(iterator, null);

    }

    /**
     * Get the first element of an Iterator or return a default value if empty.
     * @param iterator input
     * @param defaultValue value to return if empty
     * @return first element or default value if empty
     * @param <T> element type
     */
    public static <T> T first(Iterator<T> iterator, T defaultValue) {
        return iterator.hasNext() ? iterator.next() : defaultValue;
    }

    /**
     * Get the first element of an Iterable or return null if empty.
     * @param iterable input
     * @return first element or null if empty
     * @param <T> element type
     */
    public static <T> T first(Iterable<T> iterable) {
        return first(iterable.iterator());
    }

    /**
     * Get the first element of an Iterable or return a default value if empty.
     * @param iterable input
     * @param defaultValue value to return if empty
     * @return first element or default value if empty
     * @param <T> element type
     */
    public static <T> T first(Iterable<T> iterable, T defaultValue) {
        return first(iterable.iterator(), defaultValue);
    }

    /**
     * Reverse the order of elements in an Iterator.
     * @param input input iterator
     * @return new iterator with elements in reverse order
     * @param <T> element type
     */
    public static <T> Iterator<T> reverse(Iterator<T> input) {
        return toList(input).reversed().iterator();
    }

    /**
     * Reverse the order of elements in an Iterable.
     * @param input input iterable
     * @return new iterable with elements in reverse order
     * @param <T> element type
     */
    public static <T> Iterable<T> reverse(Iterable<T> input) {
        return () -> toList(input).reversed().iterator();
    }

    /**
     * Reverse the order of elements in a Stream.
     * @param input input stream
     * @return new stream with elements in reverse order
     * @param <T> element type
     */
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length)
                .mapToObj(i -> temp[temp.length - i - 1]);
    }

    /**
     * Turns <code>Iterator A</code> into <code>Iterator B</code>
     * @param <A> the initial value type of the iterator
     * @param <B> the new value type of returned iterator
     * @author Arvid Halma
     */
    public static class MappingIterator<A,B> implements Iterator<B> {
        private Iterator<A> as;
        private Function<A, B> mapping;


        public MappingIterator(Iterator<A> as, Function<A, B> mapping) {
            this.as = as;
            this.mapping = mapping;
        }

        @Override
        public boolean hasNext() {
            return as.hasNext();
        }

        @Override
        public B next() {
            return mapping.apply(as.next());
        }
    }

}
