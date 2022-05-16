package core;

import core.annotation.I18NAction;
import core.annotation.NoI18N;
import java.awt.Window;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.MenuElement;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import util.functions;

public class EasyI18N {
   public static final String SETING_KETY = "language";
   private static final HashMap<Class<?>, Method> actionMap = new HashMap();
   private static final Class[] parameterTypes = new Class[]{Object.class, Field.class};
   private static final Locale language = new Locale(Db.getSetingValue("language", "zh".equalsIgnoreCase(Locale.getDefault().getLanguage()) ? "zh" : "en"));
   private static final ResourceBundle bundle;

   public static void installObject(Object obj) {
      try {
         for(Class objClass = obj.getClass(); objClass != null && (!objClass.getName().startsWith("java") || !objClass.getName().startsWith("sun")); objClass = objClass.getSuperclass()) {
            try {
               Field[] fields = objClass.getDeclaredFields();
               Method actionMethod = null;
               Field[] var4 = fields;
               int var5 = fields.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  Field field = var4[var6];
                  if (field.getAnnotation(NoI18N.class) == null) {
                     field.setAccessible(true);
                     actionMethod = findAction(field.getType());
                     if (actionMethod != null) {
                        actionMethod.setAccessible(true);
                        actionMethod.invoke((Object)null, obj, field);
                     }
                  }
               }

               if (objClass.getAnnotation(NoI18N.class) == null) {
                  actionMethod = findAction(objClass);
                  if (actionMethod != null) {
                     actionMethod.setAccessible(true);
                     actionMethod.invoke((Object)null, obj, null);
                  }
               }
            } catch (Exception var8) {
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }

   public static String getI18nString(String format, Object... args) {
      return String.format(getI18nString(format), args);
   }

   public static String getI18nString(String key) {
      if ("zh".equals(language.getLanguage())) {
         return key;
      } else if (key != null) {
         String value = null;

         try {
            value = bundle.getString(key.trim().replace("\r\n", "\\r\\n").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t"));
            if (value != null) {
               value = value.replace("\\r\\n", "\r\n").replace("\\r", "\r").replace("\\n", "\n").replace("\\t", "\t");
            }
         } catch (Exception var3) {
         }

         return value == null ? key : value;
      } else {
         return null;
      }
   }

   private static Method findAction(Class fieldType) {
      Method action = findAction(fieldType, true);
      if (action == null) {
         action = findAction(fieldType, false);
      }

      return action;
   }

   private static Method findAction(Class fieldType, boolean comparisonThis) {
      Iterator keys;
      Class clazz;
      if (comparisonThis) {
         keys = actionMap.keySet().iterator();

         while(keys.hasNext()) {
            clazz = (Class)keys.next();
            if (fieldType.equals(clazz)) {
               return (Method)actionMap.get(clazz);
            }
         }
      } else {
         keys = actionMap.keySet().iterator();

         while(keys.hasNext()) {
            clazz = (Class)keys.next();
            if (clazz.isAssignableFrom(fieldType)) {
               return (Method)actionMap.get(clazz);
            }
         }
      }

      return null;
   }

   @I18NAction(
      targetClass = JLabel.class
   )
   public static void installJLabel(Object obj, Field targetField) throws Throwable {
      JLabel label = (JLabel)targetField.get(obj);
      if (label != null) {
         label.setText(getI18nString(label.getText()));
      } else {
         targetField.set(obj, new JLabel(targetField.getName()));
      }

   }

   @I18NAction(
      targetClass = JMenu.class
   )
   public static void installJMenu(Object obj, Field targetField) throws Throwable {
      JMenu menu = (JMenu)targetField.get(obj);
      menu.setText(getI18nString(menu.getText()));
      int itemCount = menu.getItemCount();

      for(int i = 0; i < itemCount; ++i) {
         JMenuItem menuItem = menu.getItem(i);
         menuItem.setText(getI18nString(menuItem.getText()));
      }

   }

   @I18NAction(
      targetClass = JTabbedPane.class
   )
   public static void installJTabbedPane(Object obj, Field targetField) throws Throwable {
      JTabbedPane tabbedPane = (JTabbedPane)targetField.get(obj);
      int itemCount = tabbedPane.getTabCount();

      for(int i = 0; i < itemCount; ++i) {
         String title = tabbedPane.getTitleAt(i);
         if (title != null) {
            tabbedPane.setTitleAt(i, getI18nString(title));
         }
      }

   }

   @I18NAction(
      targetClass = JPopupMenu.class
   )
   public static void installJPopupMenu(Object obj, Field targetField) throws Throwable {
      JPopupMenu popupMenu = (JPopupMenu)targetField.get(obj);
      MenuElement[] menuElements = popupMenu.getSubElements();
      MenuElement[] var4 = menuElements;
      int var5 = menuElements.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         MenuElement menuElement = var4[var6];
         if (menuElement instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem)menuElement;
            menuItem.setText(getI18nString(menuItem.getText()));
         }
      }

   }

   @I18NAction(
      targetClass = JButton.class
   )
   public static void installJButton(Object obj, Field targetField) throws Throwable {
      JButton button = (JButton)targetField.get(obj);
      if (button != null) {
         button.setText(getI18nString(button.getText()));
      }

   }

   @I18NAction(
      targetClass = JCheckBox.class
   )
   public static void installJCheckBox(Object obj, Field targetField) throws Throwable {
      JCheckBox checkBox = (JCheckBox)targetField.get(obj);
      if (checkBox != null) {
         checkBox.setText(getI18nString(checkBox.getText()));
      }

   }

   @I18NAction(
      targetClass = JComponent.class
   )
   public static void installJComponent(Object obj, Field targetField) throws Throwable {
      JComponent component = null;
      if (targetField == null) {
         component = (JComponent)obj;
      } else {
         component = (JComponent)targetField.get(obj);
      }

      if (component != null) {
         Border border = component.getBorder();
         if (border instanceof TitledBorder) {
            TitledBorder titledBorder = (TitledBorder)border;
            titledBorder.setTitle(getI18nString(titledBorder.getTitle()));
         }

         Method getTitleMethod = functions.getMethodByClass(component.getClass(), "getTitle", (Class[])null);
         Method setTitleMethod = functions.getMethodByClass(component.getClass(), "setTitle", String.class);
         if (getTitleMethod != null && setTitleMethod != null) {
            getTitleMethod.setAccessible(true);
            setTitleMethod.setAccessible(true);
            String oldTitle = (String)getTitleMethod.invoke(obj, (Object[])null);
            if (oldTitle != null) {
               setTitleMethod.invoke(obj, getI18nString(oldTitle));
            }
         }

      }
   }

   @I18NAction(
      targetClass = Window.class
   )
   public static void installWindow(Object obj, Field targetField) {
      try {
         Window component = null;
         if (targetField == null) {
            component = (Window)obj;
         } else {
            component = (Window)targetField.get(obj);
         }

         Method getTitleMethod = functions.getMethodByClass(component.getClass(), "getTitle", (Class[])null);
         Method setTitleMethod = functions.getMethodByClass(component.getClass(), "setTitle", String.class);
         if (getTitleMethod != null && setTitleMethod != null) {
            getTitleMethod.setAccessible(true);
            setTitleMethod.setAccessible(true);
            String oldTitle = (String)getTitleMethod.invoke(obj, (Object[])null);
            if (oldTitle != null) {
               setTitleMethod.invoke(obj, getI18nString(oldTitle));
            }
         }
      } catch (Exception var6) {
      }

   }

   static {
      bundle = ResourceBundle.getBundle("godzilla", language);

      try {
         Method[] methods = EasyI18N.class.getDeclaredMethods();
         Method[] var1 = methods;
         int var2 = methods.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Method method = var1[var3];
            if (Modifier.isStatic(method.getModifiers()) && Arrays.equals(parameterTypes, method.getParameterTypes())) {
               I18NAction action = (I18NAction)method.getDeclaredAnnotation(I18NAction.class);
               if (action != null) {
                  actionMap.put(action.targetClass(), method);
               }
            }
         }
      } catch (Exception var6) {
      }

   }
}
