package com.test.integrations.domain.seb.validation;

import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Validator<T> {

    public static <U> Validator<U> validate(final U value) {
        return new Valid<>(value);
    }

    /**
     * Combines {@code Validator.validate(value).nonNull(message);} into one.
     * Most (if not all) validation validates nonNull anyway.
     *
     * @param value the value to validate
     * @param message the message if value is null
     * @param <U> the value type
     * @return the validator
     */
    public static <U> Validator<U> validateNonNull(final U value, final String message) {
        return value != null ? new Valid<>(value) : new Invalid<>(message);
    }

    public abstract Validator<T> nonNull(final String message);

    public abstract Validator<T> and(final Predicate<T> p, final String message);

    /**
     * Uses the extractor before running the predicate.
     * Useful when validating complex objects.
     *
     * @param p the predicate
     * @param extractor the function that extracts the thing to run the predicate on
     * @param message the error message if the predicate fails
     * @param <U> the type of the thing
     * @return the validator
     */
    public abstract <U> Validator<T> and(final Predicate<? super U> p, final Function<? super T, ? extends U> extractor, final String message);

    public abstract <U> Validator<U> map(Function<? super T, ? extends U> f);

    /**
     * Map the value using a function that returns an option.
     * If the option contains a value that value is extracted.
     * If the option is empty it is regarded as an error.
     *
     * @param f the function to use, returns an Option of the value to map to
     * @param message the message to use if option is empty
     * @param <U> The new type
     * @return valid if the option is not empty, invalid otherwise
     */
    public abstract <U> Validator<U> mapOption(final Function<? super T, Option<? extends U>> f, final String message);

    public abstract <U> Validator<U> mapTry(final Function<? super T, Try<? extends U>> f, final String message);

    public abstract <U> Validator<U> tryMap(Function<? super T, ? extends U> f, final String message);

    public abstract Validation<String, T> toValidation();

    private static final class Valid<T> extends Validator<T> {
        private final T value;

        private Valid(final T value) {
            this.value = value;
        }

        @Override
        public Validator<T> nonNull(final String message) {
            return value != null ? this : new Invalid<>(message);
        }

        @Override
        public Validator<T> and(final Predicate<T> p, final String message) {
            return p.test(value) ? this : new Invalid<>(message);
        }

        @Override
        public <U> Validator<T> and(final Predicate<? super U> p, final Function<? super T, ? extends U> extractor, final String message) {
            return p.test(extractor.apply(value)) ? this : new Invalid<>(message);
        }

        @Override
        public <U> Validator<U> map(final Function<? super T, ? extends U> f) {
            return new Valid<>(f.apply(value));
        }

        @Override
        public <U> Validator<U> mapOption(final Function<? super T, Option<? extends U>> f, final String message) {
            return f.apply(value)
                    .fold(
                            () -> new Invalid<>(message),
                            Valid<U>::new
                    );
        }

        @Override
        public <U> Validator<U> mapTry(final Function<? super T, Try<? extends U>> f, final String message) {
            return f.apply(value)
                    .fold(
                            e -> new Invalid<>(message),
                            Valid<U>::new
                    );
        }

        @Override
        public <U> Validator<U> tryMap(final Function<? super T, ? extends U> f, final String message) {
            try {
                return map(f);
            } catch (final Exception e) {
                return new Invalid<>(message);
            }
        }

        @Override
        public Validation<String, T> toValidation() {
            return Validation.valid(value);
        }
    }

    private static final class Invalid<T> extends Validator<T> {
        private final String message;

        Invalid(final String message) {
            this.message = message;
        }

        @Override
        public Validator<T> nonNull(final String message) {
            return this;
        }

        @Override
        public Validator<T> and(final Predicate<T> p, final String message) {
            return this;
        }

        @Override
        public <U> Validator<T> and(final Predicate<? super U> p, final Function<? super T, ? extends U> extractor, final String message) {
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Validator<U> map(final Function<? super T, ? extends U> f) {
            return (Validator<U>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Validator<U> mapOption(final Function<? super T, Option<? extends U>> f, final String message) {
            return (Validator<U>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Validator<U> mapTry(final Function<? super T, Try<? extends U>> f, final String message) {
            return (Validator<U>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Validator<U> tryMap(final Function<? super T, ? extends U> f, final String message) {
            return (Validator<U>) this;
        }

        @Override
        public Validation<String, T> toValidation() {
            return Validation.invalid(message);
        }

    }

}
