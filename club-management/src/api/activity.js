import request from '../utils/request'

/**
 * 获取活动列表
 */
export function getActivityList(page = 1, size = 10, status = '', clubId = '') {
  return request({
    url: '/activities',
    method: 'get',
    params: { page, size, status, clubId }
  })
}

/**
 * 获取活动详情
 */
export function getActivityDetail(id) {
  return request({
    url: `/activities/${id}`,
    method: 'get'
  })
}

/**
 * 创建活动
 */
export function createActivity(data) {
  return request({
    url: '/activities',
    method: 'post',
    data
  })
}

/**
 * 更新活动
 */
export function updateActivity(id, data) {
  return request({
    url: `/activities/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除活动
 */
export function deleteActivity(id) {
  return request({
    url: `/activities/${id}`,
    method: 'delete'
  })
}

/**
 * 搜索活动
 */
export function searchActivities(keyword, page = 1, size = 10, status = '') {
  return request({
    url: '/activities/search',
    method: 'get',
    params: { keyword, page, size, status }
  })
}

/**
 * 活动报名
 */
export function signupActivity(activityId, data) {
  return request({
    url: `/activities/${activityId}/signup`,
    method: 'post',
    data
  })
}

/**
 * 取消报名
 */
export function cancelSignup(activityId, userId) {
  return request({
    url: `/activities/${activityId}/signup`,
    method: 'delete',
    params: { userId }
  })
}

/**
 * 获取活动报名人员
 */
export function getActivitySignups(activityId, page = 1, size = 10) {
  return request({
    url: `/activities/${activityId}/signups`,
    method: 'get',
    params: { page, size }
  })
}

/**
 * 审核活动报名
 */
export function updateSignupStatus(activityId, signupId, status) {
  return request({
    url: "/activities/" + activityId + "/signups/" + signupId + "/status",
    method: 'put',
    data: { status }
  })
}

/**
 * 活动签到
 */
export function checkinSignup(activityId, signupId) {
  return request({
    url: "/activities/" + activityId + "/signups/" + signupId + "/checkin",
    method: 'post'
  })
}
