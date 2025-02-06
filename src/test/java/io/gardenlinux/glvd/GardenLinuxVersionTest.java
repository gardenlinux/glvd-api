package io.gardenlinux.glvd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GardenLinuxVersionTest {

    @Test
    public void canGetPreviousVersion() {
        var gardenLinuxVersion = new GardenLinuxVersion("1592.5");
        assertEquals("1592.4", gardenLinuxVersion.previousPatchVersion());
        assertEquals("1592.5", gardenLinuxVersion.printVersion());
    }

}