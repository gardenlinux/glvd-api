package io.gardenlinux.glvd.dto;

import java.util.List;
import java.util.Objects;

public class Configuration {
    private List<Node> nodes;

    public Configuration() {
    }

    public Configuration(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;
        return Objects.equals(nodes, that.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nodes);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "nodes=" + nodes +
                '}';
    }
}
