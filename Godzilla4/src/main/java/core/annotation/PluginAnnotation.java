package core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginAnnotation {
   String payloadName();

   String Name();

   String DisplayName();
}
