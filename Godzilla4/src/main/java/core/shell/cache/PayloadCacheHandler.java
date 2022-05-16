package core.shell.cache;

import core.imp.Payload;
import core.shell.ShellEntity;
import java.io.File;
import util.RC4;
import util.http.ReqParameter;

public abstract class PayloadCacheHandler {
   public static final String[] blackMethod = new String[]{"bigFileDownload", "bigFileUpload", "include", "uploadFile"};
   protected ShellEntity shellEntity;
   protected Payload payload;
   protected CacheDb cacheDb;
   protected boolean initialized;
   protected String currentDirectory;
   protected String shellId;
   protected RC4 rc4;

   protected PayloadCacheHandler(ShellEntity entity, Payload payload) {
      this.shellEntity = entity;
      this.payload = payload;
      this.initialized();
   }

   protected void initialized() {
      if (!this.initialized) {
         this.rc4 = new RC4();
         this.shellId = this.shellEntity.getId();
         File file = new File("GodzillaCache");
         if (file.exists() && !file.isDirectory()) {
            file.delete();
         }

         file.mkdirs();
         this.currentDirectory = String.format("%s/%s/", "GodzillaCache", this.shellId);
         file = new File(this.currentDirectory);
         if (file.exists() && !file.isDirectory()) {
            file.delete();
         }

         file.mkdirs();
         this.cacheDb = new CacheDb(this.shellEntity.getId());
      }

   }

   public abstract byte[] evalFunc(byte[] var1, String var2, String var3, ReqParameter var4);
}
