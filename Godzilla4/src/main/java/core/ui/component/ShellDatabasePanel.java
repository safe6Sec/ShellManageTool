package core.ui.component;

import core.Encoding;
import core.annotation.DisplayName;
import core.imp.Payload;
import core.shell.ShellEntity;
import core.ui.component.dialog.DatabaseSetting;
import core.ui.component.dialog.GFileChooser;
import core.ui.component.dialog.GOptionPane;
import core.ui.component.listener.ActionDblClick;
import core.ui.component.model.DbInfo;
import core.ui.config.DatabaseSql;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;

@DisplayName(
   DisplayName = "数据库管理"
)
public class ShellDatabasePanel extends JPanel {
   private static final String[] EXEC_TYPES = new String[]{"select", "update"};
   private static final String[] SQL_EXAMPLE = new String[]{"SELECT 1;", "SELECT COUNT(1) FROM tableName", "SELECT VERSION();"};
   private ShellEntity shellEntity;
   private JSplitPane splitPane;
   private JButton execButton;
   private JButton dbsetButton;
   private DataTree dblist;
   private DataView dataView;
   private RTextArea sqlCommand;
   private JScrollPane dblistpane;
   private JScrollPane datalistpane;
   private JScrollPane commandpane;
   private JComboBox<String> execTypeComboBox;
   private JComboBox<String> commonsql;
   private JLabel statusLabel;
   private JLabel execTypeLabel;
   private JLabel currentDbLabel;
   private JLabel sql_listLabel;
   private JTextField currentDbTextField;
   private DefaultMutableTreeNode databaseTreeNode;
   private Payload payload;
   private DbInfo dbInfo;
   private Encoding encoding;
   private JPopupMenu dataViewPopupMenu;
   private JPopupMenu dblistPopupMenu;

