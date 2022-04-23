package util.http;

import util.functions;

public class ReqParameter extends Parameter {
    public String format() {
        StringBuffer buffer = new StringBuffer();
        for (String key : this.hashMap.keySet()) {
            buffer.append(key);
            buffer.append("=");
            Object valueObject = this.hashMap.get(key);
            if (valueObject.getClass().isAssignableFrom(byte[].class)) {
                buffer.append(functions.base64Encode((byte[]) valueObject));
            } else {
                buffer.append(functions.base64Encode(((String) valueObject).getBytes()));
            }
            buffer.append("&");
        }
        return buffer.delete(buffer.length() - 1, buffer.length()).toString();
    }

    //把参数转为byte数组
    public byte[] formatEx() {
        return super.serialize();
    }
}
