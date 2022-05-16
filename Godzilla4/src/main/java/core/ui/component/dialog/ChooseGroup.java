package core.ui.component.dialog;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import core.EasyI18N;
import core.ui.component.ShellGroup;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import util.UiFunction;
import util.functions;

public class ChooseGroup extends JDialog {
   private JPanel contentPane;
   private JButton buttonOK;
   private JButton buttonCancel;
   public JPanel groupPanel;
   public ShellGroup groupTree;
   private String groupId;
   private Window parentWindow;

   public ChooseGroup(Window parentWindow, String defaultGroup) {
      this.parentWindow = parentWindow;
      this.$$$setupUI$$$();
      this.groupTree.setSelectNote(defaultGroup);
      this.setContentPane(this.contentPane);
      this.setModal(true);
      this.getRootPane().setDefaultButton(this.buttonOK);
      this.buttonOK.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ChooseGroup.this.onOK();
         }
      });
      this.buttonCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ChooseGroup.this.onCancel();
         }
      });
      this.setDefaultCloseOperation(0);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            ChooseGroup.this.onCancel();
         }
      });
      this.contentPane.registerKeyboardAction(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ChooseGroup.this.onCancel();
         }
      }, KeyStroke.getKeyStroke(27, 0), 1);
      EasyI18N.installObject(this);
   }

   private void onOK() {
      this.groupId = this.groupTree.getSelectedGroupName();
      if (this.groupId.isEmpty()) {
         this.groupId = null;
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.groupTree), "未选中组!");
      } else {
         this.dispose();
      }
   }

   private void onCancel() {
      this.groupId = null;
      this.dispose();
   }

   public String getChooseGroup() {
      this.setTitle("选择分组");
      this.pack();
      functions.setWindowSize(this, 600, 630);
      this.setLocationRelativeTo(this.parentWindow);
      EasyI18N.installObject(this);
      this.setVisible(true);
      return this.groupId;
   }

   private void $$$setupUI$$$() {
      this.contentPane = new JPanel();
      this.contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
      JPanel panel1 = new JPanel();
      panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
      this.contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 1, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      Spacer spacer1 = new Spacer();
      panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, 0, 1, 4, 1, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      JPanel panel2 = new JPanel();
      panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
      panel1.add(panel2, new GridConstraints(0, 1, 1, 1, 0, 3, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.buttonOK = new JButton();
      this.buttonOK.setText("OK");
      panel2.add(this.buttonOK, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.buttonCancel = new JButton();
      this.buttonCancel.setText("Cancel");
      panel2.add(this.buttonCancel, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.groupPanel = new JPanel();
      this.groupPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
      this.contentPane.add(this.groupPanel, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      JScrollPane scrollPane1 = new JScrollPane();
      this.groupPanel.add(scrollPane1, new GridConstraints(0, 0, 1, 1, 0, 3, 5, 5, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.groupTree = new ShellGroup();
      scrollPane1.setViewportView(this.groupTree);
   }

   public JComponent $$$getRootComponent$$$() {
      return this.contentPane;
   }

   private void createUIComponents() {
   }
}
