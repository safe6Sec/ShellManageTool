package core.ui.component;

import core.EasyI18N;
import core.ui.component.dialog.GFileChooser;
import core.ui.component.dialog.GOptionPane;
import core.ui.component.listener.ActionDblClick;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.RowFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class DataView extends JTable {
   private static final long serialVersionUID = -8531006713898868252L;
   private JPopupMenu rightClickMenu;
   private RightClickEvent rightClickEvent;
   private final int imgColumn;
   private TableRowSorter sorter;
   private String lastFiter;
   private Vector columnNameVector;
   private DefaultTableModel model;

   private void initJtableConfig() {
      this.rightClickEvent = new RightClickEvent(this.rightClickMenu, this);
      this.addMouseListener(this.rightClickEvent);
      this.setSelectionMode(0);
      this.setAutoCreateRowSorter(true);
      this.setRowHeight(25);
      this.rightClickMenu = new JPopupMenu();
      JMenuItem copyselectItem = new JMenuItem("复制选中");
      copyselectItem.setActionCommand("copySelected");
      JMenuItem copyselectedLineItem = new JMenuItem("复制选中行");
      copyselectedLineItem.setActionCommand("copyselectedLine");
      JMenuItem exportAllItem = new JMenuItem("导出");
      exportAllItem.setActionCommand("exportData");
      this.rightClickMenu.add(copyselectItem);
      this.rightClickMenu.add(copyselectedLineItem);
      this.rightClickMenu.add(exportAllItem);
      this.setRightClickMenu(this.rightClickMenu);
      this.sorter = new TableRowSorter(super.dataModel);
      this.setRowSorter(this.sorter);
      automaticBindClick.bindMenuItemClick(this.rightClickMenu, (Map)null, this);
      this.addActionForKey("ctrl pressed F", new AbstractAction() {
         public void actionPerformed(ActionEvent e) {
            DataView.this.ctrlPassF(e);
         }
      });
   }

   public DataView() {
      this(new Vector(), new Vector(), -1, -1);
   }

   public DataView(Vector rowData, Vector columnNames, int imgColumn, int imgMaxWidth) {
      super(rowData, columnNames);
      this.lastFiter = "*";
      if (columnNames == null) {
         columnNames = new Vector();
      }

      if (rowData == null) {
         rowData = new Vector();
      }

      this.getModel().setDataVector(rowData, columnNames);
      this.columnNameVector = columnNames;
      this.imgColumn = imgColumn;
      if (imgColumn >= 0) {
         this.getColumnModel().getColumn(0).setMaxWidth(imgMaxWidth);
      }

      this.initJtableConfig();
      EasyI18N.installObject(this);
   }

   public void ctrlPassF(ActionEvent e) {
      Object filterObject = GOptionPane.showInputDialog((Component)null, "input filter", "input filter", 3, (Icon)null, (Object[])null, this.lastFiter);
      if (filterObject != null) {
         String fiter = filterObject.toString();
         this.lastFiter = fiter;
         if (fiter.isEmpty()) {
            this.sorter.setRowFilter((RowFilter)null);
         } else {
            this.sorter.setRowFilter(new RowFilter() {
               public boolean include(RowFilter.Entry entry) {
                  int count = entry.getValueCount();

                  for(int i = 0; i < count; ++i) {
                     if (functions.isMatch(entry.getStringValue(i), DataView.this.lastFiter, false)) {
                        return true;
                     }
                  }

                  return false;
               }
            });
         }
      } else {
         Log.log("用户取消选择");
      }

   }

   public void setActionDblClick(ActionDblClick actionDblClick) {
      if (this.rightClickEvent != null) {
         this.rightClickEvent.setActionListener(actionDblClick);
      }

   }

   public JPopupMenu getRightClickMenu() {
      return this.rightClickMenu;
   }

   public void addActionForKeyStroke(KeyStroke keyStroke, Action action) {
      this.getActionMap().put(keyStroke.toString(), action);
      this.getInputMap().put(keyStroke, keyStroke.toString());
   }

   public void addActionForKey(String keyString, Action action) {
      this.addActionForKeyStroke(KeyStroke.getKeyStroke(keyString), action);
   }

   public void RemoveALL() {
      DefaultTableModel defaultTableModel = this.getModel();

      while(defaultTableModel.getRowCount() > 0) {
         defaultTableModel.removeRow(0);
      }

      this.updateUI();
   }

   public TableRowSorter getSorter() {
      return this.sorter;
   }

   public void setSorter(TableRowSorter sorter) {
      this.sorter = sorter;
   }

   public Class getColumnClass(int column) {
      return column == this.imgColumn ? Icon.class : Object.class;
   }

   public Vector GetSelectRow() {
      int select_row_id = this.getSelectedRow();
      if (select_row_id == -1) {
         return null;
      } else {
         int column_num = this.getColumnCount();
         Vector vector = new Vector();

         for(int i = 0; i < column_num; ++i) {
            vector.add(this.getValueAt(select_row_id, i));
         }

         return vector;
      }
   }

   public Vector getColumnVector() {
      return this.columnNameVector;
   }

   public String[] GetSelectRow1() {
      int select_row_id = this.getSelectedRow();
      if (select_row_id != -1) {
         int column_num = this.getColumnCount();
         String[] select_row_columns = new String[column_num];

         for(int i = 0; i < column_num; ++i) {
            Object value = this.getValueAt(select_row_id, i);
            if (value instanceof String) {
               select_row_columns[i] = (String)value;
            } else if (value != null) {
               try {
                  select_row_columns[i] = value.toString();
               } catch (Exception var7) {
                  select_row_columns[i] = "null";
                  Log.error((Throwable)var7);
               }
            } else {
               select_row_columns[i] = "null";
            }
         }

         return select_row_columns;
      } else {
         return null;
      }
   }

   public DefaultTableModel getModel() {
      return super.dataModel != null ? (DefaultTableModel)super.dataModel : null;
   }

   public synchronized void AddRow(Object object) {
      Class<?> class1 = object.getClass();
      Field[] fields = class1.getFields();
      String field_name = null;
      String field_value = null;
      DefaultTableModel tableModel = this.getModel();
      Vector rowVector = new Vector(tableModel.getColumnCount());
      String[] columns = new String[tableModel.getColumnCount()];

      for(int i = 0; i < tableModel.getColumnCount(); ++i) {
         columns[i] = tableModel.getColumnName(i).toUpperCase();
         rowVector.add("NULL");
      }

      Field[] var16 = fields;
      int var10 = fields.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         Field field = var16[var11];
         field_name = field.getName();
         int find_id = Arrays.binarySearch(columns, field_name.substring(2).toUpperCase());
         if (field_name.startsWith("s_") && find_id != -1) {
            try {
               if (field.get(object) instanceof String) {
                  field_value = (String)field.get(object);
               } else {
                  field_value = "NULL";
               }
            } catch (Exception var15) {
               field_value = "NULL";
            }

            rowVector.set(find_id, field_value);
         }
      }

      tableModel.addRow(rowVector);
   }

   public synchronized void AddRow(Vector one_row) {
      DefaultTableModel tableModel = this.getModel();
      tableModel.addRow(one_row);
   }

   public synchronized Vector<Vector> getDataVector() {
      this.sorter.setRowFilter((RowFilter)null);
      return this.getModel().getDataVector();
   }

   public synchronized void AddRows(Vector rows) {
      this.sorter.setRowFilter((RowFilter)null);
      DefaultTableModel tableModel = this.getModel();
      Vector columnVector = this.getColumnVector();
      tableModel.setDataVector(rows, columnVector);
   }

   public synchronized void SetRow(int row_id, Object object) {
      Class<?> class1 = object.getClass();
      Field[] fields = class1.getFields();
      String field_name = null;
      String field_value = null;
      DefaultTableModel tableModel = this.getModel();
      Vector rowVector = (Vector)tableModel.getDataVector().get(row_id);
      String[] columns = new String[tableModel.getColumnCount()];

      for(int i = 0; i < tableModel.getColumnCount(); ++i) {
         columns[i] = tableModel.getColumnName(i).toUpperCase();
      }

      Field[] var17 = fields;
      int var11 = fields.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Field field = var17[var12];
         field_name = field.getName();
         int find_id = Arrays.binarySearch(columns, field_name.substring(2).toUpperCase());
         if (field_name.startsWith("s_") && find_id != -1) {
            try {
               if (field.get(object) instanceof String) {
                  field_value = (String)field.get(object);
               } else {
                  field_value = "NULL";
               }
            } catch (Exception var16) {
               field_value = "NULL";
            }

            rowVector.set(find_id, field_value);
         }
      }

   }

   public void setRightClickMenu(JPopupMenu rightClickMenu) {
      this.setRightClickMenu(rightClickMenu, false);
   }

   public void setRightClickMenu(JPopupMenu rightClickMenu, boolean append) {
      if (append) {
         MenuElement[] var3 = this.rightClickMenu.getSubElements();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            MenuElement c = var3[var5];
            rightClickMenu.add(c.getComponent());
         }
      }

      this.rightClickMenu = rightClickMenu;
      this.rightClickEvent.setRightClickMenu(rightClickMenu);
   }

   public JTableHeader getTableHeader() {
      JTableHeader tableHeader = super.getTableHeader();
      tableHeader.setReorderingAllowed(false);
      DefaultTableCellRenderer hr = (DefaultTableCellRenderer)tableHeader.getDefaultRenderer();
      hr.setHorizontalAlignment(0);
      return tableHeader;
   }

   public void addColumn(Object column) {
      this.getModel().addColumn(column);
   }

   public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
      DefaultTableCellRenderer cr = (DefaultTableCellRenderer)super.getDefaultRenderer(columnClass);
      cr.setHorizontalAlignment(0);
      return cr;
   }

   public boolean isCellEditable(int paramInt1, int paramInt2) {
      return false;
   }

   private void copySelectedMenuItemClick(ActionEvent e) {
      int columnIndex = this.getSelectedColumn();
      if (columnIndex != -1) {
         Object o = this.getValueAt(this.getSelectedRow(), this.getSelectedColumn());
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
      int columnIndex = this.getSelectedColumn();
      if (columnIndex != -1) {
         String[] o = this.GetSelectRow1();
         if (o != null) {
            String value = Arrays.toString(o);
            this.GetSelectRow1();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(value), (ClipboardOwner)null);
            GOptionPane.showMessageDialog((Component)null, "复制成功", "提示", 1);
         } else {
            GOptionPane.showMessageDialog((Component)null, "选中列是空的", "提示", 2);
         }
      } else {
         GOptionPane.showMessageDialog((Component)null, "未选中列", "提示", 2);
      }

   }

   private void exportDataMenuItemClick(ActionEvent e) {
      GFileChooser chooser = new GFileChooser();
      chooser.setFileSelectionMode(0);
      chooser.setFileFilter(new FileNameExtensionFilter("*.csv", new String[]{"csv"}));
      boolean flag = 0 == chooser.showDialog((Component)null, "选择");
      File selectdFile = chooser.getSelectedFile();
      if (flag && selectdFile != null) {
         String fileString = selectdFile.getAbsolutePath();
         if (!fileString.endsWith(".csv")) {
            fileString = fileString + ".csv";
         }

         if (functions.saveDataViewToCsv(this.getColumnVector(), this.getModel().getDataVector(), fileString)) {
            GOptionPane.showMessageDialog((Component)null, "导出成功", "提示", 1);
         } else {
            GOptionPane.showMessageDialog((Component)null, "导出失败", "提示", 1);
         }
      } else {
         Log.log("用户取消选择......");
      }

   }

   private class RightClickEvent extends MouseAdapter {
      private JPopupMenu rightClickMenu;
      private final DataView dataView;
      private ActionDblClick actionDblClick;

      public RightClickEvent(JPopupMenu rightClickMenu, DataView jtable) {
         this.rightClickMenu = rightClickMenu;
         this.dataView = jtable;
      }

      public void setRightClickMenu(JPopupMenu rightClickMenu) {
         this.rightClickMenu = rightClickMenu;
      }

      public void setActionListener(ActionDblClick event) {
         this.actionDblClick = event;
      }

      public void mouseClicked(MouseEvent mouseEvent) {
         if (mouseEvent.getButton() == 3) {
            if (this.rightClickMenu != null) {
               int i = this.dataView.rowAtPoint(mouseEvent.getPoint());
               if (i != -1) {
                  this.rightClickMenu.show(this.dataView, mouseEvent.getX(), mouseEvent.getY());
                  this.dataView.addRowSelectionInterval(i, i);
               }
            }
         } else if (mouseEvent.getClickCount() == 2 && this.actionDblClick != null) {
            this.actionDblClick.dblClick(mouseEvent);
         }

      }
   }
}
