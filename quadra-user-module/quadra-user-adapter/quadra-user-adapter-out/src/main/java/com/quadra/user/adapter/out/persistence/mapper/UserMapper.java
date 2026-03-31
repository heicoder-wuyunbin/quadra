package com.quadra.user.adapter.out.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quadra.user.adapter.out.persistence.entity.UserDO;
import com.quadra.user.application.port.in.dto.AdminUserDTO;
import com.quadra.user.application.port.in.dto.AdminUserDetailDTO;
import com.quadra.user.application.port.in.dto.UserProfileDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * CQRS 读模型：使用 JOIN 直接查询并映射为 DTO
     * 绕过了繁琐的 DO -> Domain -> DTO 转换，性能更高
     */
    @Select("""
        SELECT u.id as userId, u.mobile,
               p.nickname, p.avatar, p.gender, p.birthday, p.city,
               p.income, p.profession, p.marriage, p.cover_pic as coverPic, p.tags
        FROM user u
        LEFT JOIN user_profile p ON u.id = p.id
        WHERE u.id = #{userId} AND u.deleted = 0
    """)
    UserProfileDTO findProfileDtoById(@Param("userId") Long userId);

    @Select({
            "<script>",
            "SELECT u.id, u.mobile,",
            "p.nickname, p.gender, p.city,",
            "u.status, u.created_at AS createdAt",
            "FROM `user` u",
            "LEFT JOIN user_profile p ON u.id = p.id AND p.deleted = 0",
            "WHERE u.deleted = 0",
            "<if test='mobile != null and mobile != \"\"'>",
            "AND u.mobile LIKE CONCAT('%', #{mobile}, '%')",
            "</if>",
            "<if test='status != null'>",
            "AND u.status = #{status}",
            "</if>",
            "ORDER BY u.created_at DESC",
            "LIMIT #{size} OFFSET #{offset}",
            "</script>"
    })
    List<AdminUserDTO> listAdminUsers(
            @Param("mobile") String mobile,
            @Param("status") Integer status,
            @Param("offset") long offset,
            @Param("size") int size
    );

    @Select({
            "<script>",
            "SELECT COUNT(1)",
            "FROM `user` u",
            "WHERE u.deleted = 0",
            "<if test='mobile != null and mobile != \"\"'>",
            "AND u.mobile LIKE CONCAT('%', #{mobile}, '%')",
            "</if>",
            "<if test='status != null'>",
            "AND u.status = #{status}",
            "</if>",
            "</script>"
    })
    long countAdminUsers(@Param("mobile") String mobile, @Param("status") Integer status);

    @Select("""
        SELECT 
            u.id, 
            u.mobile, 
            u.status,
            u.created_at AS createdAt, 
            u.updated_at AS updatedAt,
            COALESCE(p.nickname, '') AS nickname,
            COALESCE(p.avatar, '') AS avatar,
            COALESCE(p.gender, 0) AS gender,
            p.birthday,
            COALESCE(p.city, '') AS city,
            p.income,
            p.profession,
            p.marriage,
            p.cover_pic AS coverPic,
            p.tags,
            COALESCE(s.like_notification, 1) AS likeNotification,
            COALESCE(s.comment_notification, 1) AS commentNotification,
            COALESCE(s.system_notification, 1) AS systemNotification
        FROM `user` u
        LEFT JOIN user_profile p ON u.id = p.id
        LEFT JOIN user_setting s ON u.id = s.id
        WHERE u.deleted = 0 AND u.id = #{userId}
    """)
    AdminUserDetailDTO getAdminUserDetail(@Param("userId") Long userId);

    @Update("""
        UPDATE `user`
        SET status = #{status}, updated_at = NOW(), version = version + 1
        WHERE id = #{userId} AND deleted = 0
    """)
    int updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status);

    @Update("""
        UPDATE `user`
        SET password = #{password}, updated_at = NOW(), version = version + 1
        WHERE id = #{userId} AND deleted = 0
    """)
    int updateUserPassword(@Param("userId") Long userId, @Param("password") String password);
}
