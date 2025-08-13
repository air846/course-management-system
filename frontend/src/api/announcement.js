import { request } from '@/utils/request'

export const announcementApi = {
  // 发布或更新公告
  saveOrUpdateAnnouncement(data) {
    return request.post('/announcements', data)
  },

  // 删除公告
  deleteAnnouncement(id) {
    return request.delete(`/announcements/${id}`)
  },

  // 查询公告详情
  getAnnouncementById(id) {
    return request.get(`/announcements/${id}`)
  },

  // 分页查询公告列表（管理端）
  getAnnouncementPage(params) {
    return request.get('/announcements/manage', params)
  },

  // 查询用户可见的公告列表
  getVisibleAnnouncements(params) {
    return request.get('/announcements/visible', params)
  },

  // 查询置顶公告
  getTopAnnouncements(params) {
    return request.get('/announcements/top', params)
  },

  // 查询最新公告
  getLatestAnnouncements(params) {
    return request.get('/announcements/latest', params)
  },

  // 发布公告
  publishAnnouncement(id) {
    return request.put(`/announcements/${id}/publish`)
  },

  // 撤回公告
  withdrawAnnouncement(id) {
    return request.put(`/announcements/${id}/withdraw`)
  },

  // 设置置顶
  setTopAnnouncement(id, isTop) {
    return request.put(`/announcements/${id}/top?isTop=${isTop}`)
  },

  // 查询公告统计
  getAnnouncementStatistics(params) {
    return request.get('/announcements/statistics', params)
  },

  // 批量删除公告
  batchDeleteAnnouncements(ids) {
    return request.delete('/announcements/batch', ids)
  },

  // 批量发布公告
  batchPublishAnnouncements(ids) {
    return request.put('/announcements/batch/publish', ids)
  }
}
