package jp.bunkaich.sukashimotion;

import java.util.regex.Pattern;

/** Samsung FoldInteractive log line carrying the fine hinge angle. */
final class AngleLog {
    private AngleLog() {}

    static Pattern pattern(String action) {
        String quoted=Pattern.quote(action);
        // Fold7 uses brackets; Fold8 uses equals signs (with optional spaces).
        return Pattern.compile("^\\s*([0-9.]+)\\s+\\d+\\s+\\d+\\s+I\\s+SprWallpaper\\|FoldInteractive:\\s+onCommand: action(?:\\[|\\s*=\\s*)"
                +quoted+"\\]?, mCurrentAngle(?:\\[|\\s*=\\s*)([0-9.]+)\\]?, isVisible(?:\\[|\\s*=\\s*)true\\]?");
    }
}
