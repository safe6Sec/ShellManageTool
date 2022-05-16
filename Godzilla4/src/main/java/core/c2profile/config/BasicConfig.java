package core.c2profile.config;

import core.annotation.YamlComment;
import core.c2profile.CommandMode;

public class BasicConfig {
   @YamlComment(
      Comment = "均衡Uri 如 /upload /login /download"
   )
   public String[] uris = new String[0];
   @YamlComment(
      Comment = "均衡Proxy 如 http://127.0.0.1:8080  socks5://127.0.0.1:1088"
   )
   public String[] proxys = new String[0];
   @YamlComment(
      Comment = "均衡Proxy 如 http://127.0.0.1:8080  socks5://127.0.0.1:1088"
   )
   public CommandMode commandMode;
   @YamlComment(
      Comment = "是否使用默认代理"
   )
   public boolean useDefaultProxy;
   @YamlComment(
      Comment = "是否开启均衡Uri 会随机使用其中任意一个uri"
   )
   public boolean enabledBalanceUris;
   @YamlComment(
      Comment = "是否开启均衡Proxy 会随机使用其中任意一个proxy"
   )
   public boolean enabledBalanceProxys;
   @YamlComment(
      Comment = "是否开启https证书强认证"
   )
   public boolean enabledHttpsTrusted;
   @YamlComment(
      Comment = "是否合并返回包的 \"set-cookie\""
   )
   public boolean mergeResponseCookie;
   @YamlComment(
      Comment = "是否合并shell配置页面的请求头"
   )
   public boolean mergeBasicHeader;
   @YamlComment(
      Comment = "关闭shell后是否清除shell在服务器的缓存"
   )
   public boolean clearup;

   public BasicConfig() {
      this.commandMode = CommandMode.EASY;
      this.useDefaultProxy = true;
      this.enabledBalanceUris = false;
      this.enabledBalanceProxys = false;
      this.enabledHttpsTrusted = true;
      this.mergeResponseCookie = true;
      this.mergeBasicHeader = true;
      this.clearup = false;
   }
}
