package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.DebSrc;
import io.gardenlinux.glvd.db.SourcePackageCve;
import io.gardenlinux.glvd.releasenotes.ReleaseNoteGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReleaseNoteGeneratorTest {

    final GardenLinuxVersion gardenLinuxVersion = new GardenLinuxVersion("1337.9");
    final int DIST_ID_OLD = 15;
    final int DIST_ID_NEW = 16;
    SourcePackageCve cveOld1 = new SourcePackageCve(
            "CVE-2021-33909",
            "linux",
            "5.10.0-8.3-cloud-amd64",
            gardenLinuxVersion.previousPatchVersion(),
            true,
            "2021-07-20",
            "2021-08-15",
            "2021-08-20",
            7.5f,
            "AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
            7.5f,
            7.5f,
            7.5f,
            7.5f,
            "AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H"
    );
    SourcePackageCve cveOld2 = new SourcePackageCve(
            "CVE-2020-28052",
            "openssl",
            "1.1.1k-1",
            gardenLinuxVersion.previousPatchVersion(),
            true,
            "2020-12-08",
            "2021-01-05",
            "2021-01-10",
            5.9f,
            "AV:N/AC:H/PR:N/UI:N/S:U/C:L/I:L/A:L",
            5.9f,
            5.9f,
            5.9f,
            5.9f,
            "AV:N/AC:H/PR:N/UI:N/S:U/C:L/I:L/A:L",
            "AV:N/AC:H/PR:N/UI:N/S:U/C:L/I:L/A:L",
            "AV:N/AC:H/PR:N/UI:N/S:U/C:L/I:L/A:L",
            "AV:N/AC:H/PR:N/UI:N/S:U/C:L/I:L/A:L"
    );
    SourcePackageCve cveOld3 = new SourcePackageCve(
            "CVE-2019-1563",
            "libxml2",
            "2.9.4+dfsg1-7",
            gardenLinuxVersion.previousPatchVersion(),
            true,
            "2019-07-10",
            "2019-08-05",
            "2019-08-15",
            6.5f,
            "AV:N/AC:L/PR:N/UI:R/S:U/C:H/I:H/A:H",
            6.5f,
            6.5f,
            6.5f,
            6.5f,
            "AV:N/AC:L/PR:N/UI:R/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:N/UI:R/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:N/UI:R/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:N/UI:R/S:U/C:H/I:H/A:H"
    );
    SourcePackageCve cveOld4 = new SourcePackageCve(
            "CVE-2018-18074",
            "curl",
            "7.64.0-4+deb10u1",
            gardenLinuxVersion.previousPatchVersion(),
            false,
            "2018-10-10",
            "2018-11-15",
            "2018-11-20",
            7.5f,
            "AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H",
            7.5f,
            7.5f,
            7.5f,
            7.5f,
            "AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H"
    );
    SourcePackageCve cveOld5 = new SourcePackageCve(
            "CVE-2017-1000367",
            "bash",
            "4.4-5",
            gardenLinuxVersion.previousPatchVersion(),
            true,
            "2017-07-16",
            "2017-08-15",
            "2017-08-20",
            7.8f,
            "AV:L/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
            7.8f,
            7.8f,
            7.8f,
            7.8f,
            "AV:L/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
            "AV:L/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
            "AV:L/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
            "AV:L/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H"
    );
    SourcePackageCve cveNew2 = new SourcePackageCve(
            "CVE-2020-28052",
            "openssl",
            "1.1.5",
            gardenLinuxVersion.printVersion(),
            true,
            "2020-12-08",
            "2021-01-05",
            "2021-01-10",
            5.9f,
            "AV:N/AC:H/PR:N/UI:N/S:U/C:L/I:L/A:L",
            5.9f,
            5.9f,
            5.9f,
            5.9f,
            "AV:N/AC:H/PR:N/UI:N/S:U/C:L/I:L/A:L",
            "AV:N/AC:H/PR:N/UI:N/S:U/C:L/I:L/A:L",
            "AV:N/AC:H/PR:N/UI:N/S:U/C:L/I:L/A:L",
            "AV:N/AC:H/PR:N/UI:N/S:U/C:L/I:L/A:L"
    );
    SourcePackageCve cveNew4 = new SourcePackageCve(
            "CVE-2018-18074",
            "curl",
            "7.64.2",
            gardenLinuxVersion.printVersion(),
            false,
            "2018-10-10",
            "2018-11-15",
            "2018-11-20",
            7.5f,
            "AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H",
            7.5f,
            7.5f,
            7.5f,
            7.5f,
            "AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H",
            "AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H"
    );

    @Test
    public void generateReleaseNotesWithoutCveContext() {
        final List<SourcePackageCve> cvesOldVersion = List.of(cveOld1, cveOld2, cveOld3, cveOld4, cveOld5);
        final List<SourcePackageCve> cvesNewVersion = List.of(cveNew2, cveNew4);
        final List<String> resolvedInNew = List.of();
        final List<DebSrc> sourcePackagesInOldVersion = List.of(new DebSrc(DIST_ID_OLD, "2023-10-01", "curl", "7.64.0-4+deb10u1"), new DebSrc(DIST_ID_OLD, "2023-10-01", "openssl", "1.1.1k-1"), new DebSrc(DIST_ID_OLD, "2023-10-01", "bash", "4.4-5"), new DebSrc(DIST_ID_OLD, "2023-10-01", "libxml2", "2.9.4+dfsg1-7"), new DebSrc(DIST_ID_OLD, "2023-10-01", "linux", "5.10.0-8.3-cloud-amd64"));
        final List<DebSrc> sourcePackagesInNewVersion = List.of(new DebSrc(DIST_ID_NEW, "2023-10-01", "curl", "7.64.2"), new DebSrc(DIST_ID_NEW, "2023-10-01", "openssl", "1.1.5"), new DebSrc(DIST_ID_NEW, "2023-10-01", "bash", "4.5"), new DebSrc(DIST_ID_NEW, "2023-10-01", "libxml2", "2.9.6"), new DebSrc(DIST_ID_NEW, "2023-10-01", "linux", "5.10.2-8.3-cloud-amd64"));

        var actual = new ReleaseNoteGenerator(gardenLinuxVersion, cvesOldVersion, cvesNewVersion, resolvedInNew, sourcePackagesInOldVersion, sourcePackagesInNewVersion).generate();

        assertEquals(3, actual.getPackageList().size());
        assertEquals(cveOld1.getCveId(), actual.getPackageList().get(0).getFixedCves().getFirst());
        assertEquals(cveOld5.getCveId(), actual.getPackageList().get(1).getFixedCves().getFirst());
        assertEquals(cveOld3.getCveId(), actual.getPackageList().get(2).getFixedCves().getFirst());
    }

    @Test
    public void generateReleaseNotesWithCveContext() {
        final List<SourcePackageCve> cvesOldVersion = List.of(cveOld1, cveOld2, cveOld3, cveOld4, cveOld5);
        final List<SourcePackageCve> cvesNewVersion = List.of(cveNew2, cveNew4);
        final List<String> resolvedInNew = List.of(cveOld2.getCveId());
        final List<DebSrc> sourcePackagesInOldVersion = List.of(new DebSrc(DIST_ID_OLD, "2023-10-01", "curl", "7.64.0-4+deb10u1"), new DebSrc(DIST_ID_OLD, "2023-10-01", "openssl", "1.1.1k-1"), new DebSrc(DIST_ID_OLD, "2023-10-01", "bash", "4.4-5"), new DebSrc(DIST_ID_OLD, "2023-10-01", "libxml2", "2.9.4+dfsg1-7"), new DebSrc(DIST_ID_OLD, "2023-10-01", "linux", "5.10.0-8.3-cloud-amd64"));
        final List<DebSrc> sourcePackagesInNewVersion = List.of(new DebSrc(DIST_ID_NEW, "2023-10-01", "curl", "7.64.2"), new DebSrc(DIST_ID_NEW, "2023-10-01", "openssl", "1.1.5"), new DebSrc(DIST_ID_NEW, "2023-10-01", "bash", "4.5"), new DebSrc(DIST_ID_NEW, "2023-10-01", "libxml2", "2.9.6"), new DebSrc(DIST_ID_NEW, "2023-10-01", "linux", "5.10.2-8.3-cloud-amd64"));

        var actual = new ReleaseNoteGenerator(gardenLinuxVersion, cvesOldVersion, cvesNewVersion, resolvedInNew, sourcePackagesInOldVersion, sourcePackagesInNewVersion).generate();

        assertEquals(4, actual.getPackageList().size());
        assertEquals(cveOld1.getCveId(), actual.getPackageList().get(0).getFixedCves().getFirst());
        assertEquals(cveOld2.getCveId(), actual.getPackageList().get(1).getFixedCves().getFirst());
        assertEquals(cveOld5.getCveId(), actual.getPackageList().get(2).getFixedCves().getFirst());
        assertEquals(cveOld3.getCveId(), actual.getPackageList().get(3).getFixedCves().getFirst());
    }

    @Test
    public void generateReleaseNotesWithCveContextOfNonVulnerableCve() {
        final List<SourcePackageCve> cvesOldVersion = List.of(cveOld1, cveOld2, cveOld3, cveOld4, cveOld5);
        final List<SourcePackageCve> cvesNewVersion = List.of(cveNew2, cveNew4);
        final List<String> resolvedInNew = List.of(cveOld1.getCveId());
        final List<DebSrc> sourcePackagesInOldVersion = List.of(new DebSrc(DIST_ID_OLD, "2023-10-01", "curl", "7.64.0-4+deb10u1"), new DebSrc(DIST_ID_OLD, "2023-10-01", "openssl", "1.1.1k-1"), new DebSrc(DIST_ID_OLD, "2023-10-01", "bash", "4.4-5"), new DebSrc(DIST_ID_OLD, "2023-10-01", "libxml2", "2.9.4+dfsg1-7"), new DebSrc(DIST_ID_OLD, "2023-10-01", "linux", "5.10.0-8.3-cloud-amd64"));
        final List<DebSrc> sourcePackagesInNewVersion = List.of(new DebSrc(DIST_ID_NEW, "2023-10-01", "curl", "7.64.2"), new DebSrc(DIST_ID_NEW, "2023-10-01", "openssl", "1.1.5"), new DebSrc(DIST_ID_NEW, "2023-10-01", "bash", "4.5"), new DebSrc(DIST_ID_NEW, "2023-10-01", "libxml2", "2.9.6"), new DebSrc(DIST_ID_NEW, "2023-10-01", "linux", "5.10.2-8.3-cloud-amd64"));

        var actual = new ReleaseNoteGenerator(gardenLinuxVersion, cvesOldVersion, cvesNewVersion, resolvedInNew, sourcePackagesInOldVersion, sourcePackagesInNewVersion).generate();

        assertEquals(3, actual.getPackageList().size());
        assertEquals(cveOld1.getCveId(), actual.getPackageList().get(0).getFixedCves().getFirst());
        assertEquals(cveOld5.getCveId(), actual.getPackageList().get(1).getFixedCves().getFirst());
        assertEquals(cveOld3.getCveId(), actual.getPackageList().get(2).getFixedCves().getFirst());
    }

}