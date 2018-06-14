package com.shuzau.transfer.domain.core;

import java.util.function.Consumer;

import com.shuzau.transfer.domain.exception.TransferException;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Validators {

    private static void report(String error) {
        throw new TransferException(error);
    }

    private static void report(Seq<String> errors) {
        report(errors.intersperse(". ")
                     .foldLeft(new StringBuilder(), StringBuilder::append)
                     .toString());
    }

    public static Consumer<Validation<String, ?>> validator() {
        return validation -> validation.toEither()
                                       .orElseRun(Validators::report);
    }

    public static Consumer<Validation<Seq<String>, ?>> multiValidator() {
        return validation -> validation.toEither()
                                       .orElseRun(Validators::report);
    }
}
