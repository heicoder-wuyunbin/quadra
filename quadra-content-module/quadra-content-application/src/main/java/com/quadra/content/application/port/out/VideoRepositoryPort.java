package com.quadra.content.application.port.out;

import com.quadra.content.domain.model.Video;

/**
 * 视频仓储端口
 */
public interface VideoRepositoryPort {
    /**
     * 保存视频
     */
    void save(Video video);
    
    /**
     * 根据ID查询视频
     */
    Video findById(Long id);
    
    /**
     * 更新视频
     */
    void update(Video video);
    
    /**
     * 生成分布式ID
     */
    Long nextId();
}
