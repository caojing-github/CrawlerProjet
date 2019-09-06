package com.service.impl;

import com.dao.ItemDao;
import com.pojo.Item;
import com.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemDaoImpl implements ItemService {

    @Autowired
    private ItemDao itemDao;

    @Override
    public List<Item> findAll(Item item) {
        return itemDao.findAll(Example.of(item));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Item item) {
        itemDao.save(item);
    }

}
