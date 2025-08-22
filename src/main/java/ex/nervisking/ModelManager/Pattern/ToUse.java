package ex.nervisking.ModelManager.Pattern;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToUse {

    /**
     * Descripción general de lo que hace el método.
     */
    String value() default "unknown";

    String[] description() default {};

    /**
     * Lista de parámetros en formato "nombre: descripción".
     */
    String[] params() default {};

    /**
     * Descripción del valor de retorno.
     */
    String returns() default "void";


    String Throws() default "void";

    /**
     * Uso previsto o propósito general.
     */
    String usedFor() default "general";

    /**
     * Notas adicionales o advertencias.
     */
    String[] notes() default {};
}
