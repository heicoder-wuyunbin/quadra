package com.quadra.interaction.application.port.in.dto;

import java.util.List;

/**
 * 分页结果
 */
public class PageResult<T> {
    private List<T> list;
    private Long nextCursor;
    private Boolean hasMore;

    public PageResult() {}

    public PageResult(List<T> list, Long nextCursor, Boolean hasMore) {
        this.list = list;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
    }

    // Getters and Setters
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public Long getNextCursor() { return nextCursor; }
    public void setNextCursor(Long nextCursor) { this.nextCursor = nextCursor; }
    public Boolean getHasMore() { return hasMore; }
    public void setHasMore(Boolean hasMore) { this.hasMore = hasMore; }
}
