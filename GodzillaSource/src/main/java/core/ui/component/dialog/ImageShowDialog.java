package core.ui.component.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import util.functions;

public class ImageShowDialog extends JDialog {
    private JLabel imageLabel;
    private JPanel panel = new JPanel(new BorderLayout());

    private ImageShowDialog(Frame owner, ImageIcon imageIcon, String title, int width, int height) {
        super(owner, title, true);
        this.imageLabel = new JLabel(imageIcon);
        this.panel.add(this.imageLabel);
        add(this.panel);
        functions.setWindowSize(this, width, height);
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    public static void showImageDiaolog(Frame owner, ImageIcon imageIcon, String title, int width, int height) {
        int width2 = width + 50;
        int height2 = height + 50;
        if (title == null || title.trim().length() < 1) {
            title = String.format("image info Width:%s Height:%s", Integer.valueOf(imageIcon.getIconWidth()), Integer.valueOf(imageIcon.getIconHeight()));
        }
        new ImageShowDialog(owner, imageIcon, title, width2, height2);
    }

    public static void showImageDiaolog(Frame owner, ImageIcon imageIcon, String title) {
        showImageDiaolog(owner, imageIcon, title, imageIcon.getIconWidth(), imageIcon.getIconHeight());
    }

    public static void showImageDiaolog(ImageIcon imageIcon, String title) {
        showImageDiaolog(null, imageIcon, title);
    }

    public static void showImageDiaolog(Frame owner, ImageIcon imageIcon) {
        showImageDiaolog(owner, imageIcon, null);
    }

    public static void showImageDiaolog(ImageIcon imageIcon) {
        showImageDiaolog((Frame) null, imageIcon);
    }
}
