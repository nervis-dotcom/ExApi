package ex.nervisking.command;

import ex.nervisking.ModelManager.Pattern.KeyAlphaNum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    @KeyAlphaNum String name();
    @KeyAlphaNum String per() default "";
    String description() default "";
    boolean permission() default false;
    String[] aliases() default {};
}
