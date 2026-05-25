package com.takeout.mapper;

import com.takeout.entity.UserProfile;
import org.apache.ibatis.annotations.Param;

public interface UserProfileMapper {
    UserProfile findById(@Param("id") String id);
    int insert(UserProfile profile);
    int update(UserProfile profile);
}
