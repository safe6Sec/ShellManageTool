package util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class StringEscapeUtils {
   public static final char LF = '\n';
   public static final char CR = '\r';
   private static final char CSV_DELIMITER = ',';
   private static final char CSV_QUOTE = '"';
   private static final String CSV_QUOTE_STR = String.valueOf('"');
   private static final char[] CSV_SEARCH_CHARS = new char[]{',', '"', '\r', '\n'};

   public static String escapeJava(String str) {
      return escapeJavaStyleString(str, false);
   }

   public static void escapeJava(Writer out, String str) throws IOException {
      escapeJavaStyleString(out, str, false);
   }

   public static String escapeJavaScript(String str) {
      return escapeJavaStyleString(str, true);
   }

   public static void escapeJavaScript(Writer out, String str) throws IOException {
      escapeJavaStyleString(out, str, true);
   }

   private static String escapeJavaStyleString(String str, boolean escapeSingleQuotes) {
      if (str == null) {
         return null;
      } else {
         try {
            StringWriter writer = new StringWriter(str.length() * 2);
            escapeJavaStyleString(writer, str, escapeSingleQuotes);
            return writer.toString();
         } catch (IOException var3) {
            var3.printStackTrace();
            return null;
         }
      }
   }

   private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote) throws IOException {
      if (out == null) {
         throw new IllegalArgumentException("The Writer must not be null");
      } else if (str != null) {
         int sz = str.length();

         for(int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (ch > 4095) {
               out.write("\\u" + hex(ch));
            } else if (ch > 255) {
               out.write("\\u0" + hex(ch));
            } else if (ch > 127) {
               out.write("\\u00" + hex(ch));
            } else if (ch < ' ') {
               switch (ch) {
                  case '\b':
                     out.write(92);
                     out.write(98);
                     break;
                  case '\t':
                     out.write(92);
                     out.write(116);
                     break;
                  case '\n':
                     out.write(92);
                     out.write(110);
                     break;
                  case '\u000b':
                  default:
                     if (ch > 15) {
                        out.write("\\u00" + hex(ch));
                     } else {
                        out.write("\\u000" + hex(ch));
                     }
                     break;
                  case '\f':
                     out.write(92);
                     out.write(102);
                     break;
                  case '\r':
                     out.write(92);
                     out.write(114);
               }
            } else {
               switch (ch) {
                  case '"':
                     out.write(92);
                     out.write(34);
                     break;
                  case '\'':
                     if (escapeSingleQuote) {
                        out.write(92);
                     }

                     out.write(39);
                     break;
                  case '/':
                     out.write(92);
                     out.write(47);
                     break;
                  case '\\':
                     out.write(92);
                     out.write(92);
                     break;
                  default:
                     out.write(ch);
               }
            }
         }

      }
   }

   private static String hex(char ch) {
      return Integer.toHexString(ch).toUpperCase();
   }

   public static String unescapeJava(String str) {
      if (str == null) {
         return null;
      } else {
         try {
            StringWriter writer = new StringWriter(str.length());
            unescapeJava(writer, str);
            return writer.toString();
         } catch (IOException var2) {
            var2.printStackTrace();
            return null;
         }
      }
   }

   public static void unescapeJava(Writer out, String str) throws IOException {
      if (out == null) {
         throw new IllegalArgumentException("The Writer must not be null");
      } else if (str != null) {
         int sz = str.length();
         StringBuffer unicode = new StringBuffer(4);
         boolean hadSlash = false;
         boolean inUnicode = false;

         for(int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (inUnicode) {
               unicode.append(ch);
               if (unicode.length() == 4) {
                  try {
                     int value = Integer.parseInt(unicode.toString(), 16);
                     out.write((char)value);
                     unicode.setLength(0);
                     inUnicode = false;
                     hadSlash = false;
                  } catch (NumberFormatException var9) {
                     throw new RuntimeException("Unable to parse unicode value: " + unicode, var9);
                  }
               }
            } else if (hadSlash) {
               hadSlash = false;
               switch (ch) {
                  case '"':
                     out.write(34);
                     break;
                  case '\'':
                     out.write(39);
                     break;
                  case '\\':
                     out.write(92);
                     break;
                  case 'b':
                     out.write(8);
                     break;
                  case 'f':
                     out.write(12);
                     break;
                  case 'n':
                     out.write(10);
                     break;
                  case 'r':
                     out.write(13);
                     break;
                  case 't':
                     out.write(9);
                     break;
                  case 'u':
                     inUnicode = true;
                     break;
                  default:
                     out.write(ch);
               }
            } else if (ch == '\\') {
               hadSlash = true;
            } else {
               out.write(ch);
            }
         }

         if (hadSlash) {
            out.write(92);
         }

      }
   }

   public static String unescapeJavaScript(String str) {
      return unescapeJava(str);
   }

   public static void unescapeJavaScript(Writer out, String str) throws IOException {
      unescapeJava(out, str);
   }
}
