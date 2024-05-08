package io.gardenlinux.glvd.dto;

import java.util.List;

public record Node(List<CpeMatch> cpeMatch, boolean negate, String operator) {

}
