package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    @Insert("insert into order_detail (name, image, order_id, dish_id, setmeal_id, dish_flavor, amount) " +
            "values (#{name} ,#{image} ,#{orderId} ,#{dishId} ,#{setmealId} ,#{dishFlavor} ,#{amount} )")
    void insert(OrderDetail orderDetail);

    /**
     * 根据order_id查询订单详情
     * @param id
     * @return
     */
    @Select("select * from order_detail where order_id=#{id} ")
    List<OrderDetail> getByOrderId(Long id);

    /**
     * 插入多条数据
     * @param orderDetailList
     */
    void insertBatch(List<OrderDetail> orderDetailList);


}
