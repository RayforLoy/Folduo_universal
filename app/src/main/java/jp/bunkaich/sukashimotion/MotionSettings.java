package jp.bunkaich.sukashimotion;

import android.content.Context;

/** Persist the user's start/stop choice, never screenshots or application content. */
final class MotionSettings {
    static final int DEFAULT_BLUR_RADIUS=28;
    static final int DEFAULT_BLUR_START=35;
    static boolean enabled(Context c){return c.getSharedPreferences("motion",0).getBoolean("enabled",false);}
    static void setEnabled(Context c,boolean enabled){c.getSharedPreferences("motion",0).edit().putBoolean("enabled",enabled).commit();}
    static int blurRadius(Context c){return clamp(c.getSharedPreferences("motion",0).getInt("blurRadius",DEFAULT_BLUR_RADIUS),0,60);}
    static void blurRadius(Context c,int value){c.getSharedPreferences("motion",0).edit().putInt("blurRadius",clamp(value,0,60)).apply();}
    static int blurStart(Context c){return clamp(c.getSharedPreferences("motion",0).getInt("blurStart",DEFAULT_BLUR_START),0,90);}
    static void blurStart(Context c,int value){c.getSharedPreferences("motion",0).edit().putInt("blurStart",clamp(value,0,90)).apply();}
    static String recovery(Context c){var p=c.getSharedPreferences("motion",0);return p.contains("recoveryMessage")?UiText.decode(c,p.getString("recoveryMessage","")).resolve(c):(p.getString("recovery","").isEmpty()?"":c.getString(R.string.previous_recovery));}
    static void recovery(Context c,String text){recovery(c,UiText.raw(text));}
    static void recovery(Context c,UiText text){c.getSharedPreferences("motion",0).edit().remove("recovery").putString("recoveryMessage",text.encode(c)).apply();}
    private static int boot(Context c){return android.provider.Settings.Global.getInt(c.getContentResolver(),android.provider.Settings.Global.BOOT_COUNT,-1);}
    static int ownerPid(Context c){var p=c.getSharedPreferences("motion",0);int boot=boot(c);return boot>=0&&p.getInt("displayOwnerBoot",-1)==boot?p.getInt("displayOwnerPid",0):0;}
    static void ownerPid(Context c,int pid){if(pid>0)c.getSharedPreferences("motion",0).edit().putInt("displayOwnerPid",pid).putInt("displayOwnerBoot",boot(c)).commit();}
    private static int clamp(int value,int minimum,int maximum){return Math.max(minimum,Math.min(maximum,value));}
}
