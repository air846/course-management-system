package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.AnnouncementRequest;
import com.course.dto.PageRequest;
import com.course.entity.Announcement;

import java.util.List;
import java.util.Map;

/**
 * 公告服务接口
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface AnnouncementService {

    /**
     * 发布或更新公告
     * 
     * @param announcementRequest 公告请求
     * @return 公告记录
     */
    Announcement saveOrUpdateAnnouncement(AnnouncementRequest announcementRequest);

    /**
     * 删除公告
     * 
     * @param id 公告ID
     * @return 是否删除成功
     */
    boolean deleteAnnouncement(Long id);

    /**
     * 根据ID查询公告
     * 
     * @param id 公告ID
     * @return 公告记录
     */
    Announcement getAnnouncementById(Long id);

    /**
     * 根据ID查询公告详情（包含关联信息）
     * 
     * @param id 公告ID
     * @return 公告详情
     */
    Announcement getAnnouncementWithDetails(Long id);

    /**
     * 分页查询公告列表
     * 
     * @param pageRequest 分页请求参数
     * @param type 公告类型（可选）
     * @param status 状态（可选）
     * @param targetType 目标用户类型（可选）
     * @param courseId 课程ID（可选）
     * @param publisherId 发布者ID（可选）
     * @return 公告分页列表
     */
    IPage<Announcement> getAnnouncementPage(PageRequest pageRequest, Integer type, Integer status,
                                           Integer targetType, Long courseId, Long publisherId);

    /**
     * 查询用户可见的公告列表
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @param keyword 搜索关键词（可选）
     * @param type 公告类型（可选）
     * @param courseId 课程ID（可选）
     * @return 公告列表
     */
    List<Announcement> getVisibleAnnouncements(Long userId, String userRole, String keyword,
                                              Integer type, Long courseId);

    /**
     * 查询置顶公告
     * 
     * @param userRole 用户角色
     * @param limit 限制数量
     * @return 置顶公告列表
     */
    List<Announcement> getTopAnnouncements(String userRole, Integer limit);

    /**
     * 查询最新公告
     * 
     * @param userRole 用户角色
     * @param limit 限制数量
     * @return 最新公告列表
     */
    List<Announcement> getLatestAnnouncements(String userRole, Integer limit);

    /**
     * 增加阅读次数
     * 
     * @param id 公告ID
     * @return 是否成功
     */
    boolean incrementReadCount(Long id);

    /**
     * 发布公告（将草稿状态的公告发布）
     * 
     * @param id 公告ID
     * @return 是否发布成功
     */
    boolean publishAnnouncement(Long id);

    /**
     * 撤回公告
     * 
     * @param id 公告ID
     * @return 是否撤回成功
     */
    boolean withdrawAnnouncement(Long id);

    /**
     * 置顶公告
     * 
     * @param id 公告ID
     * @param isTop 是否置顶
     * @return 是否成功
     */
    boolean setTopAnnouncement(Long id, boolean isTop);

    /**
     * 获取公告统计信息
     * 
     * @param type 公告类型（可选）
     * @param status 状态（可选）
     * @param publisherId 发布者ID（可选）
     * @return 统计信息
     */
    Map<String, Object> getAnnouncementStatistics(Integer type, Integer status, Long publisherId);

    /**
     * 检查用户是否有权限查看公告
     * 
     * @param announcementId 公告ID
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return 是否有权限
     */
    boolean hasPermissionToView(Long announcementId, Long userId, String userRole);

    /**
     * 检查用户是否有权限编辑公告
     * 
     * @param announcementId 公告ID
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return 是否有权限
     */
    boolean hasPermissionToEdit(Long announcementId, Long userId, String userRole);

    /**
     * 批量删除公告
     * 
     * @param ids 公告ID列表
     * @return 删除成功的数量
     */
    int batchDeleteAnnouncements(List<Long> ids);

    /**
     * 批量发布公告
     * 
     * @param ids 公告ID列表
     * @return 发布成功的数量
     */
    int batchPublishAnnouncements(List<Long> ids);

    /**
     * 批量撤回公告
     * 
     * @param ids 公告ID列表
     * @return 撤回成功的数量
     */
    int batchWithdrawAnnouncements(List<Long> ids);
}
