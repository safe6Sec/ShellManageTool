package shells.plugins.generic.model;

import core.socksServer.SocketStatus;
import java.util.UUID;
import shells.plugins.generic.model.enums.RetransmissionType;

public class Retransmission {
   public String identifier = UUID.randomUUID().toString();
   public String listenAddress;
   public int listenPort;
   public String targetAddress;
   public int targetPort;
   public RetransmissionType retransmissionType;
   public SocketStatus socketStatus;

   public String toString() {
      return "Retransmission{listenAddress='" + this.listenAddress + '\'' + ", listenPort=" + this.listenPort + ", targetAddress='" + this.targetAddress + '\'' + ", targetPort=" + this.targetPort + ", retransmissionType=" + this.retransmissionType + '}';
   }
}
