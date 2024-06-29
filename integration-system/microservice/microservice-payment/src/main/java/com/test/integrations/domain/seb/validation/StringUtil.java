package com.test.integrations.domain.seb.validation;

import io.vavr.collection.Seq;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringUtil {
    public static Predicate<String> nonEmpty = s -> !s.trim().isEmpty();

    public static Function<Seq<String>, String> seqStringToString(final String prefix, final String delimiter) {
        return s -> prefix + delimiter + s.mkString(", ");
    }

    public static Function<Seq<String>, String> seqStringToString() {
        return seqStringToString("", "");
    }


}
