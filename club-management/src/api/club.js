import request from '../utils/request'

/**
 * 获取社团列表
 */
export function getClubList(page = 1, size = 10, status = '', keyword = '') {
  return request({
    url: '/clubs',
    method: 'get',
    params: { page, size, status, keyword }
  })
}

/**
 * 获取社团详情
 */
export function getClubDetail(id) {
  return request({
    url: `/clubs/${id}`,
    method: 'get'
  })
}

/**
 * 创建社团
 */
export function createClub(data) {
  return request({
    url: '/clubs',
    method: 'post',
    data
  })
}

/**
 * 更新社团
 */
export function updateClub(id, data) {
  return request({
    url: `/clubs/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除社团
 */
export function deleteClub(id) {
  return request({
    url: `/clubs/${id}`,
    method: 'delete'
  })
}

/**
 * 搜索社团
 */
export function searchClubs(keyword, page = 1, size = 10) {
  return request({
    url: '/clubs/search',
    method: 'get',
    params: { keyword, page, size }
  })
}

/**
 * 获取社团管理统计
 */
export function getClubStats() {
  return request({
    url: '/clubs/stats',
    method: 'get'
  })
}

/**
 * 获取社团成员
 */
export function getClubMembers(clubId, page = 1, size = 10) {
  return request({
    url: `/clubs/${clubId}/members`,
    method: 'get',
    params: { page, size }
  })
}

/**
 * 添加社团成员
 */
export function addClubMember(clubId, data) {
  return request({
    url: `/clubs/${clubId}/members`,
    method: 'post',
    data
  })
}

/**
 * 更新社团成员身份或状态
 */
export function updateClubMember(clubId, userId, data) {
  return request({
    url: `/clubs/${clubId}/members/${userId}`,
    method: 'put',
    data
  })
}

/**
 * 移除社团成员
 */
export function removeClubMember(clubId, userId) {
  return request({
    url: `/clubs/${clubId}/members/${userId}`,
    method: 'delete'
  })
}

/**
 * 获取社团活动
 */
export function getClubActivities(clubId, page = 1, size = 10) {
  return request({
    url: `/clubs/${clubId}/activities`,
    method: 'get',
    params: { page, size }
  })
}
