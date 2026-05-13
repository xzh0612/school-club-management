package com.club.common;

public final class PageQuery {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    private PageQuery() {
    }

    public static int normalizePage(int page) {
        return page > 0 ? page : DEFAULT_PAGE;
    }

    public static int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    public static int offset(int page, int size) {
        return (normalizePage(page) - 1) * normalizeSize(size);
    }
}
