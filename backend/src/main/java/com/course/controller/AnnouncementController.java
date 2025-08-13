package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.dto.AnnouncementRequest;
import com.course.dto.AnnouncementResponse;
import com.course.dto.PageRequest;
import com.course.dto.Result;
import com.course.entity.Announcement;
import com.course.service.AnnouncementService;
import com.course.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公告管理控制器
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
@Tag(name = "公告管理", description = "公告管理相关接口")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 发布或更新公告
     */
    @PostMapping
    @Operation(summary = "发布或更新公告", description = "管理员和教师可以发布或更新公告")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<AnnouncementResponse> saveOrUpdateAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        log.info("发布或更新公告: title={}", request.getTitle());
        
        Announcement announcement = announcementService.saveOrUpdateAnnouncement(request);
        AnnouncementResponse response = convertToResponse(announcement);
        
        return Result.success("公告保存成功", response);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告", description = "删除指定公告")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Void> deleteAnnouncement(
            @Parameter(description = "公告ID", required = true)
            @PathVariable @NotNull Long id) {
        log.info("删除公告: id={}", id);
        
        boolean success = announcementService.deleteAnnouncement(id);
        if (success) {
            return Result.<Void>success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 查询公告详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询公告详情", description = "根据ID查询公告详情")
    public Result<AnnouncementResponse> getAnnouncementById(
            @Parameter(description = "公告ID", required = true)
            @PathVariable @NotNull Long id) {
        log.info("查询公告详情: id={}", id);
        
        Announcement announcement = announcementService.getAnnouncementWithDetails(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        
        // 权限检查
        if (!announcementService.hasPermissionToView(id, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentUserRole())) {
            return Result.error("无权查看该公告");
        }
        
        // 增加阅读次数
        announcementService.incrementReadCount(id);
        
        AnnouncementResponse response = convertToResponse(announcement);
        return Result.success(response);
    }

    /**
     * 分页查询公告列表（管理端）
     */
    @GetMapping("/manage")
    @Operation(summary = "分页查询公告列表", description = "管理端分页查询公告列表")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<IPage<AnnouncementResponse>> getAnnouncementPage(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "公告类型")
            @RequestParam(required = false) Integer type,
            @Parameter(description = "状态")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "目标用户类型")
            @RequestParam(required = false) Integer targetType,
            @Parameter(description = "课程ID")
            @RequestParam(required = false) Long courseId,
            @Parameter(description = "发布者ID")
            @RequestParam(required = false) Long publisherId) {
        log.info("分页查询公告: current={}, size={}, keyword={}", current, size, keyword);
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(current);
        pageRequest.setSize(size);
        pageRequest.setKeyword(keyword);
        
        // 如果不是管理员，只能查看自己发布的公告
        if (!SecurityUtils.isAdmin() && publisherId == null) {
            publisherId = SecurityUtils.getCurrentUserId();
        }
        
        IPage<Announcement> announcementPage = announcementService.getAnnouncementPage(pageRequest, type, status,
                                                                                      targetType, courseId, publisherId);
        
        // 转换为响应对象
        IPage<AnnouncementResponse> responsePage = announcementPage.convert(this::convertToResponse);
        
        return Result.success(responsePage);
    }

    /**
     * 查询用户可见的公告列表
     */
    @GetMapping("/visible")
    @Operation(summary = "查询可见公告", description = "查询当前用户可见的公告列表")
    public Result<List<AnnouncementResponse>> getVisibleAnnouncements(
            @Parameter(description = "搜索关键词")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "公告类型")
            @RequestParam(required = false) Integer type,
            @Parameter(description = "课程ID")
            @RequestParam(required = false) Long courseId) {
        log.info("查询可见公告: keyword={}, type={}, courseId={}", keyword, type, courseId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        String userRole = SecurityUtils.getCurrentUserRole();
        
        List<Announcement> announcements = announcementService.getVisibleAnnouncements(userId, userRole, keyword, type, courseId);
        List<AnnouncementResponse> responses = announcements.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 查询置顶公告
     */
    @GetMapping("/top")
    @Operation(summary = "查询置顶公告", description = "查询置顶公告列表")
    public Result<List<AnnouncementResponse>> getTopAnnouncements(
            @Parameter(description = "限制数量", example = "5")
            @RequestParam(defaultValue = "5") Integer limit) {
        log.info("查询置顶公告: limit={}", limit);
        
        String userRole = SecurityUtils.getCurrentUserRole();
        List<Announcement> announcements = announcementService.getTopAnnouncements(userRole, limit);
        List<AnnouncementResponse> responses = announcements.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 查询最新公告
     */
    @GetMapping("/latest")
    @Operation(summary = "查询最新公告", description = "查询最新公告列表")
    public Result<List<AnnouncementResponse>> getLatestAnnouncements(
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("查询最新公告: limit={}", limit);
        
        String userRole = SecurityUtils.getCurrentUserRole();
        List<Announcement> announcements = announcementService.getLatestAnnouncements(userRole, limit);
        List<AnnouncementResponse> responses = announcements.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Result.success(responses);
    }

    /**
     * 发布公告
     */
    @PutMapping("/{id}/publish")
    @Operation(summary = "发布公告", description = "将草稿状态的公告发布")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Void> publishAnnouncement(
            @Parameter(description = "公告ID", required = true)
            @PathVariable @NotNull Long id) {
        log.info("发布公告: id={}", id);
        
        boolean success = announcementService.publishAnnouncement(id);
        if (success) {
            return Result.<Void>success("发布成功", null);
        } else {
            return Result.error("发布失败");
        }
    }

    /**
     * 撤回公告
     */
    @PutMapping("/{id}/withdraw")
    @Operation(summary = "撤回公告", description = "撤回已发布的公告")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Void> withdrawAnnouncement(
            @Parameter(description = "公告ID", required = true)
            @PathVariable @NotNull Long id) {
        log.info("撤回公告: id={}", id);
        
        boolean success = announcementService.withdrawAnnouncement(id);
        if (success) {
            return Result.<Void>success("撤回成功", null);
        } else {
            return Result.error("撤回失败");
        }
    }

    /**
     * 设置置顶
     */
    @PutMapping("/{id}/top")
    @Operation(summary = "设置置顶", description = "设置公告是否置顶")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Void> setTopAnnouncement(
            @Parameter(description = "公告ID", required = true)
            @PathVariable @NotNull Long id,
            @Parameter(description = "是否置顶", required = true)
            @RequestParam @NotNull Boolean isTop) {
        log.info("设置公告置顶: id={}, isTop={}", id, isTop);
        
        boolean success = announcementService.setTopAnnouncement(id, isTop);
        if (success) {
            return Result.<Void>success("设置成功", null);
        } else {
            return Result.error("设置失败");
        }
    }

    /**
     * 查询公告统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "查询公告统计", description = "查询公告统计信息")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Map<String, Object>> getAnnouncementStatistics(
            @Parameter(description = "公告类型")
            @RequestParam(required = false) Integer type,
            @Parameter(description = "状态")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "发布者ID")
            @RequestParam(required = false) Long publisherId) {
        log.info("查询公告统计: type={}, status={}, publisherId={}", type, status, publisherId);
        
        // 如果不是管理员，只能查看自己的统计
        if (!SecurityUtils.isAdmin() && publisherId == null) {
            publisherId = SecurityUtils.getCurrentUserId();
        }
        
        Map<String, Object> statistics = announcementService.getAnnouncementStatistics(type, status, publisherId);
        return Result.success(statistics);
    }

    /**
     * 批量删除公告
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除公告", description = "批量删除公告")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Integer> batchDeleteAnnouncements(@RequestBody List<Long> ids) {
        log.info("批量删除公告: 数量={}", ids.size());
        
        int count = announcementService.batchDeleteAnnouncements(ids);
        return Result.success("批量删除成功", count);
    }

    /**
     * 批量发布公告
     */
    @PutMapping("/batch/publish")
    @Operation(summary = "批量发布公告", description = "批量发布公告")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Integer> batchPublishAnnouncements(@RequestBody List<Long> ids) {
        log.info("批量发布公告: 数量={}", ids.size());
        
        int count = announcementService.batchPublishAnnouncements(ids);
        return Result.success("批量发布成功", count);
    }

    /**
     * 转换为响应对象
     */
    private AnnouncementResponse convertToResponse(Announcement announcement) {
        AnnouncementResponse response = new AnnouncementResponse();
        BeanUtils.copyProperties(announcement, response);
        
        // 设置是否过期
        if (announcement.getExpireTime() != null) {
            response.setIsExpired(announcement.getExpireTime().isBefore(java.time.LocalDateTime.now()));
        } else {
            response.setIsExpired(false);
        }
        
        return response;
    }
}
