/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.tree;

import ru.beeline.techradar.domain.TechVersion;

public class IntervalTreeNode {
    TechVersion interval;
    long maxEnd;
    IntervalTreeNode left, right;

    public IntervalTreeNode(TechVersion interval) {
        this.interval = interval;
        this.maxEnd = convertVersionStringToLong(interval.getVersionEnd());
    }

    private long convertVersionStringToLong(String versionString) {
        String[] parts = versionString.split("\\.");
        long result = 0;
        for (String part : parts) {
            result = result * 10000 + Integer.parseInt(part);
        }
        return result;
    }
}