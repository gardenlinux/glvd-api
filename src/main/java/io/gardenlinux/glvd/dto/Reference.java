package io.gardenlinux.glvd.dto;

import java.util.List;
import java.util.Objects;

public class Reference {
    private String url;
    private String source;
    private List<String> tags;

    public Reference() {
    }

    public Reference(String url, String source, List<String> tags) {
        this.url = url;
        this.source = source;
        this.tags = tags;
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reference reference = (Reference) o;
        return Objects.equals(url, reference.url) && Objects.equals(source, reference.source) && Objects.equals(tags, reference.tags);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(url);
        result = 31 * result + Objects.hashCode(source);
        result = 31 * result + Objects.hashCode(tags);
        return result;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "url='" + url + '\'' +
                ", source='" + source + '\'' +
                ", tags=" + tags +
                '}';
    }
}
