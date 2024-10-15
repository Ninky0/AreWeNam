package org.example.shoppingweather.arewenam.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.shoppingweather.arewenam.model.Member;

@Mapper
public interface MemberMapper {
    void signUp(Member member);
    Member signIn(String userId);
}
