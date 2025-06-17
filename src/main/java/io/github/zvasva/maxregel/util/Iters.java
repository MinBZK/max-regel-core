package io.github.zvasva.maxregel.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class providing various methods for working with Iterators, Iterables, and Streams.
 * @author Arvid Halma
 */
public class Iters {


    public static <T> Iterable<T> iterable(Stream<T> stream) {
        return stream::iterator;
    }

    public static <T> Iterable<T> iterable(Iterator<T> iterator) {
        return () -> iterator;
    }

    public static <T> Stream<T> stream(Iterator<T> iterator) {
        return StreamSupport.stream(iterable(iterator).spliterator(), false);
    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Set<T> toSet(Iterator<T> iterator){
        return stream(iterator).collect(Collectors.toSet());
    }

    public static <T> Set<T> toSet(Iterable<T> iterable){
        return stream(iterable).collect(Collectors.toSet());
    }

    public static <T> List<T> toList(Iterator<T> iterator){
        return stream(iterator).collect(Collectors.toList());
    }

    public static <T> List<T> toList(Iterable<T> iterable){
        return stream(iterable).collect(Collectors.toList());
    }

    public static <T> Stream<T> concatStream(Iterator<T> as, Iterator<T> bs) {
        return concatStream(iterable(as), iterable(bs));
    }

    public static <T> Stream<T> concatStream(Iterable<T> as, Iterable<T> bs) {
        return Stream.concat(stream(as), stream(bs));
    }

    public static <T> Stream<T> concatStream(Iterable<Iterable<T>> xss) {
        // Use flatMap to flatten each Iterable in xss into a single Stream<T>
        return StreamSupport.stream(xss.spliterator(), false)
                .flatMap(xs -> StreamSupport.stream(xs.spliterator(), false));
    }

    public static <T> Iterator<T> concatIterator(Iterator<T> as, Iterator<T> bs) {
        return concatIterator(iterable(as), iterable(bs));
    }

    public static <T> Iterator<T> concatIterator(Iterable<T> as, Iterable<T> bs) {
        return concatStream(as, bs).iterator();
    }

    public static <T> Iterator<T> concatIterator(Iterable<Iterable<T>> xss) {
        return concatStream(xss).iterator();
    }

    public static <T> Iterable<T> concat(Iterable<T> as, Iterable<T> bs) {
        return iterable(concatIterator(as, bs));
    }

    public static <T> Iterable<T> concat(Iterable<Iterable<T>> xss) {
        return iterable(concatIterator(xss));
    }

    public static <T> Iterator<T> concat(Stream<Iterator<T>> iteratorStream) {
        // Concatenate all iterators in the stream
        return iteratorStream.flatMap(it -> {
            Iterable<T> iterable = () -> it;
            return StreamSupport.stream(iterable.spliterator(), false);
        }).iterator();
    }

    public static  <T> Iterable<T>  empty() {
        return Collections::emptyIterator;
    }

    public static boolean isEmpty(Iterable<?> iterable) {
        return !iterable.iterator().hasNext();
    }

    public static long count(Iterable<?> iterable) {
        return stream(iterable).count();
    }

    public static <T> T first(Stream<T> stream) {
        return stream.findFirst().orElse(null);

    }

    public static <T> T first(Iterator<T> iterator) {
        return first(iterator, null);

    }

    public static <T> T first(Iterator<T> iterator, T defaultValue) {
        return iterator.hasNext() ? iterator.next() : defaultValue;
    }

    public static <T> T first(Iterable<T> iterable) {
        return first(iterable.iterator());
    }

    public static <T> T first(Iterable<T> iterable, T defaultValue) {
        return first(iterable.iterator(), defaultValue);
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
