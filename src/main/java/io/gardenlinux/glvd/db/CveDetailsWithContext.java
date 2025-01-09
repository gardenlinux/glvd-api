package io.gardenlinux.glvd.db;

import java.util.List;

public record CveDetailsWithContext(CveDetails details, List<CveContext> contexts) {
}
