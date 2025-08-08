package ex.nervisking.ModelManager.Pattern;

import org.intellij.lang.annotations.Pattern;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
@Pattern("[a-z0-9]+") // solo minúsculas y números (sin mayúsculas)
public @interface KeyNoUp {
}
