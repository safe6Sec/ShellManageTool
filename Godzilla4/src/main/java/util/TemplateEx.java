package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEx {
   public static String run(String code) {
      HashMap<String, String> map = new HashMap();
      String regex = "\\{[a-zA-Z][a-zA-Z0-9_]*\\}";
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(code);

      while(m.find()) {
         String g = m.group(0);
         map.putIfAbsent(g, functions.getRandomString(functions.random(3, 8)));
      }

      String key;
      for(Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); code = code.replace(key, (CharSequence)map.get(key))) {
         key = (String)iterator.next();
      }

      return code;
   }
}
