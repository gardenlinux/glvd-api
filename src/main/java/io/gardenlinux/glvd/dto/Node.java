package io.gardenlinux.glvd.dto;

import java.util.List;

public record Node(NodeElement node, String operator, boolean negate, List<CpeMatch> cpeMatch) {}

