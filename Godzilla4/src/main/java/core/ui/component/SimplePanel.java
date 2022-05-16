package core.ui.component;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
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

   public synchronized void addComponent(int setup, Component... component) {
      for(int i = 0; i < component.length; ++i) {
         GBC gbc = (new GBC(1, this.currentComponentIndex)).setInsets(5, i * -setup, 0, 0);
         this.add(component[i], gbc);
      }

      ++this.currentComponentIndex;
   }

   public synchronized void addLComponent(int setup, Component... component) {
      for(int i = 0; i < component.length; ++i) {
         GBC gbc = (new GBC(0, this.currentComponentIndex)).setInsets(0, i * setup, 0, 320);
         this.add(component[i], gbc);
      }

      ++this.currentComponentIndex;
   }

   public synchronized void addLRComponent(Component left, Component right) {
      GBC gbcLeft = (new GBC(0, this.currentComponentIndex)).setInsets(5, -40, 0, 0);
      GBC gbcRight = (new GBC(1, this.currentComponentIndex, 3, 1)).setInsets(5, 20, 0, 0);
      this.add(left, gbcLeft);
      this.add(right, gbcRight);
   }

   public void addX(Component... component) {
      this.addComponent(this.setup, component);
   }

   public int getSetup() {
      return this.setup;
   }

   public void setSetup(int setup) {
      this.setup = setup;
   }
}
