package core.ui.component.dialog;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import util.Log;
import util.functions;

public class HttpProgressBar extends JFrame {
    private static final String CURRENT_VALUE_FORMAT = "已完成  %s Mb";
    private static final String MAX_VALUE_FORMAT = "共  %s Mb";
    private final JLabel currentValueLabel = new JLabel();
    private boolean isClose;
    private final JLabel maxValueLabel = new JLabel();
    private final JPanel panel = new JPanel();
    private final JProgressBar progressBar;

    public HttpProgressBar(String title, int MaxValue) {
        this.progressBar = new JProgressBar(0, 0, MaxValue);
        this.panel.add(this.progressBar);
        this.panel.add(this.maxValueLabel);
        this.panel.add(this.currentValueLabel);
        this.maxValueLabel.setText(String.format(MAX_VALUE_FORMAT, String.format("%.4f", Float.valueOf(((float) MaxValue) / Float.valueOf(1048576.0f).floatValue()))));
        this.currentValueLabel.setText(String.format(CURRENT_VALUE_FORMAT, 0));
        add(this.panel);
        setTitle(title);
        this.progressBar.setStringPainted(true);
        setDefaultCloseOperation(2);
        setLocationRelativeTo(null);
        functions.setWindowSize(this, 430, 90);
        this.progressBar.updateUI();
        setVisible(true);
    }

    public void setValue(int value) {
        this.progressBar.setValue(value);
        this.currentValueLabel.setText(String.format(CURRENT_VALUE_FORMAT, String.format("%.4f", Float.valueOf(((float) this.progressBar.getValue()) / Float.valueOf(1048576.0f).floatValue()))));
        Log.log(this.maxValueLabel.getText() + "\t" + this.currentValueLabel.getText(), new Object[0]);
        if (this.progressBar.getMaximum() <= this.progressBar.getValue()) {
            close();
        }
    }

    public boolean isClose() {
        return this.isClose;
    }

    public void close() {
        this.isClose = true;
        dispose();
    }

    public void dispose() {
        this.isClose = true;
        HttpProgressBar.super.dispose();
    }
}
