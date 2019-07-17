package cc.lijingbo.ssm.mapper;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;

import cc.lijingbo.ssm.pojo.User;

public class UserMapperTest {

    ApplicationContext applicationContext;

    @Before
    public void setUp() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    @Test
    public void quertUserById() {
        UserMapper userMapper = applicationContext.getBean(UserMapper.class);
        User user = userMapper.quertUserById(3);
        System.out.println(user);
    }

    @Test
    public void queryUserByUserName() {
        UserMapper userMapper = applicationContext.getBean(UserMapper.class);
        List<User> users = userMapper.queryUserByUserName("张");
        for (User u : users) {
            System.out.println(u);
        }
    }

    @Test
    public void saveUser() {
        UserMapper userMapper = applicationContext.getBean(UserMapper.class);
        User user = new User();
        user.setUsername("刘备");
        user.setAddress("深圳XXX");
        user.setSex(false);
        userMapper.saveUser(user);
    }
}