import request from '../utils/request'

export function getRecruitmentPlans(params = {}) {
  return request({
    url: '/recruitment-plans',
    method: 'get',
    params
  })
}

export function createRecruitmentPlan(data) {
  return request({
    url: '/recruitment-plans',
    method: 'post',
    data
  })
}

export function updateRecruitmentPlan(id, data) {
  return request({
    url: `/recruitment-plans/${id}`,
    method: 'put',
    data
  })
}

export function deleteRecruitmentPlan(id) {
  return request({
    url: `/recruitment-plans/${id}`,
    method: 'delete'
  })
}
