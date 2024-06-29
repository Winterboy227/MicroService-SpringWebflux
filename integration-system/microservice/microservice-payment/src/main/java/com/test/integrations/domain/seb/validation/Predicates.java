package com.test.integrations.domain.seb.validation;

import io.vavr.collection.CharSeq;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Predicates {
    public static final String STRING_NUMERIC = "[0-9]";

    public static final String STRING_NUMERIC_WITH_SPACE = "[0-9 ]";
    public static final String STRING_NUMERIC_WITH_MINUS = "[0-9-]";
    public static final String STRING_NUMERIC_WITH_DOT = "[0-9.]";
    public static final String STRING_NUMERIC_WITH_COMMA = "[0-9,]";
    public static final String STRING_A_TO_Z_TO_LOWER = "[a-z]";
    public static final String STRING_A_TO_Z_AND_SPACE_TO_LOWER = "[a-z ]";
    public static final String STRING_A_TO_Z_TO_UPPER = "[A-Z]";
    public static final String STRING_A_TO_Z_AND_SPACE_TO_UPPER = "[A-Z ]";
    public static final String STRING_A_TO_Z_IGNORE_CASE = "[a-zA-Z]";
    public static final String STRING_A_TO_Z_AND_SPACE_IGNORE_CASE = "[a-zA-Z ]";
    public static final String STRING_A_TO_Z_ZERO_TO_NINE_IGNORE_CASE = "[a-zA-Z0-9]";
    public static final String STRING_A_TO_Z_ZERO_TO_NINE_MINUS_IGNORE_CASE = "[a-zA-Z0-9-]";
    public static final String STRING_A_TO_Z_ZERO_TO_NINE_TO_LOWER = "[a-z0-9]";
    public static final String STRING_A_TO_Z_ZERO_TO_NINE_AND_SPACE_IGNORE_CASE = "[a-zA-Z0-9 ]";
    public static final String STRING_A_TO_Z_ZERO_TO_NINE_AND_SPACE_TO_LOWER = "[a-z0-9 ]";

    public static <I> Predicate<I> type(final Class<? extends I> clazz) {
        requireNonNull(clazz, "class is null");
        return clazz::isInstance;
    }

    public static <I> Predicate<I> nonNull() {
        return Objects::nonNull;
    }

    public static <I> Predicate<I> isNull() {
        return Predicates.<I>nonNull().negate();
    }

    public static Predicate<String> nonEmptyString() {
        return emptyString().negate();
    }

    public static Predicate<String> emptyString() {
        return s -> s.trim().isEmpty();
    }

    public static Predicate<String> beginsWithChar = nonEmptyString().and(s -> Character.isLetter(s.charAt(0)));

    public static <I> Predicate<List<I>> nonEmptyList() {
        return Predicates.<I>emptyList().negate();
    }

    public static <I> Predicate<List<I>> emptyList() {
        return i -> i.size() == 0;
    }

    public static <I> Predicate<List<I>> allMatchNullInList() {
        return allMatchInList(Objects::isNull);
    }

    public static <I> Predicate<List<I>> anyMatchNullInList() {
        return anyMatchInList(Objects::isNull);
    }

    public static <I> Predicate<List<I>> anyMatchInList(final Predicate<? super I> predicate) {
        return l -> l.stream().anyMatch(predicate);
    }




    public static <I> Predicate<List<I>> allMatchInList(final Predicate<? super I> predicate) {
        return l -> l.stream().allMatch(predicate);
    }

    public static <I> Predicate<List<I>> allUniqueInList() {
        return list -> new HashSet<>(list).size() == list.size();
    }

    public static Predicate<String> nonMatch(final String pattern) {
        return Predicates.match(pattern).negate();
    }

    public static Predicate<String> match(final String pattern) {
        requireNonNull(pattern, "pattern is null");
        return nonEmptyString().and(s -> CharSeq.of(s)
                .replaceAll(pattern, "")
                .transform(CharSeq::isEmpty));
    }

    public static Predicate<String> stringEquals(final String value) {
        requireNonNull(value, "value is null");
        return value::equals;
    }

    public static Predicate<Integer> integerEquals(final int value) {
        return i -> i == value;
    }

    public static Predicate<String> stringEqualsIgnoreCase(final String value) {
        requireNonNull(value, "value is null");
        return value::equalsIgnoreCase;
    }

    public static <E extends Enum<E>> Predicate<String> enumEqualsIgnoreCase(final E[] enumValues) {
        requireNonNull(enumValues, "enum values are null");
        return s -> Stream.of(enumValues).anyMatch(e -> e.name().equalsIgnoreCase(s));
    }




    public static Predicate<Integer> positiveInteger() {
        return i -> i > 0;
    }

    public static Predicate<Integer> nonNegativeInteger() {
        return i -> i >= 0;
    }

    public static Predicate<Long> positiveLong() {
        return i -> i > 0;
    }

    public static Predicate<Long> nonNegativeLong() {
        return i -> i >= 0;
    }

    public static Predicate<Integer> negativeInteger() {
        return i -> i < 0;
    }

    public static Predicate<BigInteger> nonNegativeBigInteger() {
        return i -> BigInteger.ZERO.compareTo(i) <= 0;
    }

    public static Predicate<BigDecimal> positiveBigDecimal() {
        return i -> BigDecimal.ZERO.compareTo(i) < 0;
    }

    public static Predicate<BigDecimal> nonNegativeBigDecimal() {
        return i -> BigDecimal.ZERO.compareTo(i) <= 0;
    }

    public static Predicate<BigDecimal> negativeBigDecimal() {
        return i -> BigDecimal.ZERO.compareTo(i) > 0;
    }

    /**
     * Returns a predicate that tests wheter the input is between inclusive min and max i.e. [min, max].
     *
     * @param min the min, inclusive
     * @param max the max, inclusive
     * @param <T> the type, must extend {@link Comparable}
     * @return the oredicate
     */
    public static <T extends Comparable<T>> Predicate<T> betweenInclusive(final T min, final T max) {
        return i -> min.compareTo(i) <= 0 && max.compareTo(i) >= 0;
    }

    public static Predicate<String> equalsLength(final int length) {
        return s -> s.length() == length;
    }

    public static Predicate<String> equalsLength(final int... lengths) {
        return s -> IntStream.of(lengths).anyMatch(l -> l == s.length());
    }

    public static Predicate<String> equalsLength(final List<Integer> length) {
        requireNonNull(length, "list of length is null");
        return s -> length.contains(s.length());
    }

    public static Predicate<String> isShorterThan(final int length) {
        return str -> str.length() < length;
    }

    /**
     * Should rather be named isLongerOrEqualThan.
     *
     * This function gives true if longer or equal to the provided length.
     */
    public static Predicate<String> isLongerThan(final int length) {
        return isShorterThan(length).negate();
    }

    public static Predicate<String> maxLength(final int maxLength) {
        return str -> str.length() <= maxLength;
    }

    public static Predicate<String> minLength(final int minLength) {
        return str -> str.length() >= minLength;
    }



}
