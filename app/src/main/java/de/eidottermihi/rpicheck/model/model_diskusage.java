package de.eidottermihi.rpicheck.model;

public class model_diskusage {
    private float used_percent;
    private String disk_title;

    public model_diskusage(float fUsed, String sTitle) {
        this.used_percent =  fUsed;
        this.disk_title = sTitle;
    }

    public float getUsed_percent() {
        return used_percent;
    }

    public void setUsed_percent(float used_percent) {
        this.used_percent = used_percent;
    }

    public String getDisk_title() {
        return disk_title;
    }

    public void setDisk_title(String disk_title) {
        this.disk_title = disk_title;
    }
}
