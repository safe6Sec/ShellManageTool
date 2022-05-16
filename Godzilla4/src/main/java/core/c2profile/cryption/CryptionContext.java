package core.c2profile.cryption;

import java.net.URLDecoder;
import java.net.URLEncoder;
import util.functions;

public class CryptionContext {
   public static String urlEncode(String str) {
      return URLEncoder.encode(str);
   }

   public static String urlDecode(String str) {
      return URLDecoder.decode(str);
   }

   public static String urlEncode(byte[] bytes) {
      return functions.byteArrayToHexPrefix(bytes, "%");
   }

   public static byte[] base64Encode(byte[] bytes) {
      return functions.base64Encode(bytes);
   }

   public static String base64EncodeToString(byte[] bytes) {
      return functions.base64EncodeToString(bytes);
   }

   public static String base64EncodeUrl(byte[] bytes) {
      return urlEncode(base64EncodeToString(bytes));
   }

   public static String md5(String str) {
      return functions.md5(str);
   }

   public static byte[] md5(byte[] bytes) {
      return functions.md5(bytes);
   }

   public static String hex(byte[] bytes) {
      return functions.byteArrayToHex(bytes);
   }

   public static byte[] unHex(String hexStr) {
      return functions.hexToByte(hexStr);
   }

   public static byte[] aes128(byte[] key, byte[] data) {
      return null;
   }
}
