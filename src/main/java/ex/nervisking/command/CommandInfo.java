package ex.nervisking.command;

import java.lang.annotation.*;

/**
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    String name();
    String description() default "";
    boolean permission() default false;
    String[] aliases() default {};
}