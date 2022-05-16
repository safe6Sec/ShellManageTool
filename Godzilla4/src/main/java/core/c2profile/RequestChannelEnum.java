package core.c2profile;

import core.EasyI18N;
import core.annotation.YamlClass;

@YamlClass
public enum RequestChannelEnum {
   REQUEST_QUERY_STRING,
   REQUEST_URI_PARAMETER,
   REQUEST_HEADER,
   REQUEST_COOKIE,
   REQUEST_RAW_BODY,
   REQUEST_POST_FORM_PARAMETER;

   public String toString() {
      switch (this) {
         case REQUEST_QUERY_STRING:
            return EasyI18N.getI18nString("请求查询字符串");
         case REQUEST_URI_PARAMETER:
            return EasyI18N.getI18nString("请求URI参数");
         case REQUEST_HEADER:
            return EasyI18N.getI18nString("请求协议头");
         case REQUEST_COOKIE:
            return EasyI18N.getI18nString("请求Cookie");
         case REQUEST_RAW_BODY:
            return EasyI18N.getI18nString("请求体");
         case REQUEST_POST_FORM_PARAMETER:
            return EasyI18N.getI18nString("请求表单参数");
         default:
            return EasyI18N.getI18nString("未定义的枚举项");
      }
   }
}
