import request from '../utils/request'

export function getApplicationList(params = {}) {
  return request({
    url: '/applications',
    method: 'get',
    params
  })
}

export function submitApplication(clubId, introduction, recruitmentId = null) {
  return request({
    url: '/applications',
    method: 'post',
    data: { clubId, recruitmentId, introduction: introduction || '' }
  })
}

export function approveJoinApplication(id, comments = '') {
  return request({
    url: `/applications/${id}/approve`,
    method: 'post',
    data: { comments }
  })
}

export function rejectJoinApplication(id, comments = '') {
  return request({
    url: `/applications/${id}/reject`,
    method: 'post',
    data: { comments }
  })
}
