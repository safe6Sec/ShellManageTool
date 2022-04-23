package core.ui.imp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ButtonToMenuItem {
    String name() default "";
}
