import request from '../utils/request'

/**
 * 获取审批列表
 */
export function getApprovalList(params) {
  return request({
    url: '/approvals',
    method: 'get',
    params
  })
}

/**
 * 获取审批详情
 */
export function getApprovalDetail(id) {
  return request({
    url: `/approvals/${id}`,
    method: 'get'
  })
}

/**
 * 获取审批历史
 */
export function getApprovalHistories(id) {
  return request({
    url: `/approvals/${id}/histories`,
    method: 'get'
  })
}

/**
 * 创建审批
 */
export function createApproval(data) {
  return request({
    url: '/approvals',
    method: 'post',
    data
  })
}

/**
 * 通过审批
 */
export function approveApplication(id, comments = '') {
  return request({
    url: `/approvals/${id}/approve`,
    method: 'post',
    data: { comments }
  })
}

/**
 * 驳回审批
 */
export function rejectApplication(id, comments = '') {
  return request({
    url: `/approvals/${id}/reject`,
    method: 'post',
    data: { comments }
  })
}

/**
 * 归档审批
 */
export function archiveApproval(id) {
  return request({
    url: `/approvals/${id}/archive`,
    method: 'post'
  })
}
