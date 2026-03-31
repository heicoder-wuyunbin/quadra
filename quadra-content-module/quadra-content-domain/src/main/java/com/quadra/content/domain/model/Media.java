package com.quadra.content.domain.model;

/**
 * 媒体值对象
 */
public class Media {
    private String type;   // IMAGE, VIDEO
    private String url;
    private String thumbnail;
    private Integer width;
    private Integer height;

    // 禁用默认无参构造
    private Media() {}

    /**
     * 工厂方法：创建图片媒体
     */
    public static Media image(String url, String thumbnail, Integer width, Integer height) {
        Media media = new Media();
        media.type = "IMAGE";
        media.url = url;
        media.thumbnail = thumbnail;
        media.width = width;
        media.height = height;
        return media;
    }

    /**
     * 工厂方法：创建视频媒体
     */
    public static Media video(String url, String thumbnail, Integer width, Integer height) {
        Media media = new Media();
        media.type = "VIDEO";
        media.url = url;
        media.thumbnail = thumbnail;
        media.width = width;
        media.height = height;
        return media;
    }

    // Getters
    public String getType() { return type; }
    public String getUrl() { return url; }
    public String getThumbnail() { return thumbnail; }
    public Integer getWidth() { return width; }
    public Integer getHeight() { return height; }
}
