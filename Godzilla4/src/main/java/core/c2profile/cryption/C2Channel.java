package core.c2profile.cryption;

import core.Encoding;
import core.imp.Cryption;
import core.imp.Payload;
import core.shell.ShellEntity;

public class C2Channel implements Cryption {
   public ShellEntity shellEntity;
   public Payload payload;
   public Encoding encoding;

   public void init(ShellEntity context) {
      this.shellEntity = context;
      this.shellEntity.getEncodingModule();
      this.payload = this.shellEntity.getPayloadModule();
      this.encoding = this.shellEntity.getEncodingModule();
   }

   public byte[] encode(byte[] data) {
      return new byte[0];
   }

   public byte[] decode(byte[] data) {
      return new byte[0];
   }

   public boolean isSendRLData() {
      return false;
   }

   public byte[] generate(String password, String secretKey) {
      return new byte[0];
   }

   public boolean check() {
      return false;
   }
}
