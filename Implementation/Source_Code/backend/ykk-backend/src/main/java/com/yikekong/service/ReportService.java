package com.yikekong.service;

import com.yikekong.dto.HeapPoint;
import com.yikekong.dto.TrendPoint;
import com.yikekong.dto.TrendPoint2;
import com.yikekong.vo.BoardQuotaVO;
import com.yikekong.vo.Pager;
import com.yikekong.vo.PieVO;

import java.util.List;

/**
 * 报表服务
 */
public interface ReportService {


    /**
     * getStatusCollect
     * @return
     */
    List<PieVO> getStatusCollect();


    /**
     * getAlarmTrend
     * @param start
     * @param end
     * @param type
     * @return
     */
    List<TrendPoint> getAlarmTrend(String start,String end,int type);


    /**
     * getTop10Alarm
     * @param startTime
     * @param endTime
     * @return
     */
    List<HeapPoint> getTop10Alarm( String startTime,String endTime );


    /**
     * getDeviceByQuota
     * @param page
     * @param pageSize
     * @param quotaId
     * @return
     */
    Pager<String> getDeviceByQuota(Long page,Long pageSize,String quotaId);


    /**
     * getQuotaTrend
     * @param startTime
     * @param endTime
     * @param quotaId
     * @param deviceId
     * @param type
     * @return
     */
    List<TrendPoint2> getQuotaTrend( String startTime,String endTime,String quotaId,String deviceId,int type  );


    /**
     * getBoardData
     * @param quotaId
     * @param deviceIds
     * @param startTime
     * @param endTime
     * @param type
     * @return
     */
    BoardQuotaVO getBoardData(String quotaId,List<String> deviceIds,String startTime,String endTime,Integer type  );


    /**
     * getBoardData
     * @param id
     * @param start
     * @param end
     * @param type
     * @return
     */
    BoardQuotaVO getBoardData(Integer id, String start, String end, Integer type);



}
