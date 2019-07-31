package org.test.transfer.db.validation;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Validator<ContextType> {
    private Consumer<ContextType> consumer;

    public void validate(ContextType context) throws ValidationException {
        if (context != null) {
            consumer.accept(context);
        }
    }
}
