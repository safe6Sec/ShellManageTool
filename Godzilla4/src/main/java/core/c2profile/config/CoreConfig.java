package core.c2profile.config;


import core.annotation.YamlComment;

public class CoreConfig {
   @YamlComment(
      Comment = "是否同步请求 同一时间只允许一个http请求"
   )
   public boolean requestSync;
   @YamlComment(
      Comment = "是否开启心跳 某些网站的session过期时间比较短 需要开启动态心跳"
   )
   public boolean enabledHeartbeat;
   @YamlComment(
      Comment = "是否开启错误重试  一般用在负载均衡"
   )
   public boolean enabledErrRetry;
   @YamlComment(
      Comment = "是否开启操作缓存 如文件缓存 命令执行缓存"
   )
   public boolean enabledOperationCache;
   @YamlComment(
      Comment = "是否开启详细日志"
   )
   public boolean enabledDetailLog;
   @YamlComment(
      Comment = "是否开启上帝模式"
   )
   public boolean enabledGodMode;
   @YamlComment(
      Comment = "错误重试最大次数"
   )
   public int errRetryNum;
   @YamlComment(
      Comment = "心跳包延迟 ms"
   )
   public long heartbeatSleepTime;
   @YamlComment(
      Comment = "跳包抖动 百分比"
   )
   public String heartbeatJitter;
}
