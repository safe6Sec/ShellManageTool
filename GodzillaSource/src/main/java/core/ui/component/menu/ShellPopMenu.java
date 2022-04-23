package core.ui.component.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class ShellPopMenu {
    private JTextPane c;
    Clipboard clipboard;
    Transferable contents;
    private JMenuItem copy = new JMenuItem("复制");
    DataFlavor flavor;
    private JPanel p;
    private JMenuItem paste = new JMenuItem("粘贴");
    private Document shell_doc;
    private JPopupMenu shellmenu = new JPopupMenu();
    Transferable tText;

    public ShellPopMenu(JPanel panel, JTextPane console) {
        this.p = panel;
        this.c = console;
        this.shell_doc = console.getDocument();
        this.shellmenu.add(this.copy);
        this.shellmenu.add(this.paste);
        this.p.add(this.shellmenu);
        MenuAction action = new MenuAction();
        this.copy.addActionListener(action);
        this.paste.addActionListener(action);
        console.addMouseListener(new MouseL());
    }

    class MenuAction implements ActionListener {
        MenuAction() {
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == ShellPopMenu.this.copy) {
                String k = ShellPopMenu.this.c.getSelectedText();
                ShellPopMenu.this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                ShellPopMenu.this.tText = new StringSelection(k);
                ShellPopMenu.this.clipboard.setContents(ShellPopMenu.this.tText, (ClipboardOwner) null);
            } else if (e.getSource() == ShellPopMenu.this.paste) {
                ShellPopMenu.this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable clipT = ShellPopMenu.this.clipboard.getContents((Object) null);
                if (clipT != null && clipT.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        try {
                            ShellPopMenu.this.shell_doc.insertString(ShellPopMenu.this.shell_doc.getLength(), (String) clipT.getTransferData(DataFlavor.stringFlavor), (AttributeSet) null);
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                        }
                    } catch (UnsupportedFlavorException | IOException e12) {
                        e12.printStackTrace();
                    }
                }
            }
        }
    }

    class MouseL implements MouseListener {
        MouseL() {
        }

        public void mouseClicked(MouseEvent e) {
            if (e.isMetaDown()) {
                ShellPopMenu.this.shellmenu.show(ShellPopMenu.this.c, e.getX(), e.getY());
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }
}
