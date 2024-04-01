package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userAddressBookController")
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "地址簿相关接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;
    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增地址")
    public Result save(@RequestBody AddressBook addressBook){
        log.info("新增地址:{}",addressBook);
        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> allAddress(){
        log.info("查询当前登录用户的所有地址信息");

        AddressBook addressBook = AddressBook.builder().userId(BaseContext.getCurrentId()).build();
        List<AddressBook> addressBookList = addressBookService.queryAddress(addressBook);
        return Result.success(addressBookList);
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    @ApiOperation(value = "查询默认地址")
    public Result<AddressBook> getDefault(){
        log.info("查询默认地址");

        AddressBook addressBook = AddressBook.builder().userId(BaseContext.getCurrentId()).isDefault(1).build();
        List<AddressBook> addressBookList = addressBookService.queryAddress(addressBook);

        if (addressBookList != null)
            return Result.success(addressBookList.get(0));

        return Result.error("没有查询到默认地址");
    }

    /**
     * 根据id修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation(value = "根据id修改地址")
    public Result update(@RequestBody AddressBook addressBook){
        log.info("根据id修改地址");
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "根据地址id删除地址")
    public Result deleteById(Long id){
        log.info("根据id删除地址");
        addressBookService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据id查询地址
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据地址id查询地址")
    public Result<AddressBook> queryAddressById(@PathVariable Long id){
        log.info("根据id查询地址");
        AddressBook addressBook = AddressBook.builder().id(id).build();
        List<AddressBook> addressBookList = addressBookService.queryAddress(addressBook);

        return Result.success(addressBookList.get(0));
    }

    /**
     * 设置默认地址
     * @param id
     * @return
     */
    @PutMapping("/default")
    @ApiOperation(value = "设置默认地址")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook){
        log.info("设置默认地址");
        addressBookService.setDefault(addressBook);
        return Result.success();
    }
}
