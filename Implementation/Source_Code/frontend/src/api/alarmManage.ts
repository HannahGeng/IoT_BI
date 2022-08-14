import request from '@/utils/request'

// Get alarm logs
export const getAlarmLog = (params: any) =>
    request({
        url: '/api/alarm/log',
        method: 'get',
        params
    })

export const alarm = (params: any) =>
    request({
        url: '/api/alarm',
        method: 'get',
        params
    })
export const addAlarm = (data: any) =>
    request({
        url: '/api/alarm',
        method: 'post',
        data
    })
export const putAlarm = (data: any) =>
    request({
        url: '/api/alarm',
        method: 'put',
        data
    })
export const delAlarm = (data: any) =>
    request({
        url: `/api/alarm/${data}`,
        method: 'delete',
    })
