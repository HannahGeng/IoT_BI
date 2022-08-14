package com.yikekong.controller;

import com.google.common.collect.Lists;
import com.yikekong.dto.HeapPoint;
import com.yikekong.dto.TrendPoint;
import com.yikekong.es.ESRepository;
import com.yikekong.service.ReportService;
import com.yikekong.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {


    @Autowired
    private ReportService reportService;

    @Autowired
    private ESRepository esRepository;

    /**
     * Device Status Distribution
     * @return
     */
    @GetMapping("/statusCollect")
    public List<PieVO> getStatusCollect(){
        return  reportService.getStatusCollect();
    }


    /**
     * Real-time monitoring data
     * @return
     */
    @GetMapping("/monitor")
    public MonitorVO getMonitorData(){
        MonitorVO monitorVO=new MonitorVO();
        monitorVO.setDeviceCount( esRepository.getAllDeviceCount() );
        monitorVO.setAlarmCount( esRepository.getAlarmCount());
        return monitorVO;
    }


    /**
     * Get alarm trends
     * @return
     */
    @GetMapping("/trend/{startTime}/{endTime}/{type}")
    public LineVO getQuotaTrendCollect(@PathVariable String startTime,@PathVariable String endTime,@PathVariable Integer type){

        List<TrendPoint> trendPointList = reportService.getAlarmTrend(startTime, endTime, type);

        LineVO lineVO=new LineVO();
        lineVO.setXdata(Lists.newArrayList());
        lineVO.setSeries(Lists.newArrayList());

        trendPointList.forEach( t->{
            lineVO.getXdata().add( formatTime(t.getTime(),type) );
            lineVO.getSeries().add( t.getPointValue().longValue());
        });

        return lineVO;
    }


    /**
     * Formatting date strings
     * @param time
     * @param type  1：minute  2：hour  3：day
     * @return
     */
    private String formatTime(String time,int type){
        LocalDateTime localDateTime=LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME );
        if(type==1){
            return  localDateTime.getMinute()+"";
        }
        if(type==2){
            return    localDateTime.getHour()+"";
        }
        if(type==3){
            return localDateTime.getMonthValue()+"month"+localDateTime.getDayOfMonth()+"day";
        }
        return time;
    }


    /**
     * top10 alarm
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/top10Alarm/{startTime}/{endTime}")
    public List<HeapPoint> getTop10Alarm(@PathVariable String startTime, @PathVariable String endTime ){
        return reportService.getTop10Alarm(startTime,endTime);
    }


    /**
     * Check the list of devices by indicator
     * @param page
     * @param pageSize
     * @param quotaId
     * @return
     */
    @GetMapping("/devices")
    public Pager<String> getDeviceByQuota(
            @RequestParam(value = "page",required = false,defaultValue = "1") Long page,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Long pageSize,
            @RequestParam(value = "quotaId") String quotaId ){

        return  reportService.getDeviceByQuota( page,pageSize,quotaId );

    }

    /**
     * Report Preview
     * @param previewVO
     * @return
     */
    @PostMapping("/preview")
    public BoardQuotaVO getPreviewData( @RequestBody PreviewVO previewVO ){
        BoardQuotaVO boardData = reportService.getBoardData(
                previewVO.getQuotaId(), previewVO.getDeviceIdList(), previewVO.getStart(), previewVO.getEnd(), previewVO.getType());

        //Time Processing
        List<String> xdata=Lists.newArrayList();

        for(String x:boardData.getXdata()){
            xdata.add(formatTime(x,previewVO.getType() ))  ;
        }
        boardData.setXdata(xdata);

        return boardData;
    }


    /**
     * Get board data
     * @param id
     * @param start
     * @param end
     * @param type
     * @return
     */
    @GetMapping("/boardData/{id}/{start}/{end}/{type}")
    public BoardQuotaVO getBoardData(@PathVariable Integer id,
                                     @PathVariable String start,
                                     @PathVariable String end,
                                     @PathVariable Integer type){
        return reportService.getBoardData(id,start,end,type);
    }

}
