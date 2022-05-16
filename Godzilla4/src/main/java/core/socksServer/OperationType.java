package core.socksServer;

public class OperationType {
   public static final byte newSession = 0;
   public static final byte openSocket = 1;
   public static final byte closeSocket = 2;
   public static final byte writeData = 3;
   public static final byte getAllConn = 4;
   public static final byte successfully = 5;
   public static final byte bindSocket = 6;
   public static final byte closeSession = 7;
   public static final byte testConn = 8;
}