   public ShellDatabasePanel(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = this.shellEntity.getPayloadModule();
      this.encoding = shellEntity.getDbEncodingModule();
      this.dbInfo = new DbInfo(this.encoding);
      this.splitPane = new JSplitPane();
      this.databaseTreeNode = new DefaultMutableTreeNode("Database");
      this.splitPane.setOrientation(0);
      this.statusLabel = new JLabel("state");
      this.execTypeLabel = new JLabel("Exec Type");
      this.currentDbLabel = new JLabel("CurrentDatabase");
      this.sql_listLabel = new JLabel("SQL Statement");
      this.currentDbTextField = new JTextField("", 10);
      this.dblist = new DataTree("", this.databaseTreeNode);
      this.dblistpane = new JScrollPane(this.dblist);
      this.dblistpane.setPreferredSize(new Dimension(25, 0));
      this.dblist.setShowsRootHandles(true);
      this.dblist.setRootVisible(false);
      this.execTypeComboBox = new JComboBox(EXEC_TYPES);
      this.dataView = new DataView((Vector)null, (Vector)null, -1, -1);
      this.datalistpane = new JScrollPane(this.dataView);
      this.dataView.setAutoResizeMode(0);
      this.datalistpane.setPreferredSize(new Dimension(0, 0));
      this.sqlCommand = new RTextArea();
      this.commandpane = new JScrollPane(this.sqlCommand);
      this.sqlCommand.setText("");
      UiFunction.setSyntaxEditingStyle(this.sqlCommand, "user.sql");
      this.commonsql = new JComboBox(SQL_EXAMPLE);
      this.commonsql.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ShellDatabasePanel.this.sqlCommand.setText((String)ShellDatabasePanel.this.commonsql.getSelectedItem());
         }
      });
      this.dbsetButton = new JButton("DbInfoConfig");
      this.dataViewPopupMenu = new JPopupMenu();
      JMenuItem copyselectItem = new JMenuItem("复制选中");
      copyselectItem.setActionCommand("copySelected");
      JMenuItem copyselectedLineItem = new JMenuItem("复制选中行");
      copyselectedLineItem.setActionCommand("copyselectedLine");
      JMenuItem exportAllItem = new JMenuItem("导出");
      exportAllItem.setActionCommand("exportData");
      this.dataViewPopupMenu.add(copyselectItem);
      this.dataViewPopupMenu.add(copyselectedLineItem);
      this.dataViewPopupMenu.add(exportAllItem);
      this.dataView.setRightClickMenu(this.dataViewPopupMenu);
      automaticBindClick.bindMenuItemClick(this.dataViewPopupMenu, (Map)null, this);
      this.dblistPopupMenu = new JPopupMenu();
      JMenuItem countTableItem = new JMenuItem("Count");
      countTableItem.setActionCommand("countTable");
      this.dblistPopupMenu.add(countTableItem);
      automaticBindClick.bindMenuItemClick(this.dblistPopupMenu, (Map)null, this);
      this.dblist.setChildPopupMenu(this.dblistPopupMenu);
      this.execButton = new JButton("Exec SQL");
      this.setLayout(new GridBagLayout());
      GBC gbcleft = (new GBC(0, 0, 2, 4)).setFill(3).setWeight(0.0, 1.0).setIpad(200, 0);
      GBC gbcright1 = (new GBC(2, 0, 7, 1)).setFill(1).setWeight(1.0, 0.7).setInsets(0, 7, 0, 0);
      GBC gbcright2_1 = (new GBC(2, 1, 1, 1)).setFill(0).setInsets(0, 5, 0, 0);
      GBC gbcright2_2 = (new GBC(3, 1, 1, 1)).setFill(2).setWeight(1.0, 0.0);
      GBC gbcright2_3 = (new GBC(4, 1, 1, 1)).setFill(0);
      GBC gbcright2_4 = (new GBC(5, 1, 1, 1)).setFill(2).setWeight(1.0, 0.0);
      GBC gbcright2_5 = (new GBC(6, 1, 1, 1)).setFill(0);
      GBC gbcright2_6 = (new GBC(7, 1, 1, 1)).setFill(2).setWeight(1.0, 0.0);
      GBC gbcright2_7 = (new GBC(8, 1, 1, 1)).setFill(0);
      GBC gbcright3 = (new GBC(2, 2, 8, 1)).setFill(1).setWeight(1.0, 0.3).setInsets(0, 5, 0, 0);
      GBC gbcright4_1 = (new GBC(2, 3, 7, 1)).setFill(2).setWeight(1.0, 0.0).setInsets(0, 7, 0, 0);
      GBC gbcstatus = (new GBC(0, 4, 9, 1)).setFill(2).setWeight(1.0, 0.0);
      this.add(this.dblistpane, gbcleft);
      this.add(this.datalistpane, gbcright1);
      this.add(this.execTypeLabel, gbcright2_1);
      this.add(this.execTypeComboBox, gbcright2_2);
      this.add(this.currentDbLabel, gbcright2_3);
      this.add(this.currentDbTextField, gbcright2_4);
      this.add(this.sql_listLabel, gbcright2_5);
      this.add(this.commonsql, gbcright2_6);
      this.add(this.commandpane, gbcright3);
      this.add(this.dbsetButton, gbcright2_7);
      this.add(this.execButton, gbcright4_1);
      this.add(this.statusLabel, gbcstatus);
      automaticBindClick.bindJButtonClick(this, this);
      this.dblist.setActionDbclick(new ActionDblClick() {
         public void dblClick(MouseEvent e) {
            ShellDatabasePanel.this.fileDataTreeDbClick(e);
         }
      });
   }

   private void fileDataTreeDbClick(MouseEvent e) {
      String[] s = this.dblist.GetSelectFile().split("/");
      if (s.length == 1) {
         this.fillDbListByTable(s[0]);
      } else if (s.length == 2) {
         this.fillDataviewByDT(s[0], s[1]);
      }

   }

   private void dbsetButtonClick(ActionEvent actionEvent) {
      String lastConfig = this.dbInfo.toString();
      new DatabaseSetting(this.shellEntity, this.dbInfo);
      String newConfig = this.dbInfo.toString();
      this.encoding = this.dbInfo.getCharset();
      if (!lastConfig.equals(newConfig)) {
         (new Thread(new Runnable() {
            public void run() {
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     ShellDatabasePanel.this.fillDbListByDatabase();
                  }
               });
            }
         })).start();
      }

   }

   private void fillDbListByDatabase() {
      this.currentDbTextField.setText("");
      this.dblist.removeAll();
      String sqlString = (String)DatabaseSql.sqlMap.get(String.format("%s-getAllDatabase", this.dbInfo.getDbType().toLowerCase()));
      if (sqlString != null) {
         this.sqlCommand.setText(sqlString);
         String result = this.execSql("select", sqlString);
         if (this.showData(result) && this.dataView.getModel().getColumnCount() == 1) {
            Vector rows = this.dataView.getModel().getDataVector();

            for(int i = 0; i < rows.size(); ++i) {
               Vector row = (Vector)rows.get(i);
               this.dblist.AddNote(row.get(0).toString());
            }
         }
      } else {
         Log.error(String.format("Fill Database Fail! NO SQL %s", this.dbInfo.getDbType()));
      }

   }

   private void fillDbListByTable(String databaseName) {
      this.currentDbTextField.setText(databaseName);
      String sqlString = (String)DatabaseSql.sqlMap.get(String.format("%s-getTableByDatabase", this.dbInfo.getDbType().toLowerCase()));
      if (sqlString != null) {
         sqlString = this.formatSql(sqlString, (String)null);
         this.sqlCommand.setText(sqlString);
         String result = this.execSql("select", sqlString);
         if (this.showData(result) && this.dataView.getModel().getColumnCount() == 1) {
            Vector rows = this.dataView.getModel().getDataVector();

            for(int i = 0; i < rows.size(); ++i) {
               Vector row = (Vector)rows.get(i);
               this.dblist.AddNote(String.format("%s/%s", databaseName, row.get(0)));
            }
         }
      } else {
         Log.error(String.format("Fill Table Fail! NO SQL %s", this.dbInfo.getDbType()));
      }

   }

   private void fillDataviewByDT(String databaseName, String tableName) {
      this.currentDbTextField.setText(databaseName);
      String sqlString = (String)DatabaseSql.sqlMap.get(String.format("%s-getTableDataByDT", this.dbInfo.getDbType().toLowerCase()));
      if (sqlString != null) {
         sqlString = this.formatSql(sqlString, tableName);
         this.sqlCommand.setText(sqlString);
         String result = this.execSql("select", sqlString);
         this.showData(result);
      } else {
         Log.error(String.format("Fill TableData Fail! NO SQL %s", this.dbInfo.getDbType()));
      }

   }

   private String formatSql(String sql, String tableName) {
      String databaseName = this.currentDbTextField.getText().trim();
      return sql.replace("{tableName}", tableName == null ? "null" : tableName).replace("{databaseName}", databaseName);
   }

   private void execButtonClick(ActionEvent actionEvent) {
      String execSql = this.sqlCommand.getText();
      String execType = (String)this.execTypeComboBox.getSelectedItem();
      if (execSql != null && execSql.trim().length() > 0) {
         String result = this.execSql(execType, execSql);
         this.showData(result);
      } else {
         GOptionPane.showMessageDialog((Component)null, "SQL语句是空的", "提示", 2);
      }

   }

   public String execSql(String execType, String execSql) {
      Map options = new HashMap();
      options.put("dbCharset", this.encoding.getCharsetString());
      String currentDb = this.currentDbTextField.getText().trim();
      if (currentDb.length() > 0) {
         options.put("currentDb", currentDb);
      }

      return this.payload.execSql(this.dbInfo.getDbType(), this.dbInfo.getDbHost(), this.dbInfo.getDbPort(), this.dbInfo.getDbUserName(), this.dbInfo.getDbPassword(), execType, options, execSql);
   }

   public boolean showData(String data) {
      boolean state = false;
      if (data != null) {
         String[] datas = data.split("\n");
         Vector<String> columns = this.dataView.getColumnVector();
         Vector<Vector<String>> rowsVector = new Vector();
         if (datas[0].equals("ok")) {
            Vector row;
            if (datas.length > 1) {
               columns.clear();
               this.formatSqlResult(datas[1], columns);

               for(int i = 2; i < datas.length; ++i) {
                  row = new Vector();
                  this.formatSqlResult(datas[i], row);
                  rowsVector.add(row);
               }

               this.showData(rowsVector);
               state = true;
            } else {
               row = new Vector();
               row.add("");
               rowsVector.add(row);
               this.dataView.getModel().setColumnIdentifiers(row);
               this.showData(rowsVector);
            }
         } else {
            GOptionPane.showMessageDialog((Component)null, data, "提示", 2);
            Log.error(data);
         }
      } else {
         Log.error("exec SQL Result Is Null");
      }

      return state;
   }

   public void showData(Vector<Vector<String>> rowsVector) {
      this.dataView.AddRows(rowsVector);
      this.dataView.getModel().fireTableDataChanged();
   }

   public void formatSqlResult(String row, Vector<String> destVector) {
      String[] line = row.split("\t");

      for(int i = 0; i < line.length; ++i) {
         destVector.add(this.encoding.Decoding(functions.base64Decode(line[i])));
      }

   }

   private void copySelectedMenuItemClick(ActionEvent e) {
      int columnIndex = this.dataView.getSelectedColumn();
      if (columnIndex != -1) {
         Object o = this.dataView.getValueAt(this.dataView.getSelectedRow(), this.dataView.getSelectedColumn());
         if (o != null) {
            String value = (String)o;
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(value), (ClipboardOwner)null);
            GOptionPane.showMessageDialog((Component)null, "复制成功", "提示", 1);
         } else {
            GOptionPane.showMessageDialog((Component)null, "选中列是空的", "提示", 2);
         }
      } else {
         GOptionPane.showMessageDialog((Component)null, "未选中列", "提示", 2);
      }

   }

   private void copyselectedLineMenuItemClick(ActionEvent e) {
      int columnIndex = this.dataView.getSelectedColumn();
      if (columnIndex != -1) {
         String[] o = this.dataView.GetSelectRow1();
         if (o != null) {
            String value = Arrays.toString(o);
            this.dataView.GetSelectRow1();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(value), (ClipboardOwner)null);
            GOptionPane.showMessageDialog((Component)null, "复制成功", "提示", 1);
         } else {
            GOptionPane.showMessageDialog((Component)null, "选中列是空的", "提示", 2);
         }
      } else {
         GOptionPane.showMessageDialog((Component)null, "未选中列", "提示", 2);
      }

   }

   private void countTableMenuItemClick(ActionEvent e) {
      String[] s = this.dblist.GetSelectFile().split("/");
      if (s.length == 2) {
         this.currentDbTextField.setText(s[0]);
         String sqlString = (String)DatabaseSql.sqlMap.get(String.format("%s-getCountByDT", this.dbInfo.getDbType().toLowerCase()));
         if (sqlString != null) {
            sqlString = this.formatSql(sqlString, s[1]);
            this.sqlCommand.setText(sqlString);
            String result = this.execSql("select", sqlString);
            this.showData(result);
         } else {
            Log.error(String.format("Fill TableData Fail! NO SQL %s", this.dbInfo.getDbType()));
         }
      }

   }

   private void exportDataMenuItemClick(ActionEvent e) {
      GFileChooser chooser = new GFileChooser();
      chooser.setFileSelectionMode(0);
      chooser.setFileFilter(new FileNameExtensionFilter("*.csv", new String[]{"csv"}));
      boolean flag = 0 == chooser.showDialog(new JLabel(), "选择");
      File selectdFile = chooser.getSelectedFile();
      if (flag && selectdFile != null) {
         String fileString = selectdFile.getAbsolutePath();
         if (!fileString.endsWith(".csv")) {
            fileString = fileString + ".csv";
         }

         if (functions.saveDataViewToCsv(this.dataView.getColumnVector(), this.dataView.getModel().getDataVector(), fileString)) {
            GOptionPane.showMessageDialog((Component)null, "导出成功", "提示", 1);
         } else {
            GOptionPane.showMessageDialog((Component)null, "导出失败", "提示", 1);
         }
      } else {
         Log.log("用户取消选择......");
      }

   }
}
