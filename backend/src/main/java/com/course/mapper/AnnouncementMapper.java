package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 公告数据访问层
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /**
     * 分页查询公告列表（包含发布者和课程信息）
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param type 公告类型
     * @param status 状态
     * @param targetType 目标用户类型
     * @param courseId 课程ID
     * @param publisherId 发布者ID
     * @return 公告分页列表
     */
    IPage<Announcement> selectAnnouncementPageWithDetails(Page<Announcement> page,
                                                         @Param("keyword") String keyword,
                                                         @Param("type") Integer type,
                                                         @Param("status") Integer status,
                                                         @Param("targetType") Integer targetType,
                                                         @Param("courseId") Long courseId,
                                                         @Param("publisherId") Long publisherId);

    /**
     * 查询用户可见的公告列表
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @param keyword 搜索关键词
     * @param type 公告类型
     * @param courseId 课程ID
     * @return 公告列表
     */
    @Select("<script>" +
            "SELECT a.*, " +
            "u.real_name as publisher_name, " +
            "c.course_name, " +
            "CASE " +
            "  WHEN a.type = 1 THEN '系统公告' " +
            "  WHEN a.type = 2 THEN '课程公告' " +
            "  WHEN a.type = 3 THEN '考试公告' " +
            "  WHEN a.type = 4 THEN '活动公告' " +
            "  ELSE '未知' " +
            "END as type_text, " +
            "CASE " +
            "  WHEN a.priority = 1 THEN '低' " +
            "  WHEN a.priority = 2 THEN '中' " +
            "  WHEN a.priority = 3 THEN '高' " +
            "  WHEN a.priority = 4 THEN '紧急' " +
            "  ELSE '普通' " +
            "END as priority_text, " +
            "CASE " +
            "  WHEN a.target_type = 1 THEN '全部用户' " +
            "  WHEN a.target_type = 2 THEN '学生' " +
            "  WHEN a.target_type = 3 THEN '教师' " +
            "  WHEN a.target_type = 4 THEN '管理员' " +
            "  ELSE '未知' " +
            "END as target_type_text, " +
            "CASE " +
            "  WHEN a.status = 0 THEN '草稿' " +
            "  WHEN a.status = 1 THEN '已发布' " +
            "  WHEN a.status = 2 THEN '已撤回' " +
            "  ELSE '未知' " +
            "END as status_text, " +
            "CASE " +
            "  WHEN a.expire_time IS NOT NULL AND a.expire_time &lt; NOW() THEN 1 " +
            "  ELSE 0 " +
            "END as is_expired " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.id " +
            "LEFT JOIN courses c ON a.course_id = c.id " +
            "WHERE a.deleted = 0 " +
            "AND a.status = 1 " +
            "AND (a.expire_time IS NULL OR a.expire_time &gt; NOW()) " +
            "AND (" +
            "  a.target_type = 1 " +
            "  OR (a.target_type = 2 AND #{userRole} = 'STUDENT') " +
            "  OR (a.target_type = 3 AND #{userRole} = 'TEACHER') " +
            "  OR (a.target_type = 4 AND #{userRole} = 'ADMIN') " +
            ") " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.content LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='type != null'>" +
            "AND a.type = #{type} " +
            "</if>" +
            "<if test='courseId != null'>" +
            "AND a.course_id = #{courseId} " +
            "</if>" +
            "ORDER BY a.is_top DESC, a.priority DESC, a.publish_time DESC" +
            "</script>")
    List<Announcement> findVisibleAnnouncements(@Param("userId") Long userId,
                                               @Param("userRole") String userRole,
                                               @Param("keyword") String keyword,
                                               @Param("type") Integer type,
                                               @Param("courseId") Long courseId);

    /**
     * 根据ID查询公告详情（包含关联信息）
     * 
     * @param id 公告ID
     * @return 公告详情
     */
    @Select("SELECT a.*, " +
            "u.real_name as publisher_name, " +
            "c.course_name, " +
            "CASE " +
            "  WHEN a.type = 1 THEN '系统公告' " +
            "  WHEN a.type = 2 THEN '课程公告' " +
            "  WHEN a.type = 3 THEN '考试公告' " +
            "  WHEN a.type = 4 THEN '活动公告' " +
            "  ELSE '未知' " +
            "END as type_text, " +
            "CASE " +
            "  WHEN a.priority = 1 THEN '低' " +
            "  WHEN a.priority = 2 THEN '中' " +
            "  WHEN a.priority = 3 THEN '高' " +
            "  WHEN a.priority = 4 THEN '紧急' " +
            "  ELSE '普通' " +
            "END as priority_text, " +
            "CASE " +
            "  WHEN a.target_type = 1 THEN '全部用户' " +
            "  WHEN a.target_type = 2 THEN '学生' " +
            "  WHEN a.target_type = 3 THEN '教师' " +
            "  WHEN a.target_type = 4 THEN '管理员' " +
            "  ELSE '未知' " +
            "END as target_type_text, " +
            "CASE " +
            "  WHEN a.status = 0 THEN '草稿' " +
            "  WHEN a.status = 1 THEN '已发布' " +
            "  WHEN a.status = 2 THEN '已撤回' " +
            "  ELSE '未知' " +
            "END as status_text, " +
            "CASE " +
            "  WHEN a.expire_time IS NOT NULL AND a.expire_time < NOW() THEN 1 " +
            "  ELSE 0 " +
            "END as is_expired " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.id " +
            "LEFT JOIN courses c ON a.course_id = c.id " +
            "WHERE a.id = #{id} AND a.deleted = 0")
    Announcement findByIdWithDetails(@Param("id") Long id);

    /**
     * 增加阅读次数
     * 
     * @param id 公告ID
     * @return 更新行数
     */
    @Update("UPDATE announcements SET read_count = read_count + 1 WHERE id = #{id}")
    int incrementReadCount(@Param("id") Long id);

    /**
     * 查询置顶公告
     * 
     * @param userRole 用户角色
     * @param limit 限制数量
     * @return 置顶公告列表
     */
    @Select("SELECT a.*, " +
            "u.real_name as publisher_name, " +
            "c.course_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.id " +
            "LEFT JOIN courses c ON a.course_id = c.id " +
            "WHERE a.deleted = 0 " +
            "AND a.status = 1 " +
            "AND a.is_top = 1 " +
            "AND (a.expire_time IS NULL OR a.expire_time > NOW()) " +
            "AND (" +
            "  a.target_type = 1 " +
            "  OR (a.target_type = 2 AND #{userRole} = 'STUDENT') " +
            "  OR (a.target_type = 3 AND #{userRole} = 'TEACHER') " +
            "  OR (a.target_type = 4 AND #{userRole} = 'ADMIN') " +
            ") " +
            "ORDER BY a.priority DESC, a.publish_time DESC " +
            "LIMIT #{limit}")
    List<Announcement> findTopAnnouncements(@Param("userRole") String userRole,
                                           @Param("limit") Integer limit);

    /**
     * 查询最新公告
     * 
     * @param userRole 用户角色
     * @param limit 限制数量
     * @return 最新公告列表
     */
    @Select("SELECT a.*, " +
            "u.real_name as publisher_name, " +
            "c.course_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.id " +
            "LEFT JOIN courses c ON a.course_id = c.id " +
            "WHERE a.deleted = 0 " +
            "AND a.status = 1 " +
            "AND (a.expire_time IS NULL OR a.expire_time > NOW()) " +
            "AND (" +
            "  a.target_type = 1 " +
            "  OR (a.target_type = 2 AND #{userRole} = 'STUDENT') " +
            "  OR (a.target_type = 3 AND #{userRole} = 'TEACHER') " +
            "  OR (a.target_type = 4 AND #{userRole} = 'ADMIN') " +
            ") " +
            "ORDER BY a.publish_time DESC " +
            "LIMIT #{limit}")
    List<Announcement> findLatestAnnouncements(@Param("userRole") String userRole,
                                              @Param("limit") Integer limit);

    /**
     * 统计公告数量
     *
     * @param type 公告类型
     * @param status 状态
     * @param publisherId 发布者ID
     * @return 统计结果
     */
    Map<String, Object> getAnnouncementStatistics(@Param("type") Integer type,
                                                  @Param("status") Integer status,
                                                  @Param("publisherId") Long publisherId);
}
