package tw.momocraft.playerdataplus.api;

import tw.momocraft.playerdataplus.PlayerdataPlus;

public class PlayerdataPlusAPI {

    public static PlayerDataInterface getPlayer() {
        return PlayerdataPlus.getInstance().getPlaceData();
    }

}
