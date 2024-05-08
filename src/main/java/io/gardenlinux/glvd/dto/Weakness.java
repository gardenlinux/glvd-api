package io.gardenlinux.glvd.dto;

import java.util.List;

public record Weakness(String source, String type, List<Description> description) {

}
