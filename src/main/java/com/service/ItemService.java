package com.service;

import com.pojo.Item;

import java.util.List;

public interface ItemService {

    /**
     * 根据条件查询数据
     */
    List<Item> findAll(Item item);

    /**
     * 保存数据
     */
    void save(Item item);

}
