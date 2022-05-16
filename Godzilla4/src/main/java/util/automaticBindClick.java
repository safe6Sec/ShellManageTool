package util;

import core.ui.component.annotation.ButtonToMenuItem;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

public class automaticBindClick {
   public static void bindButtonClick(final Object fieldClass, Object eventClass) {
      try {
         Field[] fields = fieldClass.getClass().getDeclaredFields();
         Field[] var3 = fields;
         int var4 = fields.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            if (field.getType().isAssignableFrom(Button.class)) {
               field.setAccessible(true);
               Button fieldValue = (Button)field.get(fieldClass);
               String fieldName = field.getName();
               if (fieldValue != null) {
                  try {
                     final Method method = eventClass.getClass().getDeclaredMethod(fieldName + "Click", ActionEvent.class);
                     method.setAccessible(true);
                     if (method != null) {
                        fieldValue.addActionListener(new ActionListener() {
                           public void actionPerformed(ActionEvent e) {
                              try {
                                 method.invoke(fieldClass, e);
                              } catch (Exception var3) {
                                 Log.error((Throwable)var3);
                              }

                           }
                        });
                     }
                  } catch (NoSuchMethodException var10) {
                     Log.error(fieldName + "Click  未实现");
                  }
               }
            }
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   public static void bindJButtonClick(Class fieldClass, Object fieldObject, Class eventClass, final Object eventObject) {
      try {
         Field[] fields = fieldClass.getDeclaredFields();
         Field[] var5 = fields;
         int var6 = fields.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Field field = var5[var7];
            if (field.getType().isAssignableFrom(JButton.class)) {
               field.setAccessible(true);
               JButton fieldValue = (JButton)field.get(fieldObject);
               String fieldName = field.getName();
               if (fieldValue != null) {
                  try {
                     final Method method = eventClass.getDeclaredMethod(fieldName + "Click", ActionEvent.class);
                     method.setAccessible(true);
                     if (method != null) {
                        fieldValue.addActionListener(new ActionListener() {
                           public void actionPerformed(ActionEvent e) {
                              try {
                                 method.invoke(eventObject, e);
                              } catch (Exception var3) {
                                 Log.error((Throwable)var3);
                              }

                           }
                        });
                     }
                  } catch (NoSuchMethodException var12) {
                     Log.error(fieldName + "Click  未实现");
                  }
               }
            }
         }
      } catch (Exception var13) {
         var13.printStackTrace();
      }

   }

   public static void bindJButtonClick(Object fieldClass, Object eventClass) {
      bindJButtonClick(fieldClass.getClass(), fieldClass, eventClass.getClass(), eventClass);
   }

   public static void bindMenuItemClick(Object item, Map<String, Method> methodMap, Object eventClass) {
      MenuElement[] menuElements = ((MenuElement)item).getSubElements();
      if (methodMap == null) {
         methodMap = getMenuItemMethod(eventClass);
      }

      if (menuElements.length == 0) {
         if (item.getClass().isAssignableFrom(JMenuItem.class)) {
            Method method = (Method)methodMap.get(((JMenuItem)item).getActionCommand() + "MenuItemClick");
            addMenuItemClickEvent(item, method, eventClass);
         }
      } else {
         for(int i = 0; i < menuElements.length; ++i) {
            MenuElement menuElement = menuElements[i];
            Class<?> itemClass = menuElement.getClass();
            if (!itemClass.isAssignableFrom(JPopupMenu.class) && !itemClass.isAssignableFrom(JMenu.class)) {
               if (item.getClass().isAssignableFrom(JMenuItem.class)) {
                  Method method = (Method)methodMap.get(((JMenuItem)menuElement).getActionCommand() + "MenuItemClick");
                  addMenuItemClickEvent(menuElement, method, eventClass);
               }
            } else {
               bindMenuItemClick(menuElement, methodMap, eventClass);
            }
         }
      }

   }

   public static void bindButtonToMenuItem(final Object fieldClass, Object eventClass, Object menu) {
      try {
         if (JMenu.class.isAssignableFrom(menu.getClass()) || JPopupMenu.class.isAssignableFrom(menu.getClass())) {
            try {
               Field[] fields = fieldClass.getClass().getDeclaredFields();
               Field[] var4 = fields;
               int var5 = fields.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  Field field = var4[var6];
                  if (field.getType().isAssignableFrom(JButton.class)) {
                     field.setAccessible(true);
                     JButton fieldValue = (JButton)field.get(fieldClass);
                     String fieldName = field.getName();
                     if (fieldValue != null && field.isAnnotationPresent(ButtonToMenuItem.class)) {
                        ButtonToMenuItem buttonToMenuItem = (ButtonToMenuItem)field.getAnnotation(ButtonToMenuItem.class);

                        try {
                           final Method method = eventClass.getClass().getDeclaredMethod(fieldName + "Click", ActionEvent.class);
                           method.setAccessible(true);
                           if (method != null) {
                              Method addMethod = menu.getClass().getMethod("add", JMenuItem.class);
                              String menuItemName = fieldValue.getText();
                              JMenuItem menuItem = new JMenuItem(buttonToMenuItem.name().length() > 0 ? buttonToMenuItem.name() : menuItemName);
                              menuItem.addActionListener(new ActionListener() {
                                 public void actionPerformed(ActionEvent e) {
                                    try {
                                       method.invoke(fieldClass, e);
                                    } catch (Exception var3) {
                                       Log.error((Throwable)var3);
                                    }

                                 }
                              });
                              addMethod.invoke(menu, menuItem);
                           }
                        } catch (NoSuchMethodException var15) {
                           Log.error(fieldName + "Click  未实现");
                        }
                     }
                  }
               }
            } catch (Exception var16) {
               var16.printStackTrace();
            }
         }
      } catch (Exception var17) {
         Log.error((Throwable)var17);
      }

   }

   private static Map<String, Method> getMenuItemMethod(Object eventClass) {
      Method[] methods = eventClass.getClass().getDeclaredMethods();
      Map<String, Method> methodMap = new HashMap();

      for(int i = 0; i < methods.length; ++i) {
         Method method = methods[i];
         Class<?>[] parameterTypes = method.getParameterTypes();
         if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(ActionEvent.class) && method.getReturnType().isAssignableFrom(Void.TYPE) && method.getName().endsWith("MenuItemClick")) {
            methodMap.put(method.getName(), method);
         }
      }

      return methodMap;
   }

   private static void addMenuItemClickEvent(Object item, final Method method, final Object eventClass) {
      if (method != null && eventClass != null && item.getClass().isAssignableFrom(JMenuItem.class)) {
         ((JMenuItem)item).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent paramActionEvent) {
               try {
                  method.setAccessible(true);
                  method.invoke(eventClass, paramActionEvent);
               } catch (Exception var3) {
                  var3.printStackTrace();
               }

            }
         });
      }

   }
}
