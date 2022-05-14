package tw.momocraft.playerdataplus.handlers;

public class UtilsHandler {

    private static DependHandler dependence;

    public static void setup(boolean reload) {
        dependence = new DependHandler();
        dependence.setup(reload);
    }

    public static DependHandler getDepend() {
        return dependence;
    }

}
