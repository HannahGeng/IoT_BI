package com.yikekong.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.yikekong.dto.*;
import com.yikekong.entity.AlarmEntity;
import com.yikekong.influx.InfluxRepository;
import com.yikekong.mapper.AlarmMapper;
import com.yikekong.service.AlarmService;
import com.yikekong.vo.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class AlarmServiceImpl extends ServiceImpl<AlarmMapper, AlarmEntity> implements AlarmService{


    @Override
    public IPage<AlarmEntity> queryPage(Long page,Long pageSize,String alarmName, Integer quotaId) {
        LambdaQueryWrapper<AlarmEntity> wrapper = new LambdaQueryWrapper<>();
        if(!Strings.isNullOrEmpty(alarmName)){
            wrapper.like(AlarmEntity::getName,alarmName);
        }
        if(quotaId != null){
            wrapper.eq(AlarmEntity::getQuotaId,quotaId);
        }
        wrapper.orderByDesc(AlarmEntity::getId);

        //wrapper.orderByDesc(AlarmEntity::getCreateTime);

        Page<AlarmEntity> pageResult = new Page<>(page,pageSize);

        return this.page(pageResult,wrapper);
    }

    @Override
    public List<AlarmEntity> getByQuotaId(Integer quotaId) {
        QueryWrapper<AlarmEntity> wrapper = new QueryWrapper<>();
        wrapper
                .lambda()
                .eq(AlarmEntity::getQuotaId,quotaId)
                .orderByDesc(AlarmEntity::getLevel);

        return this.list(wrapper);
    }

    @Override
    public AlarmEntity verifyQuota(QuotaDTO quotaDTO) {

        //1.Query the list of alarm judgment rules according to indicator id
        List<AlarmEntity> alarmEntityList = getByQuotaId(quotaDTO.getId());
        AlarmEntity alarm=null;
        for( AlarmEntity alarmEntity:alarmEntityList ){
            //Judgment: Comparison of Operators and Indicators
            if( "String".equals( quotaDTO.getValueType() ) || "Boolean".equals(quotaDTO.getValueType())  ){
                if(  alarmEntity.getOperator().equals("=")  &&  quotaDTO.getStringValue().equals(alarmEntity.getThreshold()) ){
                    alarm=alarmEntity;
                    break;
                }
            }else //Numerical value
            {
                if(  alarmEntity.getOperator().equals(">")  &&  quotaDTO.getValue()>alarmEntity.getThreshold() ){
                    alarm=alarmEntity;
                    break;
                }
                if(  alarmEntity.getOperator().equals("<")  &&  quotaDTO.getValue()<alarmEntity.getThreshold() ){
                    alarm=alarmEntity;
                    break;
                }
                if(  alarmEntity.getOperator().equals("=")  &&  quotaDTO.getValue().equals(alarmEntity.getThreshold()) ){
                    alarm=alarmEntity;
                    break;
                }
            }
        }

        return alarm;
    }

    @Override
    public DeviceInfoDTO verifyDeviceInfo(DeviceInfoDTO deviceInfoDTO) {

        // Alarms for packaging indicators Alarms for packaging devices
        DeviceDTO deviceDTO = deviceInfoDTO.getDevice();

        deviceDTO.setLevel(0);//Assuming no alarms
        deviceDTO.setAlarm(false);
        deviceDTO.setAlarmName("normal");
        deviceDTO.setStatus(true);
        deviceDTO.setOnline(true);

        for(QuotaDTO quotaDTO :deviceInfoDTO.getQuotaList() ){

            AlarmEntity alarmEntity = verifyQuota(quotaDTO);//Get alert messages based on indicators
            if(alarmEntity!=null){  //If an alarm exists for an indicator

                quotaDTO.setAlarm("1");
                quotaDTO.setAlarmName( alarmEntity.getName() );//Alarm name
                quotaDTO.setLevel( alarmEntity.getLevel()+"" );//Alarm level
                quotaDTO.setAlarmWebHook(alarmEntity.getWebHook());//Alert web hooks
                quotaDTO.setCycle( alarmEntity.getCycle() );//Cycle of Silence

                if(alarmEntity.getLevel().intValue()> deviceDTO.getLevel().intValue() ){

                    deviceDTO.setLevel( alarmEntity.getLevel() );
                    deviceDTO.setAlarm(true);
                    deviceDTO.setAlarmName(alarmEntity.getName());
                }

            }else{//If the indicator is not stored in the alarm
                quotaDTO.setAlarm("0");
                quotaDTO.setAlarmName("normal");
                quotaDTO.setLevel("0");
                quotaDTO.setAlarmWebHook("");
                quotaDTO.setCycle(0);
            }
        }
        return deviceInfoDTO;
    }

    @Autowired
    private InfluxRepository influxRepository;

    @Override
    public Pager<QuotaAllInfo> queryAlarmLog(Long page, Long pageSize, String start, String end, String alarmName, String deviceId) {


        //1.Where conditional query statements are partially constructed

        StringBuilder whereQl=new StringBuilder("where alarm='1' ");
        if(!Strings.isNullOrEmpty(start)){
            whereQl.append("and time>='"+start +"' ");
        }
        if(!Strings.isNullOrEmpty(end)){
            whereQl.append("and time<='"+end +"' ");
        }
        if(!Strings.isNullOrEmpty(alarmName)){
            whereQl.append("and alarmName=~/"+ alarmName+"/ ");
        }
        if(!Strings.isNullOrEmpty(deviceId)){
            whereQl.append("and deviceId=~/^"+deviceId+"/ ");
        }

        //2.Query Record Statement
        StringBuilder listQl=new StringBuilder("select * from quota  ");
        listQl.append( whereQl.toString() );
        listQl.append( "order by desc limit "+ pageSize+" offset "+ (page-1)*pageSize   );


        //3.Query Record Count Statement
        StringBuilder countQl=new StringBuilder("select count(value) from quota ");
        countQl.append(whereQl.toString());


        //4.Execute the query record statement
        List<QuotaAllInfo> quotaList = influxRepository.query(listQl.toString(), QuotaAllInfo.class);
        // Add time format processing
        for(QuotaAllInfo quotaAllInfo:quotaList){
            //2020-09-19T09:58:34.926Z   DateTimeFormatter.ISO_OFFSET_DATE_TIME
            //transfer to 2020-09-19 09:58:34
            LocalDateTime dateTime = LocalDateTime.parse(quotaAllInfo.getTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String time = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
            quotaAllInfo.setTime(time);
        }

        //5.Execute statistical statements
        List<QuotaCount> quotaCount = influxRepository.query(countQl.toString(), QuotaCount.class);

        //6.Wrapping return results
        if(quotaCount==null || quotaCount.size()==0){
            Pager<QuotaAllInfo> pager=new Pager<QuotaAllInfo>(0L,0L);
            pager.setPage(0);
            pager.setItems(Lists.newArrayList());
            return pager;
        }


        Long totalCount = quotaCount.get(0).getCount();//Total number of records

        Pager<QuotaAllInfo> pager=new Pager<>(totalCount,pageSize);
        pager.setPage(page);
        pager.setItems(quotaList);

        return pager;
    }


}
