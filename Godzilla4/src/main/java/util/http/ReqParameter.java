package util.http;

import core.ui.component.dialog.ShellSuperRequest;
import java.util.Iterator;
import util.functions;

public class ReqParameter extends Parameter {
   public String format() {
      String randomRP = ShellSuperRequest.randomReqParameter();
      if (randomRP != null && randomRP.length() > 1) {
         this.add(functions.getRandomString(5), randomRP);
      }

      Iterator<String> keys = this.hashMap.keySet().iterator();

      StringBuffer buffer;
      for(buffer = new StringBuffer(); keys.hasNext(); buffer.append("&")) {
         String key = (String)keys.next();
         buffer.append(key);
         buffer.append("=");
         Object valueObject = this.hashMap.get(key);
         if (valueObject.getClass().isAssignableFrom(byte[].class)) {
            buffer.append(functions.base64EncodeToString((byte[])((byte[])valueObject)));
         } else {
            buffer.append(functions.base64EncodeToString(((String)valueObject).getBytes()));
         }
      }

      String temString = buffer.delete(buffer.length() - 1, buffer.length()).toString();
      return temString;
   }

   public byte[] formatEx() {
      return super.serialize();
   }
}
