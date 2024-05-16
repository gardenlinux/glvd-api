package io.gardenlinux.glvd.dto;

import java.util.List;

public record Configuration(List<Node> nodes, String operator) {

}
