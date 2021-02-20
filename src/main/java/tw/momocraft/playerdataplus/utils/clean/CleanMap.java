package tw.momocraft.playerdataplus.utils.clean;

import java.util.List;

public class CleanMap {
    private String groupName;
    private int expiration;
    private boolean backup;

    private boolean residenceBypass;
    private List<String> list;
    private List<String> ignoreList;
    private List<String> list2;
    private List<String> ignoreList2;

    public String getGroupName() {
        return groupName;
    }

    public int getExpiration() {
        return expiration;
    }

    public boolean isBackup() {
        return backup;
    }

    public boolean isResidenceBypass() {
        return residenceBypass;
    }

    public List<String> getList() {
        return list;
    }

    public List<String> getIgnoreList() {
        return ignoreList;
    }

    public List<String> getList2() {
        return list2;
    }

    public List<String> getIgnoreList2() {
        return ignoreList2;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }

    public void setResidenceBypass(boolean residenceBypass) {
        this.residenceBypass = residenceBypass;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setIgnoreList(List<String> ignoreList) {
        this.ignoreList = ignoreList;
    }

    public void setList2(List<String> list2) {
        this.list2 = list2;
    }

    public void setIgnoreList2(List<String> ignoreList2) {
        this.ignoreList2 = ignoreList2;
    }
}
