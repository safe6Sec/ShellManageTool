package core.c2profile;

import core.annotation.YamlClass;
import core.annotation.YamlComment;

import java.util.LinkedHashMap;

@YamlClass
public class C2Request {
   @YamlComment(
      Comment = "Request 查询字符串 支持C2信道"
   )
   public String requestQueryString = "";
   @YamlComment(
      Comment = "Request Method"
   )
   public String requestMethod = "POST";
   @YamlComment(
      Comment = "是否开启Request Body写入"
   )
   public boolean enabledRequestBody;
   @YamlComment(
      Comment = "请求url参数 支持C2信道"
   )
   public LinkedHashMap<String, String> requestUrlParameters = new LinkedHashMap();
   @YamlComment(
      Comment = "请求表单参数 支持C2信道"
   )
   public LinkedHashMap<String, String> requestFormParameters = new LinkedHashMap();
   @YamlComment(
      Comment = "请求Cookies 支持C2信道"
   )
   public LinkedHashMap<String, String> requestCookies = new LinkedHashMap();
   @YamlComment(
      Comment = "请求协议头 支持C2信道"
   )
   public LinkedHashMap<String, String> requestHeaders = new LinkedHashMap();
   @YamlComment(
      Comment = "请求左边追加数据"
   )
   public byte[] requestLeftBody = "".getBytes();
   @YamlComment(
      Comment = "请求中间数据 支持C2信道"
   )
   public String requestMiddleBody;
   @YamlComment(
      Comment = "请求右边追加数据"
   )
   public byte[] requestRightBody = "".getBytes();
}
