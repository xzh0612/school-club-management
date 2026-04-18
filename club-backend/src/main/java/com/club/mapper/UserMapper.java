package com.club.mapper;

import com.club.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User findById(Integer userId);
    
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT * FROM users WHERE role = #{role} LIMIT #{offset}, #{limit}")
    List<User> findByRole(@Param("role") String role, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM users WHERE role = #{role}")
    int countByRole(String role);
    
    @Select("SELECT * FROM users LIMIT #{offset}, #{limit}")
    List<User> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM users")
    int countAll();
    
    @Insert("INSERT INTO users(username, password, real_name, role, email, phone) VALUES(#{username}, #{password}, #{realName}, #{role}, #{email}, #{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);
    
    @Update("UPDATE users SET username = #{username}, real_name = #{realName}, role = #{role}, email = #{email}, phone = #{phone} WHERE user_id = #{userId}")
    int update(User user);
    
    @Delete("DELETE FROM users WHERE user_id = #{userId}")
    int deleteById(Integer userId);
    
    @Select("SELECT * FROM users WHERE role != 'admin' AND (username LIKE CONCAT('%',#{keyword},'%') OR real_name LIKE CONCAT('%',#{keyword},'%')) LIMIT #{offset}, #{limit}")
    List<User> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM users WHERE role != 'admin' AND (username LIKE CONCAT('%',#{keyword},'%') OR real_name LIKE CONCAT('%',#{keyword},'%'))")
    int searchCount(String keyword);
}