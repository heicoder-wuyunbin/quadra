package com.quadra.content.domain.model;

import com.quadra.content.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Video 聚合根单元测试
 */
@DisplayName("Video 聚合根测试")
class VideoTest {

    @Test
    @DisplayName("成功发布视频")
    void publishVideoSuccessfully() {
        // Given
        Long id = 1L;
        Long userId = 100L;
        String videoUrl = "https://example.com/video.mp4";
        String coverUrl = "https://example.com/cover.jpg";
        Integer duration = 60;
        String description = "我的视频";

        // When
        Video video = Video.publish(id, userId, videoUrl, coverUrl, duration, description);

        // Then
        assertNotNull(video);
        assertEquals(id, video.getId());
        assertEquals(userId, video.getUserId());
        assertEquals(videoUrl, video.getVideoUrl());
        assertEquals(coverUrl, video.getCoverUrl());
        assertEquals(duration, video.getDuration());
        assertEquals(description, video.getTextContent());
        assertEquals(0, video.getLikeCount());
        assertEquals(0, video.getCommentCount());
        assertFalse(video.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("发布视频 - 视频 URL 为空应抛出异常")
    void publishVideoWithNullVideoUrl() {
        // Then
        assertThrows(DomainException.class, 
            () -> Video.publish(1L, 100L, null, "cover.jpg", 60, "desc"));
    }

    @Test
    @DisplayName("发布视频 - 封面 URL 为空应抛出异常")
    void publishVideoWithNullCoverUrl() {
        // Then
        assertThrows(DomainException.class, 
            () -> Video.publish(1L, 100L, "video.mp4", null, 60, "desc"));
    }

    @Test
    @DisplayName("删除视频成功")
    void deleteVideoSuccessfully() {
        // Given
        Video video = Video.publish(1L, 100L, "video.mp4", "cover.jpg", 60, "desc");

        // When
        video.delete();

        // Then
        assertEquals(1, video.getDeleted());
    }

    @Test
    @DisplayName("删除已删除的视频应抛出异常")
    void deleteAlreadyDeletedVideo() {
        // Given
        Video video = Video.publish(1L, 100L, "video.mp4", "cover.jpg", 60, "desc");
        video.delete();

        // Then
        assertThrows(DomainException.class, () -> video.delete());
    }
}
