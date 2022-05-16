package core.ui.component.frame;

import core.Db;
import core.EasyI18N;
import core.shell.ShellEntity;
import core.ui.MainActivity;
import core.ui.component.DataView;
import core.ui.component.dialog.GOptionPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class LiveScan extends JDialog {
   private DataView shellView;
   private JButton addShellButton;
   private JButton removeFailShellButton;
   private JButton scanButton;
   private JButton refreshButton;
   private Vector<String> columnVector;
   private JSplitPane splitPane;
   private boolean isRuning;
   private String groupName;
   private ComponentRenderer COMPONENT_RENDERER;
   private static JLabel OK_LABEL = new JLabel("Succes");
   private static JLabel FAIL_LABEL = new JLabel("Fail");
   private static JLabel WAIT_LABEL = new JLabel("wait");
   private static JLabel DELETE_LABEL = new JLabel("deleted");

   public LiveScan() {
      this("/");
   }

   public LiveScan(String groupId) {
      super(MainActivity.getFrame(), "LiveScan", true);
      this.COMPONENT_RENDERER = new ComponentRenderer();
      this.groupName = groupId;
      this.addShellButton = new JButton("添加Shell");
      this.removeFailShellButton = new JButton("移除所有失败");
      this.refreshButton = new JButton("刷新");
      this.scanButton = new JButton("扫描");
      this.splitPane = new JSplitPane();
      Vector<Vector<String>> allShellVector = new Vector();
      allShellVector.addAll(Db.getAllShell(this.groupName));
      this.columnVector = (Vector)allShellVector.remove(0);
      this.columnVector.add("Status");
      this.shellView = new DataView((Vector)null, this.columnVector, -1, -1);
      this.refreshshellView();
      JPanel bottomPanel = new JPanel();
      bottomPanel.add(this.addShellButton);
      bottomPanel.add(this.scanButton);
      bottomPanel.add(this.refreshButton);
      bottomPanel.add(this.removeFailShellButton);
      this.splitPane.setOrientation(0);
      this.splitPane.setTopComponent(new JScrollPane(this.shellView));
      this.splitPane.setBottomComponent(bottomPanel);
      this.splitPane.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
            LiveScan.this.splitPane.setDividerLocation(0.85);
         }
      });
      JMenuItem removeShellMenuItem = new JMenuItem("删除");
      removeShellMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            int selectedRow = LiveScan.this.shellView.getSelectedRow();
            int lastColumn = LiveScan.this.shellView.getColumnCount() - 1;
            if (selectedRow != -1) {
               String shellId = (String)LiveScan.this.shellView.getValueAt(selectedRow, 0);
               if (shellId != null) {
                  ShellEntity shellEntity = Db.getOneShell(shellId);
                  Log.log("removeShell -> " + shellEntity.toString());
                  if (Db.removeShell(shellId) > 0) {
                     GOptionPane.showMessageDialog((Component)null, "删除成功");
                  } else {
                     GOptionPane.showMessageDialog((Component)null, "删除失败");
                  }

                  LiveScan.this.shellView.setValueAt(LiveScan.DELETE_LABEL, selectedRow, lastColumn);
               }
            }

         }
      });
      this.shellView.getRightClickMenu().add(removeShellMenuItem);
      automaticBindClick.bindJButtonClick(this, this);
      this.add(this.splitPane);
      functions.setWindowSize(this, 510, 430);
      this.setLocationRelativeTo(MainActivity.getFrame());
      this.setDefaultCloseOperation(2);
      this.shellView.getColumn("Status").setCellRenderer(this.COMPONENT_RENDERER);
      EasyI18N.installObject(this);
      EasyI18N.installObject(this.shellView);
      this.setVisible(true);
   }

   protected void refreshshellView() {
      Vector<Vector<String>> rows = Db.getAllShell(this.groupName);
      rows.remove(0);
      rows.forEach((oneRow) -> {
         oneRow.add("WAIT_LABEL");
      });
      this.shellView.AddRows(rows);
      int max = rows.size();
      int lastColumn = this.shellView.getColumnCount() - 1;

      for(int i = 0; i < max; ++i) {
         this.shellView.setValueAt(WAIT_LABEL, i, lastColumn);
      }

      this.shellView.getModel().fireTableDataChanged();
   }

   protected void addShellButtonClick(ActionEvent actionEvent) {
      new core.ui.component.dialog.ShellSetting((String)null);
      this.refreshshellView();
   }

   private void removeFailShellButtonClick(ActionEvent actionEvent) {
      int max = this.shellView.getRowCount();
      int lastColumn = this.shellView.getColumnCount() - 1;
      Object valueObject = null;
      int removeNum = 0;

      for(int i = 0; i < max; ++i) {
         valueObject = this.shellView.getValueAt(i, lastColumn);
         if (FAIL_LABEL.equals(valueObject)) {
            String shellId = (String)this.shellView.getValueAt(i, 0);
            if (shellId != null) {
               ShellEntity shellEntity = Db.getOneShell(shellId);
               Db.removeShell(shellId);
               Log.log("removeShell -> " + shellEntity.toString());
               this.shellView.setValueAt(DELETE_LABEL, i, lastColumn);
               ++removeNum;
            }
         }
      }

      GOptionPane.showMessageDialog(this, String.format(EasyI18N.getI18nString("共删除%s条Shell"), removeNum));
   }

   protected synchronized void scanButtonClick(ActionEvent actionEvent) {
      if (!this.isRuning) {
         (new Thread(new Runnable() {
            public void run() {
               try {
                  LiveScan.this.scanStrart();
               } catch (Exception var5) {
                  Log.error((Throwable)var5);
               } finally {
                  LiveScan.this.isRuning = false;
               }

            }
         })).start();
         GOptionPane.showMessageDialog(this, "已开始存活检测");
      } else {
         GOptionPane.showMessageDialog(this, "正在检测");
      }

   }

   protected void scanStrart() {
      long startTime = System.currentTimeMillis();
      int max = this.shellView.getRowCount();
      int lastColumn = this.shellView.getColumnCount() - 1;
      Object valueObject = null;
      ThreadPoolExecutor executor = new ThreadPoolExecutor(30, 50, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue());
      Log.log(String.format("LiveScanStart startTime:%s", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis())));

      for(int i = 0; i < max; ++i) {
         this.shellView.setValueAt(WAIT_LABEL, i, lastColumn);
         String shellId = (String)this.shellView.getValueAt(i, 0);
         executor.execute(new ScanShellRunnable(shellId, this.shellView, i, lastColumn));
      }

      while(executor.getActiveCount() != 0) {
      }

      executor.shutdown();
      long endTime = System.currentTimeMillis();
      Log.log(String.format("LiveScanComplete completeTime:%s", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis())));
      int succes = 0;
      int fail = 0;

      for(int i = 0; i < max; ++i) {
         valueObject = this.shellView.getValueAt(i, lastColumn);
         if (OK_LABEL.equals(valueObject)) {
            ++succes;
         } else if (FAIL_LABEL.equals(valueObject)) {
            ++fail;
         }
      }

      Log.log(String.format("LiveScanComplete: 用时:%sms", endTime - startTime));
      this.setTitle(String.format("LiveScan all:%s succes:%s fail:%s", max, succes, fail));
      GOptionPane.showMessageDialog(this, "Scan complete!");
      Log.log("Scan complete!");
   }

   protected void refreshButtonClick(ActionEvent actionEvent) {
      this.refreshshellView();
      this.shellView.getColumn("Status").setCellRenderer(this.COMPONENT_RENDERER);
   }

   static {
      OK_LABEL.setOpaque(true);
      FAIL_LABEL.setOpaque(true);
      WAIT_LABEL.setOpaque(true);
      DELETE_LABEL.setOpaque(true);
      DELETE_LABEL.setBackground(Color.DARK_GRAY);
      WAIT_LABEL.setBackground(Color.CYAN);
      OK_LABEL.setBackground(Color.GREEN);
      FAIL_LABEL.setBackground(Color.RED);
   }

   class ComponentRenderer implements TableCellRenderer {
      public ComponentRenderer() {
      }

      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
         if (Component.class.isAssignableFrom(value.getClass())) {
            Component component = (Component)value;
            if (isSelected) {
               component.setForeground(table.getSelectionForeground());
            } else {
               component.setForeground(table.getForeground());
            }

            return component;
         } else {
            return new JLabel(value.toString());
         }
      }
   }

   class ScanShellRunnable implements Runnable {
      private String shellId;
      private DataView dataView;
      private int rowId;
      private int columnId;

      public ScanShellRunnable(String shellId, DataView dataView, int rowId, int columnId) {
         this.shellId = shellId;
         this.dataView = dataView;
         this.rowId = rowId;
         this.columnId = columnId;
      }

      public void run() {
         boolean ok = false;

         try {
            ShellEntity shellEntity = Db.getOneShell(this.shellId);
            ok = shellEntity.initShellOpertion();

            try {
               if (ok) {
                  shellEntity.getPayloadModule().close();
               }
            } catch (Exception var6) {
               Log.error((Throwable)var6);
            }
         } catch (Exception var7) {
            Log.error((Throwable)var7);
         }

         final boolean finalOk = ok;

         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                  if (finalOk) {
                     ScanShellRunnable.this.dataView.setValueAt(LiveScan.OK_LABEL, ScanShellRunnable.this.rowId, ScanShellRunnable.this.columnId);
                  } else {
                     ScanShellRunnable.this.dataView.setValueAt(LiveScan.FAIL_LABEL, ScanShellRunnable.this.rowId, ScanShellRunnable.this.columnId);
                  }

               }
            });
         } catch (InterruptedException var4) {
            var4.printStackTrace();
         } catch (InvocationTargetException var5) {
            var5.printStackTrace();
         }

      }
   }
}
