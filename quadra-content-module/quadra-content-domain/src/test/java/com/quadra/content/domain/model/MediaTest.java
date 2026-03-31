package com.quadra.content.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Media 值对象单元测试
 */
@DisplayName("Media 值对象测试")
class MediaTest {

    @Test
    @DisplayName("创建图片媒体成功")
    void createImageMediaSuccessfully() {
        // Given
        String url = "https://example.com/image.jpg";
        String thumbnail = "https://example.com/thumb.jpg";
        Integer width = 1920;
        Integer height = 1080;

        // When
        Media media = Media.image(url, thumbnail, width, height);

        // Then
        assertNotNull(media);
        assertEquals("IMAGE", media.getType());
        assertEquals(url, media.getUrl());
        assertEquals(thumbnail, media.getThumbnail());
        assertEquals(width, media.getWidth());
        assertEquals(height, media.getHeight());
    }

    @Test
    @DisplayName("创建视频媒体成功")
    void createVideoMediaSuccessfully() {
        // Given
        String url = "https://example.com/video.mp4";
        String thumbnail = "https://example.com/thumb.jpg";
        Integer width = 1920;
        Integer height = 1080;

        // When
        Media media = Media.video(url, thumbnail, width, height);

        // Then
        assertNotNull(media);
        assertEquals("VIDEO", media.getType());
        assertEquals(url, media.getUrl());
    }
}
