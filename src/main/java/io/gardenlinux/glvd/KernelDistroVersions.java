package io.gardenlinux.glvd;

import java.util.List;

public record KernelDistroVersions(List<String> distros, List<String> versions, List<String> sourcePackageName,
                                   List<String> kernelVersions) {
}
