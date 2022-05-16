package core.shell.cache;

import core.imp.Payload;
import core.shell.ShellEntity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import util.Log;
import util.functions;
import util.http.ReqParameter;

public class UsePayloadCacheHandler extends PayloadCacheHandler {
   public UsePayloadCacheHandler(ShellEntity entity, Payload payload) {
      super(entity, payload);
   }

   public byte[] evalFunc(byte[] realResult, String className, String funcName, ReqParameter parameter) {
      if (className == null && funcName != null) {
         try {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            String methodName = stack[3].getMethodName();
            if (Arrays.binarySearch(blackMethod, methodName) < 0) {
               if ("downloadFile".equals(methodName)) {
                  synchronized(this.rc4) {
                     File file = new File(this.currentDirectory + functions.byteArrayToHex(functions.md5(parameter.getParameterByteArray("fileName"))));

                     byte[] ret;
                     try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        Throwable var11 = null;

                        try {
                           ret = functions.gzipD(this.rc4.decryptMessage(functions.readInputStream(fileInputStream), this.shellId));
                        } catch (Throwable var24) {
                           var11 = var24;
                           throw var24;
                        } finally {
                           if (fileInputStream != null) {
                              if (var11 != null) {
                                 try {
                                    fileInputStream.close();
                                 } catch (Throwable var23) {
                                    var11.addSuppressed(var23);
                                 }
                              } else {
                                 fileInputStream.close();
                              }
                           }

                        }
                     } catch (Throwable var26) {
                        return "The cache file does not exist".getBytes();
                     }

                     return ret == null ? new byte[0] : ret;
                  }
               }

               this.payload.fillParameter(className, funcName, parameter);
               ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
               byteArrayOutputStream.write(funcName.getBytes());
               byteArrayOutputStream.write(parameter.formatEx());
               byte[] ret = this.cacheDb.getSetingValue(functions.byteArrayToHex(functions.md5(byteArrayOutputStream.toByteArray())));
               return ret == null ? "The operation has no cache".getBytes() : functions.gzipD(ret);
            }
         } catch (Exception var28) {
            Log.error((Throwable)var28);
         }
      }

      return "Payload does not cache the plugin return".getBytes();
   }
}
