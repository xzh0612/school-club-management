import request from '../utils/request'

export function getOperationLogs(params = {}) {
  return request({
    url: '/logs',
    method: 'get',
    params
  })
}
