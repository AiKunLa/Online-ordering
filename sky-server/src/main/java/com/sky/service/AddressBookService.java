package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 新增地址
     * @param addressBook
     */
    void save(AddressBook addressBook);

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    List<AddressBook> queryAddress(AddressBook addressBook);

    /**
     * 根据id修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    void deleteById(Long id);

    /**
     * 设置默认地址
     * @param id
     */
    void setDefault(AddressBook addressBook);
}
