package io.gardenlinux.glvd;

import io.gardenlinux.glvd.version.TwoDigitGardenLinuxVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TwoDigitGardenLinuxVersionTest {

    @Test
    public void canGetPreviousVersion() {
        var gardenLinuxVersion = new TwoDigitGardenLinuxVersion("1592.5");
        assertEquals("1592.4", gardenLinuxVersion.previousPatchVersion());
        assertEquals("1592.5", gardenLinuxVersion.printVersion());
    }

}