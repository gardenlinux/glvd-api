package io.gardenlinux.glvd.dto;

import java.util.List;

public record Metrics(List<CvssMetricV2> cvssMetricV2, List<CvssMetricV30> cvssMetricV30, List<CvssMetricV31> cvssMetricV31) {
}
