package com.quadra.content.application.service;

import com.quadra.content.application.port.in.PublishVideoUseCase;
import com.quadra.content.application.port.in.command.PublishVideoCommand;
import com.quadra.content.application.port.out.EventPublisherPort;
import com.quadra.content.application.port.out.VideoRepositoryPort;
import com.quadra.content.domain.model.Video;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoApplicationService implements PublishVideoUseCase {

    private final VideoRepositoryPort videoRepositoryPort;
    private final EventPublisherPort eventPublisherPort;

    public VideoApplicationService(
            VideoRepositoryPort videoRepositoryPort,
            EventPublisherPort eventPublisherPort) {
        this.videoRepositoryPort = videoRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    @Transactional
    public Long publishVideo(PublishVideoCommand command) {
        // 1. 获取全局唯一ID
        Long videoId = videoRepositoryPort.nextId();
        
        // 2. Domain 聚合根创建与校验，产生初始事件
        Video video = Video.publish(
            videoId,
            command.userId(),
            command.videoUrl(),
            command.coverUrl(),
            command.duration(),
            command.description()
        );

        // 3. 持久化聚合根
        videoRepositoryPort.save(video);

        // 4. 提取并持久化领域事件到 Outbox 表 (同事务)
        if (!video.getDomainEvents().isEmpty()) {
            eventPublisherPort.publish(video.getDomainEvents());
            video.clearDomainEvents();
        }

        return videoId;
    }
}
