package io.gardenlinux.glvd.dto;

import java.util.Objects;

public class Description {

    private String lang;

    private String value;

    public Description() {
    }

    public Description(String lang, String value) {
        this.lang = lang;
        this.value = value;
    }

    public String getLang() {
        return lang;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Description that = (Description) o;
        return Objects.equals(lang, that.lang) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(lang);
        result = 31 * result + Objects.hashCode(value);
        return result;
    }
}
