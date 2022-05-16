package core.socksServer;

public interface SocketStatus {
   String getErrorMessage();

   boolean isActive();

   boolean start();

   boolean stop();
}
