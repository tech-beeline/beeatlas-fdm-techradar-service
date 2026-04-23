/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.tree;

import ru.beeline.techradar.domain.TechVersion;

public class IntervalTree {
    private IntervalTreeNode root;

    public void insert(TechVersion interval) {
        root = insert(root, interval);
    }

    private IntervalTreeNode insert(IntervalTreeNode node, TechVersion interval) {
        if (node == null) {
            return new IntervalTreeNode(interval);
        }
        long start = convertVersionStringToLong(interval.getVersionStart());
        if (start < convertVersionStringToLong(node.interval.getVersionStart())) {
            node.left = insert(node.left, interval);
        } else {
            node.right = insert(node.right, interval);
        }
        if (node.maxEnd < convertVersionStringToLong(interval.getVersionEnd())) {
            node.maxEnd = convertVersionStringToLong(interval.getVersionEnd());
        }
        return node;
    }

    public boolean overlaps(TechVersion interval) {
        return overlaps(root, interval);
    }

    private boolean overlaps(IntervalTreeNode node, TechVersion interval) {
        if (node == null) {
            return false;
        }
        if (rangesOverlap(node.interval, interval)) {
            return true;
        }
        if (node.left != null && node.left.maxEnd >= convertVersionStringToLong(interval.getVersionStart())) {
            return overlaps(node.left, interval);
        }
        return overlaps(node.right, interval);
    }

    private boolean rangesOverlap(TechVersion range1, TechVersion range2) {
        long start1 = convertVersionStringToLong(range1.getVersionStart());
        long end1 = convertVersionStringToLong(range1.getVersionEnd());
        long start2 = convertVersionStringToLong(range2.getVersionStart());
        long end2 = convertVersionStringToLong(range2.getVersionEnd());
        return !(end1 < start2 || end2 < start1);
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
