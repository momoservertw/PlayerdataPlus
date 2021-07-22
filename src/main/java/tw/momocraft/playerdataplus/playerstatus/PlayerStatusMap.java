package tw.momocraft.playerdataplus.playerstatus;

import tw.momocraft.coreplus.utils.condition.LocationMap;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatusMap {

    private List<String> ignorePerms;
    private List<LocationMap> locMaps = new ArrayList<>();

    private boolean flyRes;
    private boolean flyCMIT;
    private boolean flyCMIC;
    ;
    private boolean godCMIT;
    private String gmDefault;


    public String getGmDefault() {
        return gmDefault;
    }

    public boolean isFlyCMIC() {
        return flyCMIC;
    }

    public boolean isFlyCMIT() {
        return flyCMIT;
    }

    public boolean isFlyRes() {
        return flyRes;
    }

    public boolean isGodCMIT() {
        return godCMIT;
    }

    public List<LocationMap> getLocMaps() {
        return locMaps;
    }

    public List<String> getIgnorePerms() {
        return ignorePerms;
    }

    public void setGmDefault(String gmDefault) {
        this.gmDefault = gmDefault;
    }

    public void setFlyCMIC(boolean flyCMIC) {
        this.flyCMIC = flyCMIC;
    }

    public void setFlyCMIT(boolean flyCMIT) {
        this.flyCMIT = flyCMIT;
    }

    public void setFlyRes(boolean flyRes) {
        this.flyRes = flyRes;
    }

    public void setGodCMIT(boolean godCMIT) {
        this.godCMIT = godCMIT;
    }

    public void setIgnorePerms(List<String> ignorePerms) {
        this.ignorePerms = ignorePerms;
    }

    public void setLocMaps(List<LocationMap> locMaps) {
        this.locMaps = locMaps;
    }
}
