package ex.nervisking.command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    String name();
    String description() default "";
    boolean permission() default false;
    String[] aliases() default {};
}
