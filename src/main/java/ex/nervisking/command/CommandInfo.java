package ex.nervisking.command;

import ex.nervisking.ModelManager.Pattern.KeyDef;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    @KeyDef String name();
    @KeyDef String per() default "";
    String description() default "";
    boolean permission() default false;
    String[] aliases() default {};
}
