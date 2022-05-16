package core.ui.component.dialog;

import core.ui.MainActivity;
import javax.swing.JDialog;

public class ShellSetting extends JDialog {
   public ShellSetting(String id) {
      super(MainActivity.getFrame(), "Shell Setting", true);
      new core.ui.component.frame.ShellSetting(id, "/");
   }
}
