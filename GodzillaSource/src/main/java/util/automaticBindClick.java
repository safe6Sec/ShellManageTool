package util;

import core.ui.imp.ButtonToMenuItem;
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
            for (Field field : fields) {
                if (field.getType().isAssignableFrom(Button.class)) {
                    field.setAccessible(true);
                    Button fieldValue = (Button) field.get(fieldClass);
                    String fieldName = field.getName();
                    if (fieldValue != null) {
                        try {
                            if (fieldName.equals("selectdFileButton")) {
                                System.out.println();
                            }
                            final Method method = eventClass.getClass().getDeclaredMethod(fieldName + "Click", ActionEvent.class);
                            method.setAccessible(true);
                            if (method != null) {
                                fieldValue.addActionListener(new ActionListener() {
                                     

                                    public void actionPerformed(ActionEvent e) {
                                        try {
                                            method.invoke(fieldClass, e);
                                        } catch (Exception e1) {
                                            Log.error(e1);
                                        }
                                    }
                                });
                            }
                        } catch (NoSuchMethodException e) {
                            System.out.println(fieldName + "Click  未实现");
                        }
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    //用于绑定各个类里面的事件
    public static void bindJButtonClick(final Object fieldClass, Object eventClass) {
        try {
            Field[] fields = fieldClass.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().isAssignableFrom(JButton.class)) {
                    field.setAccessible(true);
                    JButton fieldValue = (JButton) field.get(fieldClass);
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
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } catch (NoSuchMethodException e) {
                            Log.error(fieldName + "Click  未实现");
                        }
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void bindMenuItemClick(Object item, Map<String, Method> methodMap, Object eventClass) {
        MenuElement[] menuElements = ((MenuElement) item).getSubElements();
        if (methodMap == null) {
            methodMap = getMenuItemMethod(eventClass);
        }
        if (menuElements.length != 0) {
            for (MenuElement menuElement : menuElements) {
                Class<?> itemClass = menuElement.getClass();
                if (itemClass.isAssignableFrom(JPopupMenu.class) || itemClass.isAssignableFrom(JMenu.class)) {
                    bindMenuItemClick(menuElement, methodMap, eventClass);
                } else if (item.getClass().isAssignableFrom(JMenuItem.class)) {
                    addMenuItemClickEvent(menuElement, methodMap.get(((JMenuItem) menuElement).getActionCommand() + "MenuItemClick"), eventClass);
                }
            }
        } else if (item.getClass().isAssignableFrom(JMenuItem.class)) {
            addMenuItemClickEvent(item, methodMap.get(((JMenuItem) item).getActionCommand() + "MenuItemClick"), eventClass);
        }
    }

    private static Map<String, Method> getMenuItemMethod(Object eventClass) {
        Method[] methods = eventClass.getClass().getDeclaredMethods();
        Map<String, Method> methodMap = new HashMap<>();
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(ActionEvent.class) && method.getReturnType().isAssignableFrom(Void.TYPE) && method.getName().endsWith("MenuItemClick")) {
                methodMap.put(method.getName(), method);
            }
        }
        return methodMap;
    }

    private static void addMenuItemClickEvent(Object item, final Method method, final Object eventClass) {
        if (method != null && eventClass != null && item.getClass().isAssignableFrom(JMenuItem.class)) {
            ((JMenuItem) item).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent paramActionEvent) {
                    try {
                        method.setAccessible(true);
                        method.invoke(eventClass, paramActionEvent);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void bindButtonToMenuItem(final Object fieldClass, Object eventClass, Object menu) {
        try {
            if (JMenu.class.isAssignableFrom(menu.getClass()) || JPopupMenu.class.isAssignableFrom(menu.getClass())) {
                try {
                    Field[] fields = fieldClass.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getType().isAssignableFrom(JButton.class)) {
                            field.setAccessible(true);
                            JButton fieldValue = (JButton) field.get(fieldClass);
                            String fieldName = field.getName();
                            if (fieldValue != null && field.isAnnotationPresent(ButtonToMenuItem.class)) {
                                ButtonToMenuItem buttonToMenuItem = (ButtonToMenuItem) field.getAnnotation(ButtonToMenuItem.class);
                                try {
                                    final Method method = eventClass.getClass().getDeclaredMethod(fieldName + "Click", ActionEvent.class);
                                    method.setAccessible(true);
                                    if (method != null) {
                                        Method addMethod = menu.getClass().getMethod("add", JMenuItem.class);
                                        String menuItemName = fieldValue.getText();
                                        if (buttonToMenuItem.name().length() > 0) {
                                            menuItemName = buttonToMenuItem.name();
                                        }
                                        JMenuItem menuItem = new JMenuItem(menuItemName);
                                        menuItem.addActionListener(new ActionListener() {
                                             

                                            public void actionPerformed(ActionEvent e) {
                                                try {
                                                    method.invoke(fieldClass, e);
                                                } catch (Exception e1) {
                                                    Log.error(e1);
                                                }
                                            }
                                        });
                                        addMethod.invoke(menu, menuItem);
                                    }
                                } catch (NoSuchMethodException e) {
                                    Log.error(fieldName + "Click  未实现");
                                }
                            }
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } catch (Exception e3) {
            Log.error(e3);
        }
    }
}
