package com.quadra.system.adapter.out.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quadra.system.adapter.out.persistence.entity.SysRequestLogDO;
import com.quadra.system.adapter.out.persistence.mapper.dto.ApiStatRow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SysRequestLogMapper extends BaseMapper<SysRequestLogDO> {

    @Select("""
            <script>
            SELECT CONCAT(method, ':', path) AS id,
                   method,
                   path,
                   COUNT(1) AS `count`,
                   CAST(AVG(duration_ms) AS SIGNED) AS avgTime,
                   NULL AS p95Time,
                   (SUM(CASE WHEN status_code &gt;= 400 THEN 1 ELSE 0 END) / COUNT(1)) AS errorRate,
                   MAX(created_at) AS lastCalledAt
            FROM sys_request_log
            <where>
              <if test="method != null and method != ''">
                AND method = #{method}
              </if>
              <if test="keyword != null and keyword != ''">
                AND path LIKE CONCAT('%', #{keyword}, '%')
              </if>
            </where>
            GROUP BY method, path
            ORDER BY lastCalledAt DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    java.util.List<ApiStatRow> selectApiStats(
            @Param("keyword") String keyword,
            @Param("method") String method,
            @Param("limit") long limit,
            @Param("offset") long offset
    );

    @Select("""
            <script>
            SELECT COUNT(1) FROM (
              SELECT 1
              FROM sys_request_log
              <where>
                <if test="method != null and method != ''">
                  AND method = #{method}
                </if>
                <if test="keyword != null and keyword != ''">
                  AND path LIKE CONCAT('%', #{keyword}, '%')
                </if>
              </where>
              GROUP BY method, path
            ) t
            </script>
            """)
    Long countApiStats(
            @Param("keyword") String keyword,
            @Param("method") String method
    );
}
