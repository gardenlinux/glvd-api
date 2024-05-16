package io.gardenlinux.glvd.dto;

import java.util.List;

public record NodeElement(List<CpeMatch> cpeMatch, boolean negate, String operator) {

}
