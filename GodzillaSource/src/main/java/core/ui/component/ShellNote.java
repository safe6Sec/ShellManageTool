package core.ui.component;

import core.Db;
import core.shell.ShellEntity;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import util.Log;
import util.functions;

public class ShellNote extends JPanel {
    private ShellEntity shellEntity;
    private String noteData;
    private String shellId;
    private String lastNoteMd5;
    private RTextArea textArea;
    private boolean state;

    public ShellNote(ShellEntity entity) {
        this.shellEntity = entity;
        this.shellId = this.shellEntity.getId();
        super.setLayout(new BorderLayout(1, 1));
        String noteData = Db.getShellNote(this.shellId);
        this.lastNoteMd5 = functions.md5(noteData);
        this.textArea = new RTextArea();
        this.textArea.setText(noteData);
        this.state = true;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while(ShellNote.this.state) {
                    try {
                        Thread.sleep(10000L);
                        ShellNote.this.updateDbNote();
                    } catch (InterruptedException var2) {
                        Log.error(var2);
                    }
                }

            }
        });
        thread.start();
        super.add(new JScrollPane(this.textArea));
    }

    public void updateDbNote() {
        String noteData2 = this.textArea.getText();
        if (!this.lastNoteMd5.equals(functions.md5(noteData2))) {
            Db.updateShellNote(this.shellId, noteData2);
        }
    }
}
