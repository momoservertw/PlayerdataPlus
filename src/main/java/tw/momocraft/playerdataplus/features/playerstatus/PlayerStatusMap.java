package tw.momocraft.playerdataplus.features.playerstatus;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatusMap {

    private List<String> locList = new ArrayList<>();
    private List<String> conditions = new ArrayList<>();

    public List<String> getLocList() {
        return locList;
    }

    public void setLocList(List<String> locList) {
        this.locList = locList;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }
}
