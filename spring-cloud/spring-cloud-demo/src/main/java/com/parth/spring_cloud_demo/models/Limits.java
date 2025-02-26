package com.parth.spring_cloud_demo.models;

public class Limits {
    private final int minimum;
    private final int maximum;

    public Limits(int minimum, int maximum
    ) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    @Override
    public String toString() {
        return "Limits{" +
                "minimum=" + minimum +
                ", maximum=" + maximum +
                '}';
    }
}
