package core.c2profile;

import core.c2profile.exception.UnsupportedOperationException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

public class C2ProfileCheck {
   public static boolean check(InputStream yamlStream) {
      DumperOptions dumperOptions = new DumperOptions();
      dumperOptions.setProcessComments(true);
      dumperOptions.setPrettyFlow(true);
      Yaml yaml = new Yaml(dumperOptions);
      yaml.setBeanAccess(BeanAccess.FIELD);
      C2Profile c2Profile = (C2Profile)yaml.loadAs(yamlStream, C2Profile.class);
      C2ProfileContext ctx = new C2ProfileContext();
      ctx.c2Profile = c2Profile;
      loadRequstChannel(ctx);
      loadResponseChannel(ctx);
      return false;
   }

   public static void loadRequstChannel(C2ProfileContext ctx) throws UnsupportedOperationException {
      AtomicBoolean flag = new AtomicBoolean(false);
      ctx.c2Profile.request.requestUrlParameters.forEach((k, v) -> {
         if ("@@@CHANNEL".equalsIgnoreCase(v)) {
            RequestChannelType _r = new RequestChannelType(RequestChannelEnum.REQUEST_URI_PARAMETER, k);
            if (ctx.requestChannelType != null) {
               throw new UnsupportedOperationException("信道重复定义 已有%s 重复%s", new Object[]{ctx.requestChannelType, _r});
            }

            ctx.requestChannelType = _r;
            flag.set(true);
         }

      });
      RequestChannelType _r;
      if ("@@@CHANNEL".equalsIgnoreCase(ctx.c2Profile.request.requestQueryString)) {
         _r = new RequestChannelType(RequestChannelEnum.REQUEST_QUERY_STRING, (String)null);
         if (ctx.requestChannelType != null) {
            throw new UnsupportedOperationException("信道重复定义 已有%s 重复%s", new Object[]{ctx.requestChannelType, _r});
         }

         ctx.requestChannelType = _r;
         flag.set(true);
      }

      ctx.c2Profile.request.requestHeaders.forEach((k, v) -> {
         if ("@@@CHANNEL".equalsIgnoreCase(v)) {
            RequestChannelType _rr = new RequestChannelType(RequestChannelEnum.REQUEST_HEADER, k);
            if (ctx.requestChannelType != null) {
               throw new UnsupportedOperationException("信道重复定义 已有%s 重复%s", new Object[]{ctx.requestChannelType, _rr});
            }

            ctx.requestChannelType = _rr;
            flag.set(true);
         }

      });
      ctx.c2Profile.request.requestCookies.forEach((k, v) -> {
         if ("@@@CHANNEL".equalsIgnoreCase(v)) {
            RequestChannelType _rrr = new RequestChannelType(RequestChannelEnum.REQUEST_COOKIE, k);
            if (ctx.requestChannelType != null) {
               throw new UnsupportedOperationException("信道重复定义 已有%s 重复%s", new Object[]{ctx.requestChannelType, _rrr});
            }

            ctx.requestChannelType = _rrr;
            flag.set(true);
         }

      });
      ctx.c2Profile.request.requestFormParameters.forEach((k, v) -> {
         if ("@@@CHANNEL".equalsIgnoreCase(v)) {
            RequestChannelType _rrrr = new RequestChannelType(RequestChannelEnum.REQUEST_POST_FORM_PARAMETER, k);
            if (ctx.requestChannelType != null) {
               throw new UnsupportedOperationException("信道重复定义 已有%s 重复%s", new Object[]{ctx.requestChannelType, _rrrr});
            }

            ctx.requestChannelType = _rrrr;
            flag.set(true);
         }

      });
      if ("@@@CHANNEL".equalsIgnoreCase(ctx.c2Profile.request.requestMiddleBody)) {
         _r = new RequestChannelType(RequestChannelEnum.REQUEST_RAW_BODY, (String)null);
         if (ctx.requestChannelType != null) {
            throw new UnsupportedOperationException("信道重复定义 已有%s 重复%s", new Object[]{ctx.requestChannelType, _r});
         }

         ctx.requestChannelType = _r;
         flag.set(true);
      }

      if (!ctx.c2Profile.request.enabledRequestBody && (RequestChannelEnum.REQUEST_RAW_BODY == ctx.requestChannelType.requestChannelEnum || RequestChannelEnum.REQUEST_POST_FORM_PARAMETER == ctx.requestChannelType.requestChannelEnum)) {
         throw new UnsupportedOperationException("信道在请求Body内 但enabledRequestBody并未开启");
      } else if (!flag.get()) {
         throw new UnsupportedOperationException("未定义请求信道");
      }
   }

   public static void loadResponseChannel(C2ProfileContext ctx) throws UnsupportedOperationException {
      AtomicBoolean flag = new AtomicBoolean(false);
      ctx.c2Profile.response.responseHeaders.forEach((k, v) -> {
         if ("@@@CHANNEL".equalsIgnoreCase(v)) {
            ResponseChannelType _r = new ResponseChannelType(ResponseChannelEnum.RESPONSE_HEADER, k);
            if (ctx.responseChannelType != null) {
               throw new UnsupportedOperationException("信道重复定义 已有%s 重复%s", new Object[]{ctx.requestChannelType, _r});
            }

            ctx.responseChannelType = new ResponseChannelType(ResponseChannelEnum.RESPONSE_HEADER, k);
            flag.set(true);
         }

      });
      ctx.c2Profile.response.responseCookies.forEach((k, v) -> {
         if ("@@@CHANNEL".equalsIgnoreCase(v)) {
            ResponseChannelType _r = new ResponseChannelType(ResponseChannelEnum.RESPONSE_COOKIE, k);
            if (ctx.responseChannelType != null) {
               throw new UnsupportedOperationException("信道重复定义 已有%s 重复%s", new Object[]{ctx.requestChannelType, _r});
            }

            ctx.responseChannelType = _r;
            flag.set(true);
         }

      });
      if ("@@@CHANNEL".equalsIgnoreCase(ctx.c2Profile.response.responseMiddleBody)) {
         ResponseChannelType _r = new ResponseChannelType(ResponseChannelEnum.RESPONSE_RAW_BODY, (String)null);
         if (ctx.responseChannelType != null) {
            throw new UnsupportedOperationException("信道重复定义 已有%s 重复%s", new Object[]{ctx.requestChannelType, _r});
         }

         ctx.responseChannelType = _r;
         flag.set(true);
      }

      if (!flag.get()) {
         throw new UnsupportedOperationException("未定义请求信道");
      }
   }
}
