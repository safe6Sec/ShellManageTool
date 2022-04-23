package core.ui.component;

import core.imp.Payload;
import core.shell.ShellEntity;
import core.ui.component.menu.ShellPopMenu;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import util.Log;

public class ShellExecCommandPanel extends JPanel {
    private static final long serialVersionUID = 1;
    private JToolBar bar;
    private int command_start;
    private int command_stop;
    private JTextPane console;
    private JScrollPane console_scroll;
    private String currentDir;
    private String currentUser;
    private String fileRoot;
    private ArrayList<String> last_commands = new ArrayList<>();
    private int num = 1;
    private String osInfo;
    private Payload shell;
    private ShellPopMenu shellPopMenu;
    private Document shell_doc;
    private JLabel status;

    public ShellExecCommandPanel(ShellEntity shellEntity) {
        this.shell = shellEntity.getPayloadModel();
        this.bar = new JToolBar();
        this.status = new JLabel("完成");
        this.bar.setFloatable(false);
        this.console = new JTextPane();
        this.console_scroll = new JScrollPane(this.console);
        this.shell_doc = this.console.getDocument();
        this.shellPopMenu = new ShellPopMenu(this, this.console);
        this.currentDir = this.shell.currentDir();
        this.currentUser = this.shell.currentUserName();
        this.fileRoot = Arrays.toString(shellEntity.getPayloadModel().listFileRoot());
        this.osInfo = this.shell.getOsInfo();
        this.status.setText("正在连接...请稍等");
        new Thread(new Runnable() {
             

            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                     

                    public void run() {
                        try {
                            ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.shell_doc.getLength(), String.format("currentDir:%s\nfileRoot:%s\ncurrentUser:%s\nosInfo:%s\n", ShellExecCommandPanel.this.currentDir, ShellExecCommandPanel.this.fileRoot, ShellExecCommandPanel.this.currentUser, ShellExecCommandPanel.this.osInfo), (AttributeSet) null);
                            ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.shell_doc.getLength(), "\n" + ShellExecCommandPanel.this.currentDir + " >", (AttributeSet) null);
                        } catch (BadLocationException e) {
                            Log.error((Exception) e);
                        }
                        ShellExecCommandPanel.this.command_start = ShellExecCommandPanel.this.shell_doc.getLength();
                        ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength());
                        ShellExecCommandPanel.this.status.setText("完成");
                    }
                });
            }
        }).start();
        setLayout(new GridBagLayout());
        GBC gbcinfo = new GBC(0, 0, 6, 1).setFill(2).setWeight(100.0d, 0.0d);
        GBC gbcconsole = new GBC(0, 1, 6, 1).setFill(1).setWeight(0.0d, 10.0d);
        GBC gbcbar = new GBC(0, 2, 6, 1).setFill(2).setWeight(100.0d, 0.0d);
        addFocusListener(new textareaFocus());
        this.console.addKeyListener(new textareaKey());
        this.bar.add(this.status);
        add(this.bar, gbcinfo);
        add(this.console_scroll, gbcconsole);
        add(this.bar, gbcbar);
        this.console.setCaretPosition(this.shell_doc.getLength());
        Color bgColor = Color.BLACK;
        UIDefaults defaults = new UIDefaults();
        defaults.put("TextPane[Enabled].backgroundPainter", bgColor);
        this.console.putClientProperty("Nimbus.Overrides", defaults);
        this.console.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
        this.console.setBackground(bgColor);
        this.console.setForeground(Color.green);
        this.console.setBackground(Color.black);
        this.console.setCaretColor(Color.white);
        this.command_start = this.shell_doc.getLength();
    }

    private class textareaFocus extends FocusAdapter {
        private textareaFocus() {
        }

        public void focusGained(FocusEvent e) {
            ShellExecCommandPanel.this.console.requestFocus();
            ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength());
        }
    }

    private class textareaKey extends KeyAdapter {
        private textareaKey() {
        }

        public void keyPressed(KeyEvent arg0) {
            if (ShellExecCommandPanel.this.shell_doc.getLength() <= ShellExecCommandPanel.this.command_start && !arg0.isControlDown() && arg0.getKeyCode() == 8) {
                try {
                    ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.console.getCaretPosition(), ShellExecCommandPanel.this.shell_doc.getText(ShellExecCommandPanel.this.console.getCaretPosition() - 1, 1), (AttributeSet) null);
                } catch (Exception e) {
                }
            }
            if ((ShellExecCommandPanel.this.console.getCaretPosition() < ShellExecCommandPanel.this.command_start || ShellExecCommandPanel.this.console.getSelectionStart() < ShellExecCommandPanel.this.command_start || ShellExecCommandPanel.this.console.getSelectionEnd() < ShellExecCommandPanel.this.command_start) && !arg0.isControlDown()) {
                ShellExecCommandPanel.this.console.setEditable(false);
                ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength());
            } else if (!arg0.isControlDown() || ShellExecCommandPanel.this.console.getCaretPosition() >= ShellExecCommandPanel.this.command_start) {
                ShellExecCommandPanel.this.console.setEditable(true);
            } else {
                ShellExecCommandPanel.this.console.setEditable(false);
            }
            if (arg0.getKeyCode() == 10) {
                ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength());
            }
        }

        public synchronized void keyReleased(KeyEvent arg0) {
            ShellExecCommandPanel.this.command_stop = ShellExecCommandPanel.this.shell_doc.getLength();
            if (arg0.getKeyCode() == 10) {
                try {
                    String tmp_cmd = ShellExecCommandPanel.this.shell_doc.getText(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.command_stop - ShellExecCommandPanel.this.command_start).replace("\n", "").replace("\r", "");
                    if (tmp_cmd.equals("cls") || tmp_cmd.equals("clear")) {
                        ShellExecCommandPanel.this.shell_doc.remove(0, ShellExecCommandPanel.this.shell_doc.getLength());
                        ShellExecCommandPanel.this.shell_doc.insertString(0, "\n" + ShellExecCommandPanel.this.currentDir + " >", (AttributeSet) null);
                        ShellExecCommandPanel.this.command_start = ShellExecCommandPanel.this.shell_doc.getLength();
                    } else {
                        ShellExecCommandPanel.this.status.setText("正在执行...请稍等");
                        try {
                            ShellExecCommandPanel.this.execute(ShellExecCommandPanel.this.shell_doc.getText(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.command_stop - ShellExecCommandPanel.this.command_start));
                        } catch (Exception e) {
                            ShellExecCommandPanel.this.status.setText("执行失败");
                            ShellExecCommandPanel.this.console.setEditable(true);
                        }
                    }
                    if (tmp_cmd.trim().length() > 0) {
                        ShellExecCommandPanel.this.last_commands.add(tmp_cmd);
                    }
                    ShellExecCommandPanel.this.num = ShellExecCommandPanel.this.last_commands.size();
                } catch (BadLocationException e2) {
                    e2.printStackTrace();
                }
            }
            if (arg0.getKeyCode() == 38) {
                ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.command_start);
                try {
                    ShellExecCommandPanel.this.shell_doc.remove(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.shell_doc.getLength() - ShellExecCommandPanel.this.command_start);
                    ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.key_up_action(), (AttributeSet) null);
                } catch (BadLocationException e3) {
                    e3.printStackTrace();
                }
            }
            if (arg0.getKeyCode() == 40) {
                ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.command_start);
                try {
                    ShellExecCommandPanel.this.shell_doc.remove(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.shell_doc.getLength() - ShellExecCommandPanel.this.command_start);
                    ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.key_down_action(), (AttributeSet) null);
                } catch (BadLocationException e4) {
                    e4.printStackTrace();
                }
            }
        }
    }

    public void execute(String command) {
        String result;
        try {
            if (command.trim().length() > 0) {
                result = "\n" + this.shell.execCommand(command);
            } else {
                result = "\n" + "NULL";
            }
            this.shell_doc.insertString(this.shell_doc.getLength(), result, (AttributeSet) null);
            this.shell_doc.insertString(this.shell_doc.getLength(), "\n" + this.currentDir + " >", (AttributeSet) null);
            this.command_start = this.shell_doc.getLength();
            this.console.setCaretPosition(this.shell_doc.getLength());
            this.status.setText("完成");
        } catch (Exception e) {
            try {
                this.shell_doc.insertString(this.shell_doc.getLength(), "\nNull", (AttributeSet) null);
                this.shell_doc.insertString(this.shell_doc.getLength(), "\n" + this.currentDir + " >", (AttributeSet) null);
                this.command_start = this.shell_doc.getLength();
                this.console.setCaretPosition(this.shell_doc.getLength());
            } catch (Exception e2) {
                Log.error(e2);
            }
        }
    }

    public String key_up_action() {
        this.num--;
        if (this.num < 0 || this.last_commands.isEmpty()) {
            return "";
        }
        return this.last_commands.get(this.num).replace("\n", "").replace("\r", "");
    }

    public String key_down_action() {
        this.num++;
        if (this.num < this.last_commands.size() && this.num >= 0) {
            return this.last_commands.get(this.num).replace("\n", "").replace("\r", "");
        }
        if (this.num < 0) {
            this.num = 0;
            return "";
        }
        this.num = this.last_commands.size();
        return "";
    }

    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            str = str + Integer.toHexString(s.charAt(i));
        }
        return str;
    }
}
