package util;

import core.ApplicationContext;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

public class OpenC implements ActionListener {
    private final JCheckBox checkBox;
    private final String keyString;

    public OpenC(String key, JCheckBox checkBox2) {
        this.keyString = key;
        this.checkBox = checkBox2;
    }

    public void actionPerformed(ActionEvent paramActionEvent) {
        if (ApplicationContext.setOpenC(this.keyString, this.checkBox.isSelected())) {
            JOptionPane.showMessageDialog((Component) null, "修改成功!", "提示", 1);
        } else {
            JOptionPane.showMessageDialog((Component) null, "修改失败!", "提示", 2);
        }
    }
}
