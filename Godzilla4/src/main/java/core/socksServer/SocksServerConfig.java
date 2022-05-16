package core.socksServer;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class SocksServerConfig {
   public String bindAddress;
   public int bindPort;
   public String remoteProxyUrl;
   public String remoteKey;
   public int serverSocketOnceReadSize;
   public int serverPacketSize;
   public InetSocketAddress listenAddress;
   public AtomicInteger clientSocketOnceReadSize;
   public AtomicInteger clientPacketSize;
   public AtomicInteger requestDelay;
   public AtomicInteger requestErrRetry;
   public AtomicInteger requestErrDelay;
   public AtomicInteger capacity;
   public HttpRequestHandle requestHandle;

   public SocksServerConfig(String bindAddress, int bindPort) {
      this.bindAddress = bindAddress;
      this.bindPort = bindPort;
      this.clientSocketOnceReadSize = new AtomicInteger();
      this.clientPacketSize = new AtomicInteger();
      this.requestDelay = new AtomicInteger();
      this.requestErrRetry = new AtomicInteger();
      this.requestErrDelay = new AtomicInteger();
      this.capacity = new AtomicInteger();
   }

   public int getCapacity() {
      return this.capacity.get();
   }

   public void setCapacity(int capacity) {
      this.capacity.set(capacity);
   }

   public HttpRequestHandle getRequestHandle() {
      return this.requestHandle;
   }

   public void setRequestHandle(HttpRequestHandle requestHandle) {
      this.requestHandle = requestHandle;
   }

   public String getBindAddress() {
      return this.bindAddress;
   }

   public void setBindAddress(String bindAddress) {
      this.bindAddress = bindAddress;
      this.listenAddress = new InetSocketAddress(bindAddress, this.bindPort);
   }

   public int getBindPort() {
      return this.bindPort;
   }

   public void setBindPort(int bindPort) {
      this.bindPort = bindPort;
      if (this.bindAddress != null) {
         this.listenAddress = new InetSocketAddress(this.bindAddress, bindPort);
      }

   }

   public String getRemoteProxyUrl() {
      return this.remoteProxyUrl;
   }

   public void setRemoteProxyUrl(String remoteProxyUrl) {
      this.remoteProxyUrl = remoteProxyUrl;
   }

   public String getRemoteKey() {
      return this.remoteKey;
   }

   public void setRemoteKey(String remoteKey) {
      this.remoteKey = remoteKey;
   }

   public int getServerSocketOnceReadSize() {
      return this.serverSocketOnceReadSize;
   }

   public void setServerSocketOnceReadSize(int serverSocketOnceReadSize) {
      this.serverSocketOnceReadSize = serverSocketOnceReadSize;
   }

   public int getServerPacketSize() {
      return this.serverPacketSize;
   }

   public void setServerPacketSize(int serverOnceReadSize) {
      this.serverPacketSize = serverOnceReadSize;
   }

   public int getClientSocketOnceReadSize() {
      return this.clientSocketOnceReadSize.get();
   }

   public void setClientSocketOnceReadSize(int clientSocketOnceReadSize) {
      this.clientSocketOnceReadSize.set(clientSocketOnceReadSize);
   }

   public int getClientPacketSize() {
      return this.clientPacketSize.get();
   }

   public void setClientPacketSize(int clientOnceReadSize) {
      this.clientPacketSize.set(clientOnceReadSize);
   }

   public int getRequestDelay() {
      return this.requestDelay.get();
   }

   public void setRequestDelay(int requestDelay) {
      this.requestDelay.set(requestDelay);
   }

   public int getRequestErrRetry() {
      return this.requestErrRetry.get();
   }

   public void setRequestErrRetry(int requestErrRetry) {
      this.requestErrRetry.set(requestErrRetry);
   }

   public int getRequestErrDelay() {
      return this.requestErrDelay.get();
   }

   public void setRequestErrDelay(int requestErrDelay) {
      this.requestErrDelay.set(requestErrDelay);
   }

   public InetSocketAddress getListenAddress() {
      return this.listenAddress;
   }
}
