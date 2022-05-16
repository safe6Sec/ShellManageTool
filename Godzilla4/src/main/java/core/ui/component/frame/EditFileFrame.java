package core.ui.component.frame;

import core.EasyI18N;
import core.ui.component.RTabbedPane;
import core.ui.component.ShellRSFilePanel;
import java.awt.Component;
import java.awt.Container;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import util.functions;

public class EditFileFrame extends JFrame {
   private static final String TITLE = "{fileName}     shellId:{shellId}     - Godzilla-Notepad";
   private static final JLabel NO_FILE = new JLabel("NO_FILE");
   private static EditFileFrame editFileMainFrame;
   private static boolean isShow;
   private RTabbedPane tabbedPane = new RTabbedPane();
   private Container contentPane = this.getContentPane();

   private EditFileFrame() {
      super("Godzilla-Notepad");
      this.contentPane.add(this.tabbedPane);
      this.setContentPane(NO_FILE);
      this.tabbedPane.setRemoveListener((currentSize, removeSzie) -> {
         if (currentSize <= 0) {
            this.setContentPane(NO_FILE);
         }

      });
      this.tabbedPane.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            ShellRSFilePanel rsFilePanel = (ShellRSFilePanel)EditFileFrame.this.tabbedPane.getSelectedComponent();
            if (rsFilePanel != null) {
               EditFileFrame.this.setTitle(EditFileFrame.getTitle(rsFilePanel.getFile(), rsFilePanel));
            } else {
               EditFileFrame.this.setTitle("Godzilla-Notepad");
            }

         }
      });
      EasyI18N.installObject(this);
   }

   public static void OpenNewEdit(ShellRSFilePanel shellRSFilePanel) {
      if (!isShow) {
         functions.setWindowSize(editFileMainFrame, 1400, 521);
      }

      if (shellRSFilePanel != null) {
         if (editFileMainFrame.tabbedPane.getTabCount() == 0) {
            editFileMainFrame.setContentPane(editFileMainFrame.contentPane);
         }

         editFileMainFrame.tabbedPane.addTab((new File(shellRSFilePanel.getFile())).getName(), shellRSFilePanel);
         editFileMainFrame.tabbedPane.setSelectedComponent(shellRSFilePanel);
         editFileMainFrame.setVisible(true);
         if (!isShow) {
            editFileMainFrame.setLocationRelativeTo((Component)null);
            isShow = true;
         }

      }
   }

   public static void removeEdit(ShellRSFilePanel shellRSFilePanel) {
      editFileMainFrame.tabbedPane.remove(shellRSFilePanel);
   }

   private static String getTitle(String fileName, ShellRSFilePanel shellRSFilePanel) {
      return "{fileName}     shellId:{shellId}     - Godzilla-Notepad".replace("{fileName}", fileName).replace("{shellId}", shellRSFilePanel.getShellId());
   }

   static {
      NO_FILE.setHorizontalAlignment(0);
      editFileMainFrame = new EditFileFrame();
   }
}
