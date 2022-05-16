package util;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

public class RightClickMenu extends MouseAdapter {
   private final JPopupMenu popupMenu;

   public RightClickMenu(JPopupMenu popupMenu) {
      this.popupMenu = popupMenu;
   }

   public void mouseClicked(MouseEvent mouseEvent) {
      super.mouseClicked(mouseEvent);
      if (mouseEvent.getButton() == 3) {
         Component c = mouseEvent.getComponent();
         if (this.popupMenu != null && c != null) {
            this.popupMenu.show(c, mouseEvent.getX(), mouseEvent.getY());
         }
      }

   }
}
