package shells.plugins.generic.gui.dialog;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import core.EasyI18N;
import core.ui.component.dialog.GOptionPane;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import shells.plugins.generic.model.Retransmission;
import shells.plugins.generic.model.enums.RetransmissionType;

public class ChooseNewRetransmissionDialog extends JDialog {
   private JPanel contentPane;
   private JButton buttonOK;
   private JButton buttonCancel;
   public JTextField listenAddressTextField;
   public JTextField listenPortTextField;
   public JComboBox proxyTypeComboBox;
   public JTextField targetAddressTextField;
   public JTextField targetPortTextField;
   public JLabel listenAddressLabel;
   public JLabel listenPortLabel;
   public JLabel proxyTypeLabel;
   public JLabel targetAddressLabel;
   public JLabel targetPortLabel;
   public Retransmission proxyModel;

   public ChooseNewRetransmissionDialog(Window parentWindow) {
      super(parentWindow);
      this.$$$setupUI$$$();
      this.setContentPane(this.contentPane);
      this.setModal(true);
      this.getRootPane().setDefaultButton(this.buttonOK);
      this.buttonOK.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ChooseNewRetransmissionDialog.this.onOK();
         }
      });
      this.buttonCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ChooseNewRetransmissionDialog.this.onCancel();
         }
      });
      this.setDefaultCloseOperation(0);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            ChooseNewRetransmissionDialog.this.onCancel();
         }
      });
      this.contentPane.registerKeyboardAction(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ChooseNewRetransmissionDialog.this.onCancel();
         }
      }, KeyStroke.getKeyStroke(27, 0), 1);
   }

   private void onOK() {
      if (this.proxyModel == null) {
         this.proxyModel = new Retransmission();
      }

      try {
         String chooseProxy = this.proxyTypeComboBox.getSelectedItem().toString().toUpperCase();
         this.proxyModel.listenAddress = this.listenAddressTextField.getText().trim();
         this.proxyModel.listenPort = Integer.parseInt(this.listenPortTextField.getText().trim());
         this.proxyModel.targetAddress = this.targetAddressTextField.getText().trim();
         this.proxyModel.targetPort = Integer.parseInt(this.targetPortTextField.getText().trim());
         this.proxyModel.retransmissionType = "PORT_FORWARD".equals(chooseProxy) ? RetransmissionType.PORT_FORWARD : ("PORT_MAP".equals(chooseProxy) ? RetransmissionType.PORT_MAP : RetransmissionType.NULL);
      } catch (Exception var2) {
         GOptionPane.showMessageDialog(this, var2.getLocalizedMessage());
         return;
      }

      this.dispose();
   }

   private void onCancel() {
      this.proxyModel = null;
      this.dispose();
   }

   public static Retransmission chooseNewProxy(Window parentWindow) {
      ChooseNewRetransmissionDialog dialog = new ChooseNewRetransmissionDialog(parentWindow);
      dialog.pack();
      dialog.setLocationRelativeTo(parentWindow);
      EasyI18N.installObject(dialog);
      dialog.setVisible(true);
      return dialog.proxyModel;
   }

   private void $$$setupUI$$$() {
      this.contentPane = new JPanel();
      this.contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
      JPanel panel1 = new JPanel();
      panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
      this.contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 1, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      JPanel panel2 = new JPanel();
      panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
      panel1.add(panel2, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.buttonOK = new JButton();
      this.buttonOK.setText("添加");
      panel2.add(this.buttonOK, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.buttonCancel = new JButton();
      this.buttonCancel.setText("取消");
      panel2.add(this.buttonCancel, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      JPanel panel3 = new JPanel();
      panel3.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
      this.contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.listenAddressLabel = new JLabel();
      this.listenAddressLabel.setText("监听地址");
      panel3.add(this.listenAddressLabel, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.listenAddressTextField = new JTextField();
      this.listenAddressTextField.setText("127.0.0.1");
      panel3.add(this.listenAddressTextField, new GridConstraints(0, 1, 1, 1, 8, 1, 4, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null, 0, false));
      this.listenPortLabel = new JLabel();
      this.listenPortLabel.setText("监听端口");
      panel3.add(this.listenPortLabel, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.listenPortTextField = new JTextField();
      this.listenPortTextField.setText("6666");
      panel3.add(this.listenPortTextField, new GridConstraints(1, 1, 1, 1, 8, 1, 4, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null, 0, false));
      this.proxyTypeLabel = new JLabel();
      this.proxyTypeLabel.setText("代理类型");
      panel3.add(this.proxyTypeLabel, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.proxyTypeComboBox = new JComboBox();
      DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
      defaultComboBoxModel1.addElement("PORT_FORWARD");
      defaultComboBoxModel1.addElement("PORT_MAP");
      this.proxyTypeComboBox.setModel(defaultComboBoxModel1);
      panel3.add(this.proxyTypeComboBox, new GridConstraints(2, 1, 1, 1, 8, 1, 2, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.targetAddressLabel = new JLabel();
      this.targetAddressLabel.setText("目标地址");
      panel3.add(this.targetAddressLabel, new GridConstraints(3, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.targetAddressTextField = new JTextField();
      this.targetAddressTextField.setText("8.8.8.8");
      panel3.add(this.targetAddressTextField, new GridConstraints(3, 1, 1, 1, 8, 1, 4, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null, 0, false));
      this.targetPortLabel = new JLabel();
      this.targetPortLabel.setText("目标端口");
      panel3.add(this.targetPortLabel, new GridConstraints(4, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.targetPortTextField = new JTextField();
      this.targetPortTextField.setText("53");
      panel3.add(this.targetPortTextField, new GridConstraints(4, 1, 1, 1, 8, 1, 4, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null, 0, false));
   }

   public JComponent $$$getRootComponent$$$() {
      return this.contentPane;
   }

   private void createUIComponents() {
   }
}
