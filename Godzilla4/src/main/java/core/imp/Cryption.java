package core.imp;

import core.shell.ShellEntity;

public interface Cryption {
   void init(ShellEntity var1);

   byte[] encode(byte[] var1);

   byte[] decode(byte[] var1);

   boolean isSendRLData();

   byte[] generate(String var1, String var2);

   boolean check();
}
