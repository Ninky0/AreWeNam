package org.example.shoppingweather.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.shoppingweather.entity.Customer;

@Mapper
public interface CustomerMapper {
    void signUp(Customer customer);
    Customer signIn(String login_id);
}
