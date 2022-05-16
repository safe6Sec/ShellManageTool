package shells.plugins.generic;

import core.EasyI18N;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.socksServer.HttpRequestHandle;
import core.socksServer.HttpToSocks;
import core.socksServer.PortForward;
import core.socksServer.SimpleHttpRequestHandle;
import core.socksServer.SocketStatus;
import core.socksServer.SocksServerConfig;
import core.ui.component.dialog.GOptionPane;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import shells.plugins.generic.gui.SocksProxyManagePanel;
import shells.plugins.generic.gui.dialog.ChooseNewRetransmissionDialog;
import shells.plugins.generic.gui.dialog.SocksClientRetransmissionConfigManage;
import shells.plugins.generic.model.Retransmission;
import shells.plugins.generic.model.enums.RetransmissionType;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;

public class SocksProxy implements Plugin {
   SocksProxyManagePanel socksProxyManage = new SocksProxyManagePanel();
   JPanel mainPanel = new JPanel(new GridBagLayout());
   HttpToSocks proxyContext;
   SocksServerConfig socksServerConfig;
   ArrayList<Retransmission> retransmissionList = new ArrayList();
   JPopupMenu rightClickMenu;

   public SocksProxy(HttpRequestHandle requestHandle) {
      this.mainPanel.add(new JLabel("下个版本开放"));
      this.rightClickMenu = new JPopupMenu();
      this.socksServerConfig = new SocksServerConfig("127.0.0.1", 1088);
      this.socksServerConfig.requestHandle = requestHandle;
      this.parseConfig();
      this.proxyContext = new HttpToSocks(this.socksServerConfig);
      automaticBindClick.bindJButtonClick(this.socksProxyManage.getClass(), this.socksProxyManage, SocksProxy.class, this);
      JMenuItem stopItem = new JMenuItem("停止代理");
      stopItem.setActionCommand("stop");
      JMenuItem removeItem = new JMenuItem("删除代理");
      removeItem.setActionCommand("remove");
      JMenuItem refreshItem = new JMenuItem("刷新代理");
      refreshItem.setActionCommand("refresh");
      this.rightClickMenu.add(stopItem);
      this.rightClickMenu.add(removeItem);
      this.rightClickMenu.add(refreshItem);
      automaticBindClick.bindMenuItemClick(this.rightClickMenu, (Map)null, this);
      this.socksProxyManage.proxyManageDataView.setRightClickMenu(this.rightClickMenu, true);
      EasyI18N.installObject(this);
      EasyI18N.installObject(this.socksProxyManage);
   }

   public static void main(String[] args) {
      JFrame frame = new JFrame("SocksProxyManagePanel");
      frame.setContentPane((new SocksProxy(new SimpleHttpRequestHandle())).getView());
      frame.setDefaultCloseOperation(3);
      frame.pack();
      frame.setVisible(true);
   }

   public void setHttpRequestHandle(HttpRequestHandle requestHandle) {
      this.socksServerConfig.requestHandle = requestHandle;
   }

   protected void parseConfig() {
      this.socksServerConfig.setBindAddress(this.socksProxyManage.socksBindAddressTextField.getText().trim());
      this.socksServerConfig.setBindPort(Integer.parseInt(this.socksProxyManage.socksBindPortTextField.getText().trim()));
      this.socksServerConfig.remoteProxyUrl = this.socksProxyManage.remoteSocksProxyUrlTextField.getText().trim();
      this.socksServerConfig.remoteKey = this.socksProxyManage.remoteKeyTextField.getText().trim();
      this.socksServerConfig.serverSocketOnceReadSize = Integer.parseInt(this.socksProxyManage.serverSocketOnceReadSizeTextField.getText().trim());
      this.socksServerConfig.serverPacketSize = Integer.parseInt(this.socksProxyManage.serverPacketSizeTextField.getText().trim());
      this.socksServerConfig.clientSocketOnceReadSize.set(Integer.parseInt(this.socksProxyManage.clientSocketOnceReadSizeTextField.getText().trim()));
      this.socksServerConfig.clientPacketSize.set(Integer.parseInt(this.socksProxyManage.clientPacketTextField.getText().trim()));
      this.socksServerConfig.capacity.set(Integer.parseInt(this.socksProxyManage.capacityTextField.getText().trim()));
      this.socksServerConfig.requestDelay.set(Integer.parseInt(this.socksProxyManage.requestDelayTextField.getText().trim()));
      this.socksServerConfig.requestErrRetry.set(Integer.parseInt(this.socksProxyManage.requestErrRetryTextField.getText().trim()));
      this.socksServerConfig.requestErrDelay.set(Integer.parseInt(this.socksProxyManage.requestErrDelayTextField.getText().trim()));
   }

