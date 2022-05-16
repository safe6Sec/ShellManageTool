package core.httpProxy.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ByteUtil {
   public static byte[] readNextLine(InputStream inputStream, boolean appendCRLF) {
      byte last = 0;
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
      byte current = false;

      byte current;
      try {
         while((current = (byte)inputStream.read()) != -1) {
            if (current == 13) {
               last = current;
            } else {
               if (last == 13 && current == 10) {
                  if (appendCRLF) {
                     outputStream.write(13);
                     outputStream.write(10);
                  }
                  break;
               }

               if (last == 13) {
                  outputStream.write(last);
               } else {
                  outputStream.write(current);
               }

               last = current;
            }
         }
      } catch (Exception var6) {
         throw new RuntimeException(var6);
      }

      return outputStream.toByteArray();
   }

   public static byte[] readNextLine(InputStream inputStream) {
      return readNextLine(inputStream, false);
   }

   public static byte[] readInputStream(InputStream inputStream) {
      byte[] temp = new byte[5120];
      int readOneNum = false;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      int readOneNum;
      try {
         while((readOneNum = inputStream.read(temp)) != -1) {
            bos.write(temp, 0, readOneNum);
         }
      } catch (Exception var5) {
         if (bos.size() == 0) {
            throw new RuntimeException(var5);
         }
      }

      return bos.toByteArray();
   }
}
