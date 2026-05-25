package com.takeout.mapper;

import com.takeout.entity.UserAccount;
import org.apache.ibatis.annotations.Param;

public interface UserAccountMapper {
    UserAccount findByUsername(@Param("username") String username);
    int insert(UserAccount account);
}