   public boolean testProxyContext(boolean initContext, boolean closeContext) throws UnsupportedOperationException {
      boolean flag = false;

      try {
         if (initContext) {
            this.proxyContext.reset();
            String sessionId = this.proxyContext.generateSessionId();
            if (sessionId == null) {
               new UnsupportedOperationException("未能获取到Session");
            }
         }

         flag = this.proxyContext.testConnect();
         if (closeContext) {
            try {
               this.proxyContext.reset();
            } catch (Exception var5) {
               var5.printStackTrace();
            }
         }

         return flag;
      } catch (Exception var6) {
         throw new UnsupportedOperationException("通信时发生错误 请检查服务是否启动 网络是否畅通 密钥是否正确!", var6);
      }
   }

   private void stopMenuItemClick(ActionEvent e) {
      String identifier = this.socksProxyManage.proxyManageDataView.GetSelectRow1()[0];
      Retransmission ref = (Retransmission)this.retransmissionList.stream().filter((retransmission) -> {
         return retransmission.identifier.equals(identifier);
      }).findFirst().get();
      if (ref.socketStatus.isActive()) {
         if (ref.socketStatus.stop()) {
            GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.socksProxyManage.getMainPanel()), "关闭成功!");
         } else {
            GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.socksProxyManage.getMainPanel()), "关闭失败!");
         }
      } else {
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.socksProxyManage.getMainPanel()), "已关闭!");
      }

      this.refreshTable();
   }

   private void removeMenuItemClick(ActionEvent e) {
      String identifier = this.socksProxyManage.proxyManageDataView.GetSelectRow1()[0];
      Retransmission ref = (Retransmission)this.retransmissionList.stream().filter((retransmission) -> {
         return retransmission.identifier.equals(identifier);
      }).findFirst().get();
      if (!ref.socketStatus.isActive()) {
         this.retransmissionList.remove(ref);
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.socksProxyManage.getMainPanel()), "已删除!");
      } else {
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.socksProxyManage.getMainPanel()), "目标是启动状态 需停止服务才可进行删除!");
      }

      this.refreshTable();
   }

   private void refreshMenuItemClick(ActionEvent e) {
      this.refreshTable();
   }

   private void testButtonClick(ActionEvent actionEvent) {
      this.parseConfig();

      try {
         if (!this.testProxyContext(true, true)) {
            GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), "无法进行通信 请检查服务是否启动 网络是否畅通!");
         } else {
            GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), "successfully!");
         }
      } catch (Exception var3) {
         Log.error((Throwable)var3);
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), var3.getMessage());
      }

   }

   private void startSocksServerButtonClick(ActionEvent actionEvent) {
      this.parseConfig();

      try {
         if (!this.testProxyContext(true, false)) {
            GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), "无法进行通信 请检查服务是否启动 网络是否畅通!");
         } else if (this.proxyContext.start()) {
            GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), "socks代理已启动!");
            ((CardLayout)((CardLayout)this.mainPanel.getLayout())).show(this.mainPanel, "socksProxyManage");
            (new Thread(this::calcTips)).start();
         } else {
            GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), "socks代理启动失败!");
         }
      } catch (Exception var3) {
         Log.error((Throwable)var3);
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), var3.getMessage());
      }

   }

   private void addNewProxyButtonClick(ActionEvent actionEvent) {
      Retransmission choose = ChooseNewRetransmissionDialog.chooseNewProxy(UiFunction.getParentWindow(this.socksProxyManage.getMainPanel()));
      if (choose != null && choose.retransmissionType != RetransmissionType.NULL) {
         SocketStatus socketStatus = null;
         if (choose.retransmissionType == RetransmissionType.PORT_FORWARD) {
            PortForward portForward = new PortForward(new InetSocketAddress(choose.listenAddress, choose.listenPort), this.proxyContext, choose.targetAddress, String.valueOf(choose.targetPort));
            portForward.start();
            socketStatus = portForward;
         } else if (choose.retransmissionType == RetransmissionType.PORT_MAP) {
            socketStatus = this.proxyContext.addBindMirror(choose.listenAddress, String.valueOf(choose.listenPort), choose.targetAddress, String.valueOf(choose.targetPort));
         }

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var5) {
            var5.printStackTrace();
         }

         choose.socketStatus = (SocketStatus)socketStatus;
         this.retransmissionList.add(choose);
         if (((SocketStatus)socketStatus).isActive()) {
            GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.socksProxyManage.getMainPanel()), "启动成功!");
         } else {
            GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.socksProxyManage.getMainPanel()), String.format(EasyI18N.getI18nString("启动失败! 错误信息:%s"), ((SocketStatus)socketStatus).getErrorMessage()));
         }

         this.refreshTable();
      } else {
         Log.error("用户取消选择.......");
      }

   }

   private void stopProxyButtonClick(ActionEvent actionEvent) {
      this.proxyContext.reset();
      this.retransmissionList.clear();
      this.refreshTable();
      ((CardLayout)((CardLayout)this.mainPanel.getLayout())).show(this.mainPanel, "startSocksProxy");
   }

   private void serverProxyConfigButtonClick(ActionEvent actionEvent) {
      SocksClientRetransmissionConfigManage.socksServerConfig(UiFunction.getParentWindow(this.mainPanel), this.socksServerConfig);
   }

   protected void refreshTable() {
      this.socksProxyManage.proxyManageDataView.RemoveALL();
      this.retransmissionList.stream().forEach((retransmission) -> {
         Vector row = new Vector();
         row.add(retransmission.identifier);
         row.add(retransmission.listenAddress);
         row.add(retransmission.listenPort);
         row.add(retransmission.retransmissionType);
         row.add(retransmission.targetAddress);
         row.add(retransmission.targetPort);
         row.add(retransmission.socketStatus.isActive());
         row.add(retransmission.socketStatus.getErrorMessage());
         this.socksProxyManage.proxyManageDataView.AddRow(row);
      });
   }

   protected void calcTips() {
      long lastUpload = 0L;
      long lastDownload = 0L;

      while(this.proxyContext.isAlive()) {
         long runtime = (System.currentTimeMillis() - this.proxyContext.getStartSocksTime()) / 1000L;
         long connNum = (long)this.proxyContext.getSession().size();
         long uploadSpeed = this.proxyContext.getSummaryUploadBytes() - lastUpload;
         long downloadSpeed = this.proxyContext.getSummaryDownloadBytes() - lastDownload;
         lastUpload = this.proxyContext.getSummaryUploadBytes();
         lastDownload = this.proxyContext.getSummaryDownloadBytes();
         String status = String.format(EasyI18N.getI18nString("当前连接数:%d 当前上传速度:%s/s 当前下载速度:%s/s 发送成功请求:%s 发送失败请求:%s 监听地址:%s 监听端口:%d 已上传:%s 已下载:%s  运行时间:%ds"), connNum, functions.getNetworSpeedk(uploadSpeed), functions.getNetworSpeedk(downloadSpeed), this.proxyContext.getRequestSuccessNum(), this.proxyContext.getRequestFailureNum(), this.socksServerConfig.bindAddress, this.socksServerConfig.bindPort, functions.getNetworSpeedk(lastUpload), functions.getNetworSpeedk(lastDownload), runtime);
         this.socksProxyManage.statusLabel.setText(status);

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var15) {
            var15.printStackTrace();
            return;
         }
      }

      this.stopProxyButtonClick((ActionEvent)null);
   }

   public void init(ShellEntity shellEntity) {
      automaticBindClick.bindJButtonClick(SocksProxy.class, this, SocksProxy.class, this);
   }

   public JPanel getView() {
      return this.mainPanel;
   }
}
