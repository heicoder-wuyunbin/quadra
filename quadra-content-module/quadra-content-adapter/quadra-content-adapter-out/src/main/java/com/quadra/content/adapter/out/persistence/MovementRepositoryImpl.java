package com.quadra.content.adapter.out.persistence;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.content.adapter.out.persistence.entity.MovementDO;
import com.quadra.content.adapter.out.persistence.mapper.MovementMapper;
import com.quadra.content.application.port.out.MovementRepositoryPort;
import com.quadra.content.domain.model.Media;
import com.quadra.content.domain.model.Movement;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MovementRepositoryImpl implements MovementRepositoryPort {

    private final MovementMapper movementMapper;
    private final ObjectMapper objectMapper;

    public MovementRepositoryImpl(MovementMapper movementMapper, ObjectMapper objectMapper) {
        this.movementMapper = movementMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(Movement movement) {
        MovementDO movementDO = toMovementDO(movement);
        movementMapper.insert(movementDO);
    }

    @Override
    public Movement findById(Long id) {
        MovementDO movementDO = movementMapper.selectById(id);
        if (movementDO == null) {
            return null;
        }
        return toMovement(movementDO);
    }

    @Override
    public void update(Movement movement) {
        MovementDO movementDO = toMovementDO(movement);
        movementMapper.updateById(movementDO);
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    /**
     * Domain -> DO 转换
     * medias 字段从 List<Media> 转换为 JSON String
     */
    private MovementDO toMovementDO(Movement movement) {
        MovementDO movementDO = new MovementDO();
        movementDO.setId(movement.getId());
        movementDO.setUserId(movement.getUserId());
        movementDO.setTextContent(movement.getTextContent());
        
        // medias: List<Media> -> JSON String
        movementDO.setMedias(serializeMedias(movement.getMedias()));
        
        movementDO.setLongitude(movement.getLongitude());
        movementDO.setLatitude(movement.getLatitude());
        movementDO.setLocationName(movement.getLocationName());
        movementDO.setState(movement.getState());
        movementDO.setLikeCount(movement.getLikeCount());
        movementDO.setCommentCount(movement.getCommentCount());
        movementDO.setVersion(movement.getVersion());
        movementDO.setDeleted(movement.getDeleted());
        return movementDO;
    }

    /**
     * DO -> Domain 转换
     * medias 字段从 JSON String 转换为 List<Media>
     */
    private Movement toMovement(MovementDO movementDO) {
        try {
            // 使用反射重建 Movement 对象
            java.lang.reflect.Constructor<Movement> constructor = Movement.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Movement movement = constructor.newInstance();

            java.lang.reflect.Field idField = Movement.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(movement, movementDO.getId());

            java.lang.reflect.Field userIdField = Movement.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(movement, movementDO.getUserId());

            java.lang.reflect.Field textContentField = Movement.class.getDeclaredField("textContent");
            textContentField.setAccessible(true);
            textContentField.set(movement, movementDO.getTextContent());

            java.lang.reflect.Field mediasField = Movement.class.getDeclaredField("medias");
            mediasField.setAccessible(true);
            mediasField.set(movement, deserializeMedias(movementDO.getMedias()));

            java.lang.reflect.Field longitudeField = Movement.class.getDeclaredField("longitude");
            longitudeField.setAccessible(true);
            longitudeField.set(movement, movementDO.getLongitude());

            java.lang.reflect.Field latitudeField = Movement.class.getDeclaredField("latitude");
            latitudeField.setAccessible(true);
            latitudeField.set(movement, movementDO.getLatitude());

            java.lang.reflect.Field locationNameField = Movement.class.getDeclaredField("locationName");
            locationNameField.setAccessible(true);
            locationNameField.set(movement, movementDO.getLocationName());

            java.lang.reflect.Field stateField = Movement.class.getDeclaredField("state");
            stateField.setAccessible(true);
            stateField.set(movement, movementDO.getState());

            java.lang.reflect.Field likeCountField = Movement.class.getDeclaredField("likeCount");
            likeCountField.setAccessible(true);
            likeCountField.set(movement, movementDO.getLikeCount());

            java.lang.reflect.Field commentCountField = Movement.class.getDeclaredField("commentCount");
            commentCountField.setAccessible(true);
            commentCountField.set(movement, movementDO.getCommentCount());

            java.lang.reflect.Field versionField = Movement.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(movement, movementDO.getVersion());

            java.lang.reflect.Field deletedField = Movement.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(movement, movementDO.getDeleted());

            return movement;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore Movement from DB", e);
        }
    }

    /**
     * 序列化 medias 列表为 JSON 字符串
     */
    private String serializeMedias(List<Media> medias) {
        if (medias == null || medias.isEmpty()) {
            return null;
        }
        try {
            List<Map<String, Object>> mediaMaps = new ArrayList<>();
            for (Media media : medias) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", media.getType());
                map.put("url", media.getUrl());
                map.put("thumbnail", media.getThumbnail());
                map.put("width", media.getWidth());
                map.put("height", media.getHeight());
                mediaMaps.add(map);
            }
            return objectMapper.writeValueAsString(mediaMaps);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize medias", e);
        }
    }

    /**
     * 反序列化 JSON 字符串为 medias 列表
     */
    private List<Media> deserializeMedias(String mediasJson) {
        if (mediasJson == null || mediasJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<Map<String, Object>> mediaMaps = objectMapper.readValue(mediasJson, new TypeReference<>() {});
            List<Media> medias = new ArrayList<>();
            for (Map<String, Object> map : mediaMaps) {
                String type = (String) map.get("type");
                String url = (String) map.get("url");
                String thumbnail = (String) map.get("thumbnail");
                Integer width = map.get("width") != null ? ((Number) map.get("width")).intValue() : null;
                Integer height = map.get("height") != null ? ((Number) map.get("height")).intValue() : null;
                
                Media media;
                if ("IMAGE".equals(type)) {
                    media = Media.image(url, thumbnail, width, height);
                } else {
                    media = Media.video(url, thumbnail, width, height);
                }
                medias.add(media);
            }
            return medias;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize medias", e);
        }
    }
}
