package core.ui.component.model;

import java.text.DecimalFormat;
import util.functions;

public class FileInfo {
    private static final String[] ShowSize = {"KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
    private long size;

    public FileInfo(String size2) {
        this.size = functions.stringToLong(size2, 0).longValue();
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(int size2) {
        this.size = (long) size2;
    }

    public String toString() {
        int em = -1;
        float tmp = (float) this.size;
        float lastTmp = 0.0f;
        if (this.size < 1024) {
            return Long.toString(this.size);
        }
        while (true) {
            tmp /= 1024.0f;
            if (tmp < 1.0f) {
                return new DecimalFormat(".00").format((double) lastTmp) + ShowSize[em];
            }
            em++;
            lastTmp = tmp;
        }
    }
}
