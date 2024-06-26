package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 新增地址
     * @param addressBook
     */
    @Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label) " +
            "values (#{userId} ,#{consignee} ,#{sex} ,#{phone} ,#{provinceCode} ,#{provinceName} ,#{cityCode} ,#{cityName} ,#{districtCode} ,#{districtName} ,#{detail} ,#{label} )")
    void insert(AddressBook addressBook);

    /**
     * 条件查询
     * @param addressBook
     * @return
     */
    List<AddressBook> queryByCondition(AddressBook addressBook);

    /**
     * 根据id修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    @Delete("delete from address_book where id=#{id} ")
    void delete(Long id);

    /**
     * 通过用户id修改
     * @param addressBook
     */
    @Update("update address_book set is_default = #{isDefault}  where user_id = #{userId}  ")
    void updateDefaultByUserId(AddressBook addressBook);

    /**
     * 通过id查询地址
     * @param addressBookId
     * @return
     */
    @Select("select * from address_book where id=#{addressBookId} ")
    AddressBook getById(Long addressBookId);
}
