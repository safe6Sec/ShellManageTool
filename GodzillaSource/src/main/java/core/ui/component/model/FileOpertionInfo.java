package core.ui.component.model;

public class FileOpertionInfo {
    private String destFileName;
    private Boolean opertionStatus;
    private String srcFileName;

    public String getSrcFileName() {
        return this.srcFileName;
    }

    public String getDestFileName() {
        return this.destFileName;
    }

    public Boolean getOpertionStatus() {
        return this.opertionStatus;
    }

    public void setSrcFileName(String srcFileName2) {
        this.srcFileName = srcFileName2;
    }

    public void setDestFileName(String destFileName2) {
        this.destFileName = destFileName2;
    }

    public void setOpertionStatus(Boolean opertionStatus2) {
        this.opertionStatus = opertionStatus2;
    }
}
