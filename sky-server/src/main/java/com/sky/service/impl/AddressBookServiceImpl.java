package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     *
     * @param addressBook
     */
    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.insert(addressBook);
    }

    /**
     * 查询当前登录用户的所有地址信息
     *
     * @return
     */
    @Override
    public List<AddressBook> queryAddress(AddressBook addressBook) {
        return addressBookMapper.queryByCondition(addressBook);
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.delete(id);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     */
    @Override
    public void setDefault(AddressBook addressBook) {

        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);

        //将当前用户所有地址改为非默认地址
        addressBookMapper.updateDefaultByUserId(addressBook);

        //将地址改为默认地址
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }
}
