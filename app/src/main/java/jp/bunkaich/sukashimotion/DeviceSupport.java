package jp.bunkaich.sukashimotion;

import java.util.Locale;

/** Known device quirks. Runtime support is decided from advertised capabilities. */
final class DeviceSupport {
    private DeviceSupport() {}

    private static String normalized(String model) {
        return model == null ? "" : model.trim().toUpperCase(Locale.ROOT);
    }

    static boolean isFold7(String model) {
        String value=normalized(model);
        return value.startsWith("SM-F966")||value.equals("SC-56F")||value.equals("SCG34");
    }

    static boolean isFold8(String model) {
        String value=normalized(model);
        // The tested mainland model is SM-F9710. Accept its regional family and
        // the conventional F976 family without using either as a runtime gate.
        return value.startsWith("SM-F971")||value.startsWith("SM-F976");
    }

    /** Fold8 keeps a distinct launcher activity in each HOME root. */
    static boolean separateHomes(String model) { return isFold8(model); }

    /** A public hinge sensor is fine enough to drive animation directly. */
    static boolean fineHingeSensor(int type,float resolution,float maximumRange) {
        return type==36&&Float.isFinite(resolution)&&resolution>0&&resolution<10
                &&Float.isFinite(maximumRange)&&maximumRange>=160;
    }
}
