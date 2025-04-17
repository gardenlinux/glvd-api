package io.gardenlinux.glvd.db;

import io.gardenlinux.glvd.CveDetail;

import java.util.List;

public record CveDetailsWithContext(CveDetail details, List<CveContext> contexts) {
}
