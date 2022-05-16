package core.ui.component.dialog;

import core.EasyI18N;
import core.imp.Payload;
import core.shell.ShellEntity;
import core.ui.component.GBC;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class FileAttr extends JDialog {
   private final Payload payload;
   private final ShellEntity shellEntity;
   private final JLabel fileLabel = new JLabel("文件路径: ");
   private final JTextField fileTextField = new JTextField(20);
   private final JLabel filePermissionLabel = new JLabel("文件权限: ");
   private final JTextField filePermissionTextField = new JTextField(5);
   private final JButton updateFilePermissionButton = new JButton("修改");
   private final JLabel fileTimeLabel = new JLabel("文件修改时间: ");
   private final JTextField fileTimeTextField = new JTextField(15);
   private final JButton updateFileTimeButton = new JButton("修改");

   public FileAttr(ShellEntity shellEntity, String file, String filePermission, String fileTime) {
      super(shellEntity.getFrame(), "FileAttr", true);
      GBC gbcFileLabel = new GBC(0, 0);
      GBC gbcFileTextField = (new GBC(1, 0)).setInsets(0, 10, 0, 10);
      GBC gbcFilePermissionLabel = new GBC(0, 1);
      GBC gbcFilePermissionTextField = (new GBC(1, 1)).setInsets(0, 20, 0, 20);
      GBC gbcUpdateFilePermissionButton = (new GBC(2, 1)).setInsets(0, 20, 0, 20);
      GBC gbcFileTimeLabel = new GBC(0, 2);
      GBC gbcFileTimeTextField = (new GBC(1, 2)).setInsets(0, 10, 0, 10);
      GBC gbcUpdateFileTimeButton = (new GBC(2, 2)).setInsets(0, 20, 0, 20);
      this.fileTextField.setText(file);
      this.filePermissionTextField.setText(filePermission);
      this.fileTimeTextField.setText(fileTime);
      this.setLayout(new GridBagLayout());
      Container container = this.getContentPane();
      container.add(this.fileLabel, gbcFileLabel);
      container.add(this.fileTextField, gbcFileTextField);
      container.add(this.filePermissionLabel, gbcFilePermissionLabel);
      container.add(this.filePermissionTextField, gbcFilePermissionTextField);
      container.add(this.updateFilePermissionButton, gbcUpdateFilePermissionButton);
      container.add(this.fileTimeLabel, gbcFileTimeLabel);
      container.add(this.fileTimeTextField, gbcFileTimeTextField);
      container.add(this.updateFileTimeButton, gbcUpdateFileTimeButton);
      this.shellEntity = shellEntity;
      this.payload = shellEntity.getPayloadModule();
      automaticBindClick.bindJButtonClick(this, this);
      functions.setWindowSize(this, 520, 130);
      this.setLocationRelativeTo(shellEntity.getFrame());
      EasyI18N.installObject(this);
      this.setVisible(true);
   }

   public void updateFilePermissionButtonClick(ActionEvent e) {
      boolean state = false;
      String fileName = this.fileTextField.getText().trim();
      String filePermission = this.filePermissionTextField.getText().trim();

      try {
         state = this.payload.setFileAttr(fileName, "fileBasicAttr", filePermission);
      } catch (Exception var9) {
         Log.error((Throwable)var9);
      } finally {
         if (state) {
            GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "修改成功", "提示", 1);
            this.dispose();
         } else {
            GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "修改失败", "提示", 2);
         }

      }

   }

   public void updateFileTimeButtonClick(ActionEvent e) {
      boolean state = false;
      String fileName = this.fileTextField.getText().trim();
      String fileTime = this.fileTimeTextField.getText().trim();

      try {
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         String timestampString = Long.toString(simpleDateFormat.parse(fileTime).getTime());
         if (timestampString.length() > 10) {
            timestampString = timestampString.substring(0, 10);
         }

         state = this.payload.setFileAttr(fileName, "fileTimeAttr", timestampString);
      } catch (Exception var10) {
         Log.error((Throwable)var10);
      } finally {
         if (state) {
            GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "修改成功", "提示", 1);
            this.dispose();
         } else {
            GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "修改失败", "提示", 2);
         }

      }

   }
}
