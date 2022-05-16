package core.c2profile;

import core.EasyI18N;
import core.annotation.YamlClass;

@YamlClass
public enum ResponseChannelEnum {
   RESPONSE_HEADER,
   RESPONSE_COOKIE,
   RESPONSE_RAW_BODY;

   public String toString() {
      switch (this) {
         case RESPONSE_HEADER:
            return EasyI18N.getI18nString("返回协议头");
         case RESPONSE_COOKIE:
            EasyI18N.getI18nString("返回Cookie");
         case RESPONSE_RAW_BODY:
            EasyI18N.getI18nString("返回体");
         default:
            return EasyI18N.getI18nString("未定义的枚举项");
      }
   }
}
