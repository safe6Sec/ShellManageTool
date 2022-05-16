package core.socksServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import util.Log;

public class PortForward implements SocketStatus {
   private InetSocketAddress socketAddress;
   private HttpToSocks httpToSocks;
   private ServerSocket serverSocket;
   private String destHost;
   private String destPort;
   private String errMsg;
   private boolean alive;

   public PortForward(InetSocketAddress socketAddress, HttpToSocks httpToSocks, String destHost, String destPort) {
      this.socketAddress = socketAddress;
      this.httpToSocks = httpToSocks;
      this.destHost = destHost;
      this.destPort = destPort;
      this.alive = true;

      try {
         this.serverSocket = new ServerSocket();
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   public String getErrorMessage() {
      return this.errMsg;
   }

   public boolean isActive() {
      return this.alive;
   }

   public void handle() {
      while(true) {
         try {
            if (this.httpToSocks.isAlive() && this.alive) {
               Socket client = this.serverSocket.accept();
               SocksRelayInfo socksRelayInfo = new SocksRelayInfo(this.httpToSocks.socksServerConfig.clientSocketOnceReadSize.get(), this.httpToSocks.socksServerConfig.capacity.get());
               socksRelayInfo.setClient(client);
               socksRelayInfo.setDestHost(this.destHost);
               socksRelayInfo.setDestPort(Short.decode(this.destPort));
               if (this.httpToSocks.addRelaySocket(socksRelayInfo)) {
                  socksRelayInfo.getClass();
                  (new Thread(socksRelayInfo::startConnect)).start();
               }
               continue;
            }
         } catch (Exception var3) {
            this.stop();
         }

         this.stop();
         return;
      }
   }

   public boolean start() {
      try {
         this.serverSocket.bind(this.socketAddress);
         (new Thread(this::handle)).start();
      } catch (Exception var2) {
         this.errMsg = var2.getLocalizedMessage();
         this.stop();
      }

      return this.alive;
   }

   public boolean stop() {
      if (this.alive) {
         this.alive = false;
         if (this.serverSocket != null) {
            try {
               this.serverSocket.close();
            } catch (IOException var2) {
               Log.error((Throwable)var2);
            }
         }
      }

      return !this.alive;
   }

   public InetSocketAddress getSocketAddress() {
      return this.socketAddress;
   }

   public void setSocketAddress(InetSocketAddress socketAddress) {
      this.socketAddress = socketAddress;
   }

   public HttpToSocks getHttpToSocks() {
      return this.httpToSocks;
   }

   public void setHttpToSocks(HttpToSocks httpToSocks) {
      this.httpToSocks = httpToSocks;
   }
}
