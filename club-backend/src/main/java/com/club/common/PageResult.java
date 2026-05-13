package com.club.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private int page;
    private int size;

    public static <T> PageResult<T> of(List<T> records, long total, int page, int size) {
        PageResult<T> r = new PageResult<>();
        r.setRecords(records);
        r.setTotal(total);
        r.setPage(PageQuery.normalizePage(page));
        r.setSize(PageQuery.normalizeSize(size));
        return r;
    }
}
