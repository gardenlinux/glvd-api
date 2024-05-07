package io.gardenlinux.glvd.dto;

import java.util.List;
import java.util.Objects;

public class Weakness {

    private String source;

    private String type;

    private List<Description> description;

    public Weakness() {
    }

    public Weakness(String source, String type, List<Description> description) {
        this.source = source;
        this.type = type;
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public String getType() {
        return type;
    }

    public List<Description> getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weakness weakness = (Weakness) o;
        return Objects.equals(source, weakness.source) && Objects.equals(type, weakness.type) && Objects.equals(description, weakness.description);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(source);
        result = 31 * result + Objects.hashCode(type);
        result = 31 * result + Objects.hashCode(description);
        return result;
    }

    @Override
    public String toString() {
        return "Weakness{" +
                "source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", description=" + description +
                '}';
    }
}
