package com.yikekong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yikekong.entity.AdminEntity;

public interface AdminService extends IService<AdminEntity>{
    /**
     * Login
     * @param loginName
     * @param password
     * @return
     */
    Integer login(String loginName,String password);
}
