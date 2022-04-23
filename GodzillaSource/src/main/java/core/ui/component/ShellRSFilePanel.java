package core.ui.component;

import core.ApplicationContext;
import core.imp.Payload;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class ShellRSFilePanel extends JPanel {
    private JButton backButton = new JButton("返回");
    private CardLayout cardLayout;
    private String containerName;
    private JComboBox<String> encodingComboBox = new JComboBox<>(ApplicationContext.getAllEncodingTypes());
    private String encodingTypeString;
    private byte[] fileData;
    private RTextArea fileDataTextArea = new RTextArea();
    private JPanel parentPanel;
    private Payload payload;
    private JTextField readFileTextField = new JTextField(30);
    private JButton refreshButton = new JButton("刷新");
    private JButton saveButton = new JButton("保存");
    private JScrollPane scrollPane = new JScrollPane(this.fileDataTextArea);
    private JSplitPane splitPane = new JSplitPane();
    private JPanel topPanel = new JPanel();

    public ShellRSFilePanel(Payload payload2, JPanel parentPanel2, String containerName2) {
        super(new BorderLayout());
        this.parentPanel = parentPanel2;
        this.cardLayout = (CardLayout)((CardLayout)parentPanel.getLayout());
        this.containerName = containerName2;
        this.payload = payload2;
        this.topPanel.add(this.readFileTextField);
        this.topPanel.add(this.encodingComboBox);
        this.topPanel.add(this.saveButton);
        this.topPanel.add(this.refreshButton);
        this.topPanel.add(this.backButton);
        this.splitPane.setOrientation(0);
        this.splitPane.setTopComponent(this.topPanel);
        this.splitPane.setBottomComponent(this.scrollPane);
        this.encodingComboBox.addActionListener(new ActionListener() {
             

            public void actionPerformed(ActionEvent paramActionEvent) {
                if (ShellRSFilePanel.this.fileData != null) {
                    ShellRSFilePanel.this.encodingTypeString = (String) ShellRSFilePanel.this.encodingComboBox.getSelectedItem();
                    ShellRSFilePanel.this.refreshData();
                }
            }
        });
        automaticBindClick.bindJButtonClick(this, this);
        this.encodingTypeString = (String) this.encodingComboBox.getSelectedItem();
        add(this.splitPane);
    }

    public void rsFile(String file) {
        this.readFileTextField.setText(file);
        this.fileData = this.payload.downloadFile(file);
        refreshData();
    }

     
     
    private void refreshData() {
        try {
            this.fileDataTextArea.setText(new String(this.fileData, this.encodingTypeString));
        } catch (Exception e) {
            this.fileDataTextArea.setText(new String(this.fileData));
            Log.error(e);
        }
    }

    public void saveButtonClick(ActionEvent e) {
        if (this.payload.uploadFile(this.readFileTextField.getText(), functions.stringToByteArray(this.fileDataTextArea.getText(), this.encodingTypeString))) {
            JOptionPane.showMessageDialog((Component) null, "保存成功", "提示", 1);
        } else {
            JOptionPane.showMessageDialog((Component) null, "保存失败", "提示", 2);
        }
    }

    public void refreshButtonClick(ActionEvent e) {
        rsFile(this.readFileTextField.getText());
    }

    public void backButtonClick(ActionEvent e) {
        this.fileData = null;
        this.fileDataTextArea.setText("");
        this.readFileTextField.setText("");
        this.cardLayout.show(this.parentPanel, this.containerName);
    }
}
