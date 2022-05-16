package core.ui.component;

import core.annotation.DisplayName;
import core.shell.ShellEntity;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@DisplayName(
   DisplayName = "基础信息"
)
public class ShellBasicsInfo extends JPanel {
   private final ShellEntity shellEntity;
   private final RTextArea basicsInfoTextArea;

   public ShellBasicsInfo(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      super.setLayout(new BorderLayout(1, 1));
      this.basicsInfoTextArea = new RTextArea();
      this.basicsInfoTextArea.setEditable(false);
      super.add(new JScrollPane(this.basicsInfoTextArea));
      this.basicsInfoTextArea.setText(shellEntity.getPayloadModule().getBasicsInfo());
   }
}
