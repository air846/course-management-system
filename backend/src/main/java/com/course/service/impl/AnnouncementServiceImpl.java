package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.dto.AnnouncementRequest;
import com.course.dto.PageRequest;
import com.course.entity.Announcement;
import com.course.mapper.AnnouncementMapper;
import com.course.service.AnnouncementService;
import com.course.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 公告服务实现类
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Announcement saveOrUpdateAnnouncement(AnnouncementRequest announcementRequest) {
        log.info("保存或更新公告: title={}", announcementRequest.getTitle());
        
        Announcement announcement;
        if (announcementRequest.getId() != null) {
            // 更新现有公告
            announcement = announcementMapper.selectById(announcementRequest.getId());
            if (announcement == null) {
                throw new IllegalArgumentException("公告不存在");
            }
            
            // 检查权限
            if (!hasPermissionToEdit(announcement.getId(), SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentUserRole())) {
                throw new IllegalArgumentException("无权限编辑此公告");
            }
        } else {
            // 创建新公告
            announcement = new Announcement();
            announcement.setPublisherId(SecurityUtils.getCurrentUserId());
            announcement.setReadCount(0);
        }
        
        // 复制属性
        BeanUtils.copyProperties(announcementRequest, announcement, "id", "publisherId", "readCount");
        
        // 设置发布时间
        if (announcementRequest.getStatus() != null && announcementRequest.getStatus() == 1 && announcement.getPublishTime() == null) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        
        // 保存或更新
        if (announcement.getId() == null) {
            announcementMapper.insert(announcement);
            log.info("公告创建成功: {}", announcement.getId());
        } else {
            announcementMapper.updateById(announcement);
            log.info("公告更新成功: {}", announcement.getId());
        }
        
        return announcement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAnnouncement(Long id) {
        log.info("删除公告: id={}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("公告ID不能为空");
        }
        
        // 检查权限
        if (!hasPermissionToEdit(id, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentUserRole())) {
            throw new IllegalArgumentException("无权限删除此公告");
        }
        
        int result = announcementMapper.deleteById(id);
        boolean success = result > 0;
        
        if (success) {
            log.info("删除公告成功: {}", id);
        } else {
            log.warn("删除公告失败: {}", id);
        }
        
        return success;
    }

    @Override
    public Announcement getAnnouncementById(Long id) {
        if (id == null) {
            return null;
        }
        return announcementMapper.selectById(id);
    }

    @Override
    public Announcement getAnnouncementWithDetails(Long id) {
        if (id == null) {
            return null;
        }
        return announcementMapper.findByIdWithDetails(id);
    }

    @Override
    public IPage<Announcement> getAnnouncementPage(PageRequest pageRequest, Integer type, Integer status,
                                                  Integer targetType, Long courseId, Long publisherId) {
        Page<Announcement> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        
        return announcementMapper.selectAnnouncementPageWithDetails(page, pageRequest.getKeyword(),
                                                                   type, status, targetType, courseId, publisherId);
    }

    @Override
    public List<Announcement> getVisibleAnnouncements(Long userId, String userRole, String keyword,
                                                     Integer type, Long courseId) {
        return announcementMapper.findVisibleAnnouncements(userId, userRole, keyword, type, courseId);
    }

    @Override
    public List<Announcement> getTopAnnouncements(String userRole, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }
        return announcementMapper.findTopAnnouncements(userRole, limit);
    }

    @Override
    public List<Announcement> getLatestAnnouncements(String userRole, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return announcementMapper.findLatestAnnouncements(userRole, limit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementReadCount(Long id) {
        if (id == null) {
            return false;
        }
        
        int result = announcementMapper.incrementReadCount(id);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishAnnouncement(Long id) {
        log.info("发布公告: id={}", id);
        
        if (id == null) {
            return false;
        }
        
        // 检查权限
        if (!hasPermissionToEdit(id, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentUserRole())) {
            throw new IllegalArgumentException("无权限发布此公告");
        }
        
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            return false;
        }
        
        announcement.setStatus(1);
        announcement.setPublishTime(LocalDateTime.now());
        
        int result = announcementMapper.updateById(announcement);
        boolean success = result > 0;
        
        if (success) {
            log.info("发布公告成功: {}", id);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawAnnouncement(Long id) {
        log.info("撤回公告: id={}", id);
        
        if (id == null) {
            return false;
        }
        
        // 检查权限
        if (!hasPermissionToEdit(id, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentUserRole())) {
            throw new IllegalArgumentException("无权限撤回此公告");
        }
        
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            return false;
        }
        
        announcement.setStatus(2);
        
        int result = announcementMapper.updateById(announcement);
        boolean success = result > 0;
        
        if (success) {
            log.info("撤回公告成功: {}", id);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setTopAnnouncement(Long id, boolean isTop) {
        log.info("设置公告置顶: id={}, isTop={}", id, isTop);
        
        if (id == null) {
            return false;
        }
        
        // 检查权限
        if (!hasPermissionToEdit(id, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentUserRole())) {
            throw new IllegalArgumentException("无权限设置此公告置顶");
        }
        
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            return false;
        }
        
        announcement.setIsTop(isTop ? 1 : 0);
        
        int result = announcementMapper.updateById(announcement);
        boolean success = result > 0;
        
        if (success) {
            log.info("设置公告置顶成功: id={}, isTop={}", id, isTop);
        }
        
        return success;
    }

    @Override
    public Map<String, Object> getAnnouncementStatistics(Integer type, Integer status, Long publisherId) {
        return announcementMapper.getAnnouncementStatistics(type, status, publisherId);
    }

    @Override
    public boolean hasPermissionToView(Long announcementId, Long userId, String userRole) {
        if (announcementId == null || userId == null || userRole == null) {
            return false;
        }
        
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null) {
            return false;
        }
        
        // 管理员可以查看所有公告
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        
        // 发布者可以查看自己的公告
        if (announcement.getPublisherId().equals(userId)) {
            return true;
        }
        
        // 检查目标用户类型
        if (announcement.getTargetType() == 1) { // 全部用户
            return true;
        } else if (announcement.getTargetType() == 2 && "STUDENT".equals(userRole)) { // 学生
            return true;
        } else if (announcement.getTargetType() == 3 && "TEACHER".equals(userRole)) { // 教师
            return true;
        } else if (announcement.getTargetType() == 4 && "ADMIN".equals(userRole)) { // 管理员
            return true;
        }
        
        return false;
    }

    @Override
    public boolean hasPermissionToEdit(Long announcementId, Long userId, String userRole) {
        if (announcementId == null || userId == null || userRole == null) {
            return false;
        }
        
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null) {
            return false;
        }
        
        // 管理员可以编辑所有公告
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        
        // 发布者可以编辑自己的公告
        if (announcement.getPublisherId().equals(userId)) {
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteAnnouncements(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        log.info("批量删除公告: 数量={}", ids.size());
        
        int count = 0;
        for (Long id : ids) {
            if (deleteAnnouncement(id)) {
                count++;
            }
        }
        
        log.info("批量删除公告完成: 成功{}条", count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchPublishAnnouncements(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        log.info("批量发布公告: 数量={}", ids.size());
        
        int count = 0;
        for (Long id : ids) {
            if (publishAnnouncement(id)) {
                count++;
            }
        }
        
        log.info("批量发布公告完成: 成功{}条", count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchWithdrawAnnouncements(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        log.info("批量撤回公告: 数量={}", ids.size());
        
        int count = 0;
        for (Long id : ids) {
            if (withdrawAnnouncement(id)) {
                count++;
            }
        }
        
        log.info("批量撤回公告完成: 成功{}条", count);
        return count;
    }
}
