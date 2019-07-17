package cc.lijingbo.ssm.mapper;

import java.util.List;

import cc.lijingbo.ssm.pojo.User;

public interface UserMapper {
    User quertUserById(int id);

    List<User> queryUserByUserName(String username);

    void saveUser(User user);
}
