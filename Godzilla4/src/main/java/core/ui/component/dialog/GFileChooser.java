package core.ui.component.dialog;

import core.EasyI18N;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class GFileChooser extends JFileChooser {
   private static String lastDirectory = (new File("")).getPath();

   public GFileChooser() {
      super(lastDirectory);
   }

   public GFileChooser(FileSystemView fsv) {
      super(lastDirectory, fsv);
   }

   public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
      int flag = super.showDialog(parent, EasyI18N.getI18nString(approveButtonText));
      if (flag == 0) {
         lastDirectory = this.getSelectedFile().getAbsolutePath();
      }

      return flag;
   }
}
