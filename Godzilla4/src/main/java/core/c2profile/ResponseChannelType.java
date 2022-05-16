package core.c2profile;

import core.EasyI18N;

public class ResponseChannelType {
   public ResponseChannelEnum responseChannelEnum;
   public String name;

   public ResponseChannelType(ResponseChannelEnum responseChannelEnum, String name) {
      this.responseChannelEnum = responseChannelEnum;
      this.name = name;
   }

   public String toString() {
      return EasyI18N.getI18nString("通道位置: %s Name: %s", this.responseChannelEnum, this.name);
   }
}
