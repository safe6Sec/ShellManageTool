package core.ui.component;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import javassist.compiler.TokenId;
import javax.swing.JPanel;

public class SimplePanel extends JPanel {
    private int currentComponentIndex = 0;
    private int setup = 80;

    public SimplePanel() {
        super(new GridBagLayout());
    }

    public SimplePanel(LayoutManager paramLayoutManager) {
        super(paramLayoutManager);
    }

    public synchronized void addComponent(int setup2, Component... component) {
        for (int i = 0; i < component.length; i++) {
            add(component[i], new GBC(1, this.currentComponentIndex).setInsets(5, (-setup2) * i, 0, 0));
        }
        this.currentComponentIndex++;
    }

    public synchronized void addLComponent(int setup2, Component... component) {
        for (int i = 0; i < component.length; i++) {
            add(component[i], new GBC(0, this.currentComponentIndex).setInsets(0, i * setup2, 0, TokenId.IF));
        }
        this.currentComponentIndex++;
    }

    public void addX(Component... component) {
        addComponent(this.setup, component);
    }

    public int getSetup() {
        return this.setup;
    }

    public void setSetup(int setup2) {
        this.setup = setup2;
    }
}
