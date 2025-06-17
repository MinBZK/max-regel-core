package io.github.zvasva.maxregel.core.process;

/**
 * Denotes a logical or process error.
 * By making this {@link RuntimeException} more identifiable, we can catch logical errors from lower level exceptions,
 * and treat them differently (think of HTTP exception mappers for end user messages)
 *
 * @author Arvid Halma
 */
public class MaxRegelException extends RuntimeException {

    public MaxRegelException() {
    }

    public MaxRegelException(String message) {
        super(message);
    }

    public MaxRegelException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaxRegelException(Throwable cause) {
        super(cause);
    }

    public MaxRegelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static <T> T requireNonNull(T object, String message) {
        if(object == null) {
            throw new MaxRegelException(message);
        }
        return object;
    }

    public static <T> T requireNonNullArg(T object, String var) {
        if(object == null) {
            throw new MaxRegelException("Argument '" + var + "' is missing.");
        }
        return object;
    }
}
