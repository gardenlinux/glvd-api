package io.gardenlinux.glvd.dto;

import java.util.List;

public record Reference(String url, String source, List<String> tags) {

}
