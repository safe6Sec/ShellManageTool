package core.c2profile;

import core.EasyI18N;

public class RequestChannelType {
   public RequestChannelEnum requestChannelEnum;
   public String name;

   public RequestChannelType(RequestChannelEnum requestChannelEnum, String name) {
      this.requestChannelEnum = requestChannelEnum;
      this.name = name;
   }

   public String toString() {
      return EasyI18N.getI18nString("通道位置: %s Name: %s", this.requestChannelEnum, this.name);
   }
}
