package jp.bunkaich.sukashimotion;

import java.util.regex.Matcher;
import org.junit.Test;
import static org.junit.Assert.*;

public class AngleLogTest {
    private static Matcher match(String line){Matcher m=AngleLog.pattern("com.rayfor.folduo.READ_ANGLE").matcher(line);return m.find()?m:null;}

    @Test public void acceptsFold7BracketFormat(){
        Matcher m=match("1789488061.123  1234  5678 I SprWallpaper|FoldInteractive: onCommand: action[com.rayfor.folduo.READ_ANGLE], mCurrentAngle[123.5], isVisible[true]");
        assertNotNull(m);assertEquals("1789488061.123",m.group(1));assertEquals("123.5",m.group(2));
    }
    @Test public void acceptsFold8EqualsFormats(){
        assertNotNull(match("1789488061.123  1234  5678 I SprWallpaper|FoldInteractive: onCommand: action=com.rayfor.folduo.READ_ANGLE, mCurrentAngle=87.25, isVisible=true"));
        assertNotNull(match("1789488061.123  1234  5678 I SprWallpaper|FoldInteractive: onCommand: action = com.rayfor.folduo.READ_ANGLE, mCurrentAngle = 0.0, isVisible = true"));
    }
    @Test public void ignoresHiddenWallpaperAndOtherPackages(){
        assertNull(match("1789488061.123  1234  5678 I SprWallpaper|FoldInteractive: onCommand: action=com.rayfor.folduo.READ_ANGLE, mCurrentAngle=87.25, isVisible=false"));
        assertNull(match("1789488061.123  1234  5678 I SprWallpaper|FoldInteractive: onCommand: action=other.app.READ_ANGLE, mCurrentAngle=87.25, isVisible=true"));
    }
}
