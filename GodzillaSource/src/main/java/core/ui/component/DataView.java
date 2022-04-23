package core.ui.component;

import core.ui.imp.ActionDblClick;
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
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
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
    private Vector columnNameVector;
    private final int imgColumn;
    private String lastFiter = "*";
    private RightClickEvent rightClickEvent;
    private JPopupMenu rightClickMenu;
    private TableRowSorter sorter;

    private void initJtableConfig() {
        this.rightClickEvent = new RightClickEvent(this.rightClickMenu, this);
        addMouseListener(this.rightClickEvent);
        setSelectionMode(0);
        setAutoCreateRowSorter(true);
        setRowHeight(25);
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
        setRightClickMenu(this.rightClickMenu);
        this.sorter = new TableRowSorter(super.dataModel);
        setRowSorter(this.sorter);
        automaticBindClick.bindMenuItemClick(this.rightClickMenu, null, this);
        addActionForKey("ctrl pressed F", new AbstractAction() {
             

            public void actionPerformed(ActionEvent e) {
                DataView.this.ctrlPassF(e);
            }
        });
    }

    public DataView(Vector rowData, Vector columnNames, int imgColumn2, int imgMaxWidth) {
        super(rowData, columnNames);
        columnNames = columnNames == null ? new Vector() : columnNames;
        getModel().setDataVector(rowData == null ? new Vector() : rowData, columnNames);
        this.columnNameVector = columnNames;
        this.imgColumn = imgColumn2;
        if (imgColumn2 >= 0) {
            getColumnModel().getColumn(0).setMaxWidth(imgMaxWidth);
        }
        initJtableConfig();
    }

    public void ctrlPassF(ActionEvent e) {
        Object filterObject = JOptionPane.showInputDialog((Component) null, "input filter", "input filter", 3, (Icon) null, (Object[]) null, this.lastFiter);
        if (filterObject != null) {
            final String fiter = filterObject.toString();
            this.lastFiter = fiter;
            if (fiter.isEmpty()) {
                this.sorter.setRowFilter((RowFilter) null);
            } else {
                this.sorter.setRowFilter(new RowFilter() {
                     

                    public boolean include(Entry entry) {
                        int count = entry.getValueCount();
                        for (int i = 0; i < count; i++) {
                            if (functions.isMatch(entry.getStringValue(i), fiter, false)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        } else {
            Log.log("用户取消选择", new Object[0]);
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
        getActionMap().put(keyStroke.toString(), action);
        getInputMap().put(keyStroke, keyStroke.toString());
    }

    public void addActionForKey(String keyString, Action action) {
        addActionForKeyStroke(KeyStroke.getKeyStroke(keyString), action);
    }

    public void RemoveALL() {
        DefaultTableModel defaultTableModel = getModel();
        while (defaultTableModel.getRowCount() > 0) {
            defaultTableModel.removeRow(0);
        }
        updateUI();
    }

    public TableRowSorter getSorter() {
        return this.sorter;
    }

    public void setSorter(TableRowSorter sorter2) {
        this.sorter = sorter2;
    }

    public Class getColumnClass(int column) {
        return column == this.imgColumn ? Icon.class : Object.class;
    }

    public Vector GetSelectRow() {
        int select_row_id = getSelectedRow();
        if (select_row_id == -1) {
            return null;
        }
        int column_num = getColumnCount();
        Vector vector = new Vector();
        for (int i = 0; i < column_num; i++) {
            vector.add(getValueAt(select_row_id, i));
        }
        return vector;
    }

    public Vector getColumnVector() {
        return this.columnNameVector;
    }

    public String[] GetSelectRow1() {
        int select_row_id = getSelectedRow();
        if (select_row_id == -1) {
            return null;
        }
        int column_num = getColumnCount();
        String[] select_row_columns = new String[column_num];
        for (int i = 0; i < column_num; i++) {
            Object value = getValueAt(select_row_id, i);
            if (value instanceof String) {
                select_row_columns[i] = (String) value;
            } else if (value != null) {
                try {
                    select_row_columns[i] = value.toString();
                } catch (Exception e) {
                    select_row_columns[i] = "null";
                    Log.error(e);
                }
            } else {
                select_row_columns[i] = "null";
            }
        }
        return select_row_columns;
    }

    public DefaultTableModel getModel() {
        return super.dataModel != null ? (DefaultTableModel)super.dataModel : null;
    }

    public synchronized void AddRow(Object object) {
        String field_value;
        Field[] fields = object.getClass().getFields();
        DefaultTableModel tableModel = getModel();
        Vector rowVector = new Vector(tableModel.getColumnCount());
        String[] columns = new String[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            columns[i] = tableModel.getColumnName(i).toUpperCase();
            rowVector.add("NULL");
        }
        for (Field field : fields) {
            String field_name = field.getName();
            int find_id = Arrays.binarySearch(columns, field_name.substring(2).toUpperCase());
            if (field_name.startsWith("s_") && find_id != -1) {
                try {
                    if (field.get(object) instanceof String) {
                        field_value = (String) field.get(object);
                    } else {
                        field_value = "NULL";
                    }
                } catch (Exception e) {
                    field_value = "NULL";
                }
                rowVector.set(find_id, field_value);
            }
        }
        tableModel.addRow(rowVector);
    }

    public synchronized void AddRow(Vector one_row) {
        getModel().addRow(one_row);
    }

    public synchronized void AddRows(Vector rows) {
        getModel().setDataVector(rows, getColumnVector());
    }

    public synchronized void SetRow(int row_id, Object object) {
        String field_value;
        Field[] fields = object.getClass().getFields();
        DefaultTableModel tableModel = getModel();
        Vector rowVector = (Vector) tableModel.getDataVector().get(row_id);
        String[] columns = new String[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            columns[i] = tableModel.getColumnName(i).toUpperCase();
        }
        for (Field field : fields) {
            String field_name = field.getName();
            int find_id = Arrays.binarySearch(columns, field_name.substring(2).toUpperCase());
            if (field_name.startsWith("s_") && find_id != -1) {
                try {
                    if (field.get(object) instanceof String) {
                        field_value = (String) field.get(object);
                    } else {
                        field_value = "NULL";
                    }
                } catch (Exception e) {
                    field_value = "NULL";
                }
                rowVector.set(find_id, field_value);
            }
        }
    }

    public void find(String regxString) {
        TableRowSorter sorter;
        if (!regxString.isEmpty()) {
            sorter = new TableRowSorter((DefaultTableModel) super.getModel());
            this.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter(regxString, new int[0]));
        } else {
            sorter = new TableRowSorter((DefaultTableModel) super.getModel());
            this.setRowSorter(sorter);
            sorter.setRowFilter((RowFilter) null);
        }
    }

    public void setRightClickMenu(JPopupMenu rightClickMenu2) {
        this.rightClickMenu = rightClickMenu2;
        this.rightClickEvent.setRightClickMenu(rightClickMenu2);
    }

    public JTableHeader getTableHeader() {
        JTableHeader tableHeader = super.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        DefaultTableCellRenderer hr = (DefaultTableCellRenderer)tableHeader.getDefaultRenderer();
        hr.setHorizontalAlignment(0);
        return tableHeader;
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
        if (getSelectedColumn() != -1) {
            Object o = getValueAt(getSelectedRow(), getSelectedColumn());
            if (o != null) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection((String) o), (ClipboardOwner) null);
                JOptionPane.showMessageDialog((Component) null, "复制成功", "提示", 1);
                return;
            }
            JOptionPane.showMessageDialog((Component) null, "选中列是空的", "提示", 2);
            return;
        }
        JOptionPane.showMessageDialog((Component) null, "未选中列", "提示", 2);
    }

    private void copyselectedLineMenuItemClick(ActionEvent e) {
        if (getSelectedColumn() != -1) {
            String[] o = GetSelectRow1();
            if (o != null) {
                String value = Arrays.toString(o);
                GetSelectRow1();
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(value), (ClipboardOwner) null);
                JOptionPane.showMessageDialog((Component) null, "复制成功", "提示", 1);
                return;
            }
            JOptionPane.showMessageDialog((Component) null, "选中列是空的", "提示", 2);
            return;
        }
        JOptionPane.showMessageDialog((Component) null, "未选中列", "提示", 2);
    }

    private void exportDataMenuItemClick(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(0);
        chooser.setFileFilter(new FileNameExtensionFilter("*.csv", new String[]{"csv"}));
        chooser.showDialog((Component) null, "选择");
        File selectdFile = chooser.getSelectedFile();
        if (selectdFile != null) {
            String fileString = selectdFile.getAbsolutePath();
            if (!fileString.endsWith(".csv")) {
                fileString = fileString + ".csv";
            }
            if (functions.saveDataViewToCsv(getColumnVector(), getModel().getDataVector(), fileString)) {
                JOptionPane.showMessageDialog((Component) null, "导出成功", "提示", 1);
            } else {
                JOptionPane.showMessageDialog((Component) null, "导出失败", "提示", 1);
            }
        } else {
            Log.log("用户取消选择......", new Object[0]);
        }
    }

     
    public class RightClickEvent extends MouseAdapter {
        private ActionDblClick actionDblClick;
        private final DataView dataView;
        private JPopupMenu rightClickMenu;

        public RightClickEvent(JPopupMenu rightClickMenu2, DataView jtable) {
            this.rightClickMenu = rightClickMenu2;
            this.dataView = jtable;
        }

        public void setRightClickMenu(JPopupMenu rightClickMenu2) {
            this.rightClickMenu = rightClickMenu2;
        }

        public void setActionListener(ActionDblClick event) {
            this.actionDblClick = event;
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            int i;
            if (mouseEvent.getButton() == 3) {
                if (this.rightClickMenu != null && (i = this.dataView.rowAtPoint(mouseEvent.getPoint())) != -1) {
                    this.rightClickMenu.show(this.dataView, mouseEvent.getX(), mouseEvent.getY());
                    this.dataView.setRowSelectionInterval(i, i);
                }
            } else if (mouseEvent.getClickCount() == 2 && this.actionDblClick != null) {
                this.actionDblClick.dblClick(mouseEvent);
            }
        }
    }
}
