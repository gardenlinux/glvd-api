package io.gardenlinux.glvd;

import io.gardenlinux.glvd.version.ThreeDigitGardenLinuxVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreeDigitGardenLinuxVersionTest {

    @Test
    public void canGetPreviousVersion() {
        var gardenLinuxVersion = new ThreeDigitGardenLinuxVersion("2000.1.0");
        assertEquals("2000.0.0", gardenLinuxVersion.previousMinorVersion());
        assertEquals("2000.1.0", gardenLinuxVersion.printVersion());
    }

}