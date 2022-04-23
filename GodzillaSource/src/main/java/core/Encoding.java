package core;

import core.shell.ShellEntity;
import java.io.UnsupportedEncodingException;
import util.Log;

public class Encoding {
    private static final String[] ENCODING_TYPES = {"UTF-8", "GBK", "GB2312", "BIG5", "GB18030", "ISO-8859-1"};
    private final String charsetString;

    private Encoding(String charsetString2) {
        this.charsetString = charsetString2;
    }

    public static String[] getAllEncodingTypes() {
        return ENCODING_TYPES;
    }

    public byte[] Encoding(String string) {
        try {
            return string.getBytes(this.charsetString);
        } catch (UnsupportedEncodingException e) {
            Log.error(e);
            return string.getBytes();
        }
    }

    public String Decoding(byte[] srcData) {
        if (srcData == null) {
            return "";
        }
        try {
            return new String(srcData, this.charsetString);
        } catch (UnsupportedEncodingException e) {
            Log.error(e);
            return new String(srcData);
        }
    }

    public static Encoding getEncoding(ShellEntity entity) {
        return new Encoding(entity.getEncoding());
    }

    public static Encoding getEncoding(String charsetString2) {
        return new Encoding(charsetString2);
    }

    public String toString() {
        return this.charsetString;
    }
}
