package com.yikekong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yikekong.entity.QuotaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuotaMapper extends BaseMapper<QuotaEntity>{

    /**
     * Search the list of indicator configurations by topic
     * @param subject
     * @return
     */
    @Select("select * from tb_quota where subject=#{subject} ")
    List<QuotaEntity> selectBySubject(String subject);


}