package util;

import core.ApplicationContext;
import core.ui.component.dialog.GOptionPane;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;

public class OpenC implements ActionListener {
   private final String keyString;
   private final JCheckBox checkBox;

   public OpenC(String key, JCheckBox checkBox) {
      this.keyString = key;
      this.checkBox = checkBox;
   }

   public void actionPerformed(ActionEvent paramActionEvent) {
      if (ApplicationContext.setOpenC(this.keyString, this.checkBox.isSelected())) {
         GOptionPane.showMessageDialog((Component)null, "修改成功!", "提示", 1);
      } else {
         GOptionPane.showMessageDialog((Component)null, "修改失败!", "提示", 2);
      }

   }
}
