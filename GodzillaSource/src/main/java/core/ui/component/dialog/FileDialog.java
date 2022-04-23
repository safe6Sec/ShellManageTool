package core.ui.component.dialog;

import core.ui.component.GBC;
import core.ui.component.model.FileOpertionInfo;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import javassist.bytecode.Opcode;
import javassist.compiler.TokenId;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import util.automaticBindClick;
import util.functions;

public class FileDialog extends JDialog {
    Dimension TextFieldDim = new Dimension((int) TokenId.BadToken, 23);
    private JButton cancelButton = new JButton("cancel");
    private JLabel destFileLabel = new JLabel("destFile");
    private JTextField destFileTextField = new JTextField("destText", 30);
    private JButton destSelectdFileButton = new JButton("select File");
    private FileOpertionInfo fileOpertionInfo = new FileOpertionInfo();
    private JButton okButton = new JButton("ok");
    private JLabel srcFileLabel = new JLabel("srcFile");
    private JTextField srcFileTextField = new JTextField("srcFileText", 30);
    private JButton srcSelectdFileButton = new JButton("select File");
    private boolean state;

    private FileDialog(Frame frame, String tipString, String srcFileString, String destString) {
        super(frame, tipString, true);
        Dimension TextFieldDim2 = new Dimension((int) Opcode.GOTO_W, 23);
        GBC gbcLSrcFile = new GBC(0, 0).setInsets(5, -40, 0, 0);
        GBC gbcSrcFile = new GBC(1, 0, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcSrcSelectdFie = new GBC(4, 0, 7, 1).setInsets(5, 50, 0, 10);
        GBC gbcDestSelectdFie = new GBC(4, 1, 7, 1).setInsets(5, 50, 0, 10);
        GBC gbcLDestFile = new GBC(0, 1).setInsets(5, -40, 0, 0);
        GBC gbcDestFile = new GBC(1, 1, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcOkButton = new GBC(0, 2, 2, 1).setInsets(5, 20, 0, 0);
        GBC gbcCancelButton = new GBC(2, 2, 1, 1).setInsets(5, 20, 0, 0);
        this.srcFileTextField.setPreferredSize(TextFieldDim2);
        this.destFileTextField.setPreferredSize(TextFieldDim2);
        setLayout(new GridBagLayout());
        add(this.srcFileLabel, gbcLSrcFile);
        add(this.srcFileTextField, gbcSrcFile);
        add(this.srcSelectdFileButton, gbcSrcSelectdFie);
        add(this.destSelectdFileButton, gbcDestSelectdFie);
        add(this.destFileLabel, gbcLDestFile);
        add(this.destFileTextField, gbcDestFile);
        add(this.okButton, gbcOkButton);
        add(this.cancelButton, gbcCancelButton);
        automaticBindClick.bindJButtonClick(this, this);
        addWindowListener(new WindowListener() {
             

            public void windowOpened(WindowEvent paramWindowEvent) {
            }

            public void windowIconified(WindowEvent paramWindowEvent) {
            }

            public void windowDeiconified(WindowEvent paramWindowEvent) {
            }

            public void windowDeactivated(WindowEvent paramWindowEvent) {
            }

            public void windowClosing(WindowEvent paramWindowEvent) {
                FileDialog.this.cancelButtonClick(null);
            }

            public void windowClosed(WindowEvent paramWindowEvent) {
            }

            public void windowActivated(WindowEvent paramWindowEvent) {
            }
        });
        this.srcFileTextField.setText(srcFileString);
        this.destFileTextField.setText(destString);
        functions.setWindowSize(this, 650, Opcode.GETFIELD);
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(2);
        setVisible(true);
    }

    public FileOpertionInfo getResult() {
        return this.fileOpertionInfo;
    }

    private void okButtonClick(ActionEvent actionEvent) {
        this.fileOpertionInfo.setOpertionStatus(true);
        changeFileInfo();
    }

     
     
    private void cancelButtonClick(ActionEvent actionEvent) {
        this.fileOpertionInfo.setOpertionStatus(false);
        changeFileInfo();
    }

    private void changeFileInfo() {
        this.fileOpertionInfo.setSrcFileName(this.srcFileTextField.getText());
        this.fileOpertionInfo.setDestFileName(this.destFileTextField.getText());
        this.state = true;
        dispose();
    }

    private void srcSelectdFileButtonClick(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(0);
        chooser.showDialog(new JLabel(), "选择");
        File selectdFile = chooser.getSelectedFile();
        if (selectdFile != null) {
            this.srcFileTextField.setText(selectdFile.getAbsolutePath());
        }
    }

    private void destSelectdFileButtonClick(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(0);
        chooser.showDialog(new JLabel(), "选择");
        File selectdFile = chooser.getSelectedFile();
        if (selectdFile != null) {
            this.destFileTextField.setText(selectdFile.getAbsolutePath());
        }
    }

    public static FileOpertionInfo showFileOpertion(Frame frame, String title, String srcFileString, String destString) {
        return new FileDialog(frame, title, srcFileString, destString).getResult();
    }
}
