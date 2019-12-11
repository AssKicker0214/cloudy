package model.data;


import java.text.DecimalFormat;

/**
 * JSON
 */
public class FileInfo {
    private String name = "<null>";
    private char type = '-';
    private long size = 0;

    private long created;
    private long modified;
    private long accessed;

    public long getAccessed() {
        return accessed;
    }

    public FileInfo setAccessed(long accessed) {
        this.accessed = accessed;
        return this;
    }

    public long getSize() {
        return size;
    }

    public String getSizeForHuman(){
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double raw = size + 0.0;
        int i = 0;
        while (i < units.length && raw >= 1024) {
            raw /= 1024;
            i++;
        }
        return String.format("%.2f %s", raw, units[i]);
    }

    public FileInfo setSize(long size) {
        this.size = size;
        return this;
    }

    public String getName() {
        return name;
    }

    public FileInfo setName(String name) {
        this.name = name;
        return this;
    }

    public char getType() {
        return type;
    }

    public FileInfo setType(char type) {
        this.type = type;
        return this;
    }

    public long getCreated() {
        return created;
    }

    public FileInfo setCreated(long created) {
        this.created = created;
        return this;
    }

    public long getModified() {
        return modified;
    }

    public FileInfo setModified(long modified) {
        this.modified = modified;
        return this;
    }
}
