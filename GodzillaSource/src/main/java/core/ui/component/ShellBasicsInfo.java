package core.ui.component;

import core.shell.ShellEntity;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ShellBasicsInfo extends JPanel {
    private RTextArea basicsInfoTextArea = new RTextArea();
    private ShellEntity shellEntity;

    public ShellBasicsInfo(ShellEntity shellEntity2) {
        this.shellEntity = shellEntity2;
        ShellBasicsInfo.super.setLayout(new BorderLayout(1, 1));
        this.basicsInfoTextArea.setEditable(false);
        ShellBasicsInfo.super.add(new JScrollPane(this.basicsInfoTextArea));
        this.basicsInfoTextArea.setText(shellEntity2.getPayloadModel().getBasicsInfo());
    }
}
