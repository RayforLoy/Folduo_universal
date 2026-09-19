package jp.bunkaich.sukashimotion;

import org.junit.Test;
import static org.junit.Assert.*;

public class DeviceSupportTest {
    @Test public void acceptsCommercialFold7Models() {
        for (String model : new String[]{
            "SM-F966B", "SM-F966B/DS", "SM-F9660", "SM-F966U", "SM-F966U1",
            "SM-F966W", "SM-F966N", "SM-F966Q", "SM-F966Z", "SC-56F", "SCG34"
        }) assertTrue(model, DeviceSupport.isFold7(model));
    }

    @Test public void normalizesModelAndRejectsOtherDevices() {
        assertTrue(DeviceSupport.isFold7(" sm-f9660 "));
        for (String model : new String[]{null, "", "SM-F956B", "SM-F766B", "SM-W9026", "SC-55F"})
            assertFalse(String.valueOf(model), DeviceSupport.isFold7(model));
    }

    @Test public void recognizesFold8FamiliesWithoutUsingThemAsAGate() {
        for(String model:new String[]{"SM-F9710","sm-f971b","SM-F976U1"})assertTrue(model,DeviceSupport.isFold8(model));
        assertTrue(DeviceSupport.separateHomes("SM-F9710"));
        assertFalse(DeviceSupport.separateHomes("SM-F9660"));
    }

    @Test public void onlyFineWideRangePublicHingeSensorsDriveAnimation() {
        assertTrue(DeviceSupport.fineHingeSensor(36,.1f,180));
        assertFalse(DeviceSupport.fineHingeSensor(36,90,180));
        assertFalse(DeviceSupport.fineHingeSensor(36,.1f,90));
        assertFalse(DeviceSupport.fineHingeSensor(65686,.01f,180));
    }
}
