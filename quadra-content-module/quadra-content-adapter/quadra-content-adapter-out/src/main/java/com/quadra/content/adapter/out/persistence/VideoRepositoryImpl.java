package com.quadra.content.adapter.out.persistence;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.content.adapter.out.persistence.entity.VideoDO;
import com.quadra.content.adapter.out.persistence.mapper.VideoMapper;
import com.quadra.content.application.port.out.VideoRepositoryPort;
import com.quadra.content.domain.model.Video;
import org.springframework.stereotype.Repository;

@Repository
public class VideoRepositoryImpl implements VideoRepositoryPort {

    private final VideoMapper videoMapper;

    public VideoRepositoryImpl(VideoMapper videoMapper) {
        this.videoMapper = videoMapper;
    }

    @Override
    public void save(Video video) {
        VideoDO videoDO = toVideoDO(video);
        videoMapper.insert(videoDO);
    }

    @Override
    public Video findById(Long id) {
        VideoDO videoDO = videoMapper.selectById(id);
        if (videoDO == null) {
            return null;
        }
        return toVideo(videoDO);
    }

    @Override
    public void update(Video video) {
        VideoDO videoDO = toVideoDO(video);
        videoMapper.updateById(videoDO);
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    private VideoDO toVideoDO(Video video) {
        VideoDO videoDO = new VideoDO();
        videoDO.setId(video.getId());
        videoDO.setUserId(video.getUserId());
        videoDO.setTextContent(video.getTextContent());
        videoDO.setVideoUrl(video.getVideoUrl());
        videoDO.setCoverUrl(video.getCoverUrl());
        videoDO.setDuration(video.getDuration());
        videoDO.setLikeCount(video.getLikeCount());
        videoDO.setCommentCount(video.getCommentCount());
        videoDO.setVersion(video.getVersion());
        videoDO.setDeleted(video.getDeleted());
        return videoDO;
    }

    private Video toVideo(VideoDO videoDO) {
        try {
            java.lang.reflect.Constructor<Video> constructor = Video.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Video video = constructor.newInstance();

            java.lang.reflect.Field idField = Video.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(video, videoDO.getId());

            java.lang.reflect.Field userIdField = Video.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(video, videoDO.getUserId());

            java.lang.reflect.Field textContentField = Video.class.getDeclaredField("textContent");
            textContentField.setAccessible(true);
            textContentField.set(video, videoDO.getTextContent());

            java.lang.reflect.Field videoUrlField = Video.class.getDeclaredField("videoUrl");
            videoUrlField.setAccessible(true);
            videoUrlField.set(video, videoDO.getVideoUrl());

            java.lang.reflect.Field coverUrlField = Video.class.getDeclaredField("coverUrl");
            coverUrlField.setAccessible(true);
            coverUrlField.set(video, videoDO.getCoverUrl());

            java.lang.reflect.Field durationField = Video.class.getDeclaredField("duration");
            durationField.setAccessible(true);
            durationField.set(video, videoDO.getDuration());

            java.lang.reflect.Field likeCountField = Video.class.getDeclaredField("likeCount");
            likeCountField.setAccessible(true);
            likeCountField.set(video, videoDO.getLikeCount());

            java.lang.reflect.Field commentCountField = Video.class.getDeclaredField("commentCount");
            commentCountField.setAccessible(true);
            commentCountField.set(video, videoDO.getCommentCount());

            java.lang.reflect.Field versionField = Video.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(video, videoDO.getVersion());

            java.lang.reflect.Field deletedField = Video.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(video, videoDO.getDeleted());

            return video;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore Video from DB", e);
        }
    }
}
