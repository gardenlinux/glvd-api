package io.gardenlinux.glvd.dto;

import java.util.List;
import java.util.Objects;

public class Node {
    private List<CpeMatch> cpeMatch;
    private boolean negate;
    private String operator;

    public Node() {
    }

    public Node(List<CpeMatch> cpeMatch, boolean negate, String operator) {
        this.cpeMatch = cpeMatch;
        this.negate = negate;
        this.operator = operator;
    }

    public List<CpeMatch> getCpeMatch() {
        return cpeMatch;
    }

    public boolean isNegate() {
        return negate;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "Node{" +
                "cpeMatch=" + cpeMatch +
                ", negate=" + negate +
                ", operator='" + operator + '\'' +
                '}';
    }
}
