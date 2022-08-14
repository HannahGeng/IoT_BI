import request from '@/utils/request'

//Get System Panel
export const boardSystemBoard = () =>
    request({
        url: '/api/board/systemBoard',
        method: 'get',
    })
//Changing the System Panel
export const boardStatus = (params:any) =>
request({
    url: '/api/board/status',
    method: 'put',
    data:params
})
    
// Add Panel   
export const addBoard = (params:any) =>
request({
    url: `/api/board`,
    method: 'post',
    data:params
}) 
// Delete Panel    
export const delBoard = (id:number) =>
request({
    url: `/api/board/${id}`,
    method: 'delete',
})    
// Get non-system panels
export const getUnSystemPanel = () =>
    request({
        url: '/api/board',
        method: 'get',
    })
// Number of monitoring devices - number of devices and number of alarm devices
export const monitor = () =>
    request({
        url: '/api/report/monitor',
        method: 'get',
    })
// Device Status Distribution - Home Loop Data
export const statusCollect = (params: any) =>
    request({
        url: '/api/report/statusCollect',
        method: 'get',
        params
    })
// Map - Get device information based on latitude and longitude
// export const deviceList = (params: any) =>
//     request({
//         url: `/api/gps/deviceList/${params.lat}/${params.lon}/${params.distance}`,
//         method: 'get',
//         params
//     })
// // Map - Find device details
// export const deviceInfo = (params: any) =>
//     request({
//         url: '/api/device/deviceInfo/:deviceId',
//         method: 'get',
//         params
//     })
// Today's abnormal equipment
// Anomaly Trend Graph
export const trendDatas = (params: any) =>
    request({
        url: `/api/report/trend/${params.startTime}/${params.endTime}/${params.type}`,
        method: 'get',
        params
    })
    
export const top10Alarm = (params: any) =>
    request({
        url: `/api/report/top10Alarm/${params.startTime}/${params.endTime}`,
        method: 'get',
        params
    })
 
export const boardList = (params: any) =>
    request({
        url: '/board',
        method: 'get'
    })

export const boardData = (params: any) =>
    request({
        url: `/api/report/boardData/${params.id}/${params.startTime}/${params.endTime}/${params.type}`,
        method: 'get',
        params
    })

export const quotaNumberQuota = () =>
    request({
        url: `/api/quota`,
        method: 'get',
    })

export const quotaDevices = (params: any) =>
    request({
        url: `/api/report/devices`,
        method: 'get',
        params
    })

export const board = (data: any) =>
    request({
        url: '/api/board',
        method: 'post',
        data
    })

export const preview = (data: any) =>
    request({
        url: '/api/report/preview',
        method: 'post',
        data
    })
