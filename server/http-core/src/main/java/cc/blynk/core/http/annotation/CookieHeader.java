package cc.blynk.core.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to inject information into a class
 * field, bean property or classMethod parameter.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @since 1.0
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookieHeader {

    /**
     * Defines the name of the form parameter whose value will be used
     * to initialize the value of the annotated classMethod argument. The name is
     * specified in decoded form, any percent encoded literals within the value
     * will not be decoded and will instead be treated as literal text. E.g. if
     * the parameter name is "a b" then the value of the annotation is "a b",
     * <i>not</i> "a+b" or "a%20b".
     */
    String value();

}
