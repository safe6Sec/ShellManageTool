package core.c2profile;

import core.annotation.YamlClass;
import core.annotation.YamlComment;

import java.util.LinkedHashMap;


@YamlClass
public class C2Response {
   @YamlComment(
      Comment = "响应状态码"
   )
   public int responseCode = 200;
   @YamlComment(
      Comment = "响应协议头 支持C2信道"
   )
   public LinkedHashMap<String, String> responseHeaders = new LinkedHashMap();
   @YamlComment(
      Comment = "响应Cookie 支持C2信道"
   )
   public LinkedHashMap<String, String> responseCookies = new LinkedHashMap();
   @YamlComment(
      Comment = "响应左边追加数据"
   )
   public byte[] responseLeftBody = "".getBytes();
   @YamlComment(
      Comment = "请求中间数据 支持C2信道"
   )
   public String responseMiddleBody;
   @YamlComment(
      Comment = "响应右边追加数据"
   )
   public byte[] responseRightBody = "".getBytes();
}
