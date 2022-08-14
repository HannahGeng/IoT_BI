package com.yikekong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yikekong.entity.BoardEntity;

public interface BoardService extends IService<BoardEntity>{

    /**
     * delete board
     * @param boardId
     * @return
     */
    Boolean disable(Integer boardId);
}
