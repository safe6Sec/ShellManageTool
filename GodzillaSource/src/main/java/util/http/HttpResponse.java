package util.http;

import core.shell.ShellEntity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.net.HttpCookie;

public class HttpResponse {
    private Map<String, List<String>> headerMap;
    private String message;
    private byte[] result;
    private ShellEntity shellEntity;

    public byte[] getResult() {
        return this.result;
    }

    public Map<String, List<String>> getHeaderMap() {
        return this.headerMap;
    }

    public void setResult(byte[] result2) {
        this.result = result2;
    }

    public void setHeaderMap(Map<String, List<String>> headerMap2) {
        this.headerMap = headerMap2;
    }

    public HttpResponse(HttpURLConnection http, ShellEntity shellEntity2) throws IOException {
        this.shellEntity = shellEntity2;
        handleHeader(http.getHeaderFields());
        ReadAllData(getInputStream(http));
    }




    protected void handleHeader(Map<String, List<String>> map) {
        this.headerMap = map;
        this.message = (String)((List)map.get((Object)null)).get(0);

        try {
            Http http = this.shellEntity.getHttp();
            http.getCookieManager().put(http.getUri(), map);
            http.getCookieManager().getCookieStore().get(http.getUri());
            List<HttpCookie> cookies = http.getCookieManager().getCookieStore().get(http.getUri());
            StringBuilder sb = new StringBuilder();
            cookies.forEach((cookie) -> {
                sb.append(String.format(" %s=%s;", cookie.getName(), cookie.getValue()));
            });
            this.shellEntity.getHeaders().put("Cookie", sb.toString().trim());
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

     
    public InputStream getInputStream(HttpURLConnection httpURLConnection) throws IOException {
        InputStream inputStream = httpURLConnection.getErrorStream();
        return inputStream != null ? inputStream : httpURLConnection.getInputStream();
    }

     
    public void ReadAllData(InputStream inputStream) throws IOException {
        try {
            if (this.headerMap.get("Content-Length") == null || this.headerMap.get("Content-Length").size() <= 0) {
                this.result = ReadUnknownNumData(inputStream);
            } else {
                this.result = ReadKnownNumData(inputStream, Integer.parseInt(this.headerMap.get("Content-Length").get(0)));
            }
        } catch (NumberFormatException e) {
            this.result = ReadUnknownNumData(inputStream);
        }
        this.result = this.shellEntity.getCryptionModel().decode(this.result);
    }

     
    public byte[] ReadKnownNumData(InputStream inputStream, int num) throws IOException {
        if (num > 0) {
            byte[] temp = new byte[5120];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (true) {
                int readOneNum = inputStream.read(temp);
                if (readOneNum == -1) {
                    return bos.toByteArray();
                }
                bos.write(temp, 0, readOneNum);
            }
        } else if (num == 0) {
            return ReadUnknownNumData(inputStream);
        } else {
            return null;
        }
    }

     
    public byte[] ReadUnknownNumData(InputStream inputStream) throws IOException {
        byte[] temp = new byte[5120];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (true) {
            int readOneNum = inputStream.read(temp);
            if (readOneNum == -1) {
                return bos.toByteArray();
            }
            bos.write(temp, 0, readOneNum);
        }
    }
}
