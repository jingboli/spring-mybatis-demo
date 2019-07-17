# spring 整合 mybatis demo

## 思路
1. 数据库连接池交给 Spring 管理
2. SqlSessionFactory 交给 Spring 管理
3. 从 Spring 容器中直接获得 mapper 的代理对象

## 步骤
1. 创建工程
2. 导入 jar
3. 创建 config 文件夹，放置配置文件
   * 配置文件：
        * jdbc.properties : 数据库配置
            ```xml
            jdbc.driverClass=com.mysql.cj.jdbc.Driver
            jdbc.url=jdbc:mysql://localhost:3306/jdbc?serverTimezone=UTC
            jdbc.username=root
            jdbc.password=root
            ```
        * log4j.properties ：日志打印
        * mybatis_config.xml：myBatis 配置,只需要配置二级缓存就可以了，其他都交给 Spring 处理。
            ```xml
            <?xml version="1.0" encoding="UTF-8" ?><!DOCTYPE configuration    PUBLIC "-//mybatis.org//DTD Config 3.0//EN"    "http://mybatis.org/dtd/mybatis-3-config.dtd">
            <configuration>    <!-- 开启二级缓存 -->      
                <settings>        
                    <setting name="cacheEnabled" value="true" />    
                </settings>
            </configuration>
            ```
        * applicationContext.xml：Spring 配置文件
            ```xml
            <?xml version="1.0" encoding="UTF-8"?><beans xmlns="http://www.springframework.org/schema/beans"    
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"    
                xmlns:aop="http://www.springframework.org/schema/aop"    
                xmlns:tx="http://www.springframework.org/schema/tx"    
                xmlns:context="http://www.springframework.org/schema/context" 
                    xsi:schemaLocation="                           
                    http://www.springframework.org/schema/beans                            
                    http://www.springframework.org/schema/beans/spring-beans.xsd                            
                    http://www.springframework.org/schema/context                            
                    http://www.springframework.org/schema/context/spring-context.xsd                            
                    http://www.springframework.org/schema/aop                            
                    http://www.springframework.org/schema/aop/spring-aop.xsd                            
                    http://www.springframework.org/schema/tx                            
                    http://www.springframework.org/schema/tx/spring-tx.xsd">    
                <!-- 加载配置文件 -->    
                <context:property-placeholder location="classpath:config/jdbc.properties" />    
                <!-- 数据库连接池 -->    
                <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">        
                    <property name="driverClassName" 
                    value="${jdbc.driverClass}" />        
                    <property name="url" value="${jdbc.url}" />        
                    <property name="username" value="${jdbc.username}" />        
                    <property name="password" value="${jdbc.password}" />        
                    <property name="maxActive" value="10" />        
                    <property name="maxIdle" value="5" />    
                </bean>    
                <!-- 配置 sqlSessionFactory -->    
                <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">        
                    <!-- 配置 mybatis 核心配置文件 -->        
                    <property name="configLocation" value="classpath:config/mybatis_config.xml" />        
                    <!-- 配置数据源 -->        
                    <property name="dataSource" ref="dataSource" />        
                </bean>    
            </beans>
            ```
4. DAO 开发
    1.  创建 POJO  User.java
        ```java
        public class User {    
            private Integer id;    
            private String username;    
            private Date birthday;    
            private String address;    
            private boolean sex;    
            public boolean isSex() {        
            return sex;   
            }   
            public void setSex(boolean sex) {      
             this.sex = sex;   
            }   
            public Integer getId() {        
             return id;   
            }    
            public void setId(Integer id) {      
              this.id = id;  
            }   
            public String getUsername() {       
             return username;  
            }   
            public void setUsername(String username) {      
             this.username = username;   
            }   
            public Date getBirthday() {     
             return birthday;  
            }   
            public void setBirthday(Date birthday) {     
             this.birthday = birthday;   
            }   
            public String getAddress() {    
              return address;   
            }   
            public void setAddress(String address) {      
             this.address = address;  
            }    
            @Override    
            public String toString() {       
             return "User{" +                "id=" + id +                ", username='" + username + '\'' +                ", birthday=" + birthday +                ", address='" + address + '\'' +                ", sex=" + sex +                '}';    
            }
        }
        ```
    2. 在 applicatonContext.xml 中配置别名扫描
    ![e80e05d0437f77fd2b0b25e3461a1025.png](en-resource://database/10165:0)
    
    3.  实现 UserMapper 接口
        ```java
        public interface UserMapper {    
            User quertUserById(int id);   

            List<User> queryUserByUserName(String username);   

            void saveUser(User user);
        }
        ```
    4.  实现 UserMapper.xml 配置文件
        ```xml
        <?xml version="1.0" encoding="UTF-8"?><!DOCTYPE mapper PUBLIC 
        "-//mybatis.org//DTD Mapper 3.0//EN"    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
        <mapper namespace="cc.lijingbo.ssm.mapper.UserMapper">    
            <select id="quertUserById" parameterType="int" resultType="user">        
                select * from user where id = #{id}    
            </select>    
            <select id="queryUserByUserName" resultType="user" parameterType="String">        
                select * from user where username like '%${value}%'    
            </select>    
            <insert id="saveUser" parameterType="user">        
                <selectKey keyProperty="id" keyColumn="id" order="AFTER"   resultType="int">            
                select last_insert_id()        
                </selectKey>        
                insert into user (username,sex,address) values (#{username},#{sex},#{address})    
            </insert>
        </mapper>
        ```
    5.  在 applicationContext.xml 中配置 mapper 扫描
    ![bfaac79b7f67c47b95aba96d6f649cb0.png](en-resource://database/10167:0)
    6.  测试
        ```java
        public class UserMapperTest {   
            ApplicationContext applicationContext;    
            @Before    
            public void setUp() throws Exception {       
                applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
            }    
            @Test    
            public void quertUserById() {       
                UserMapper userMapper = 
                applicationContext.getBean(UserMapper.class);       
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
        ```
    
> [源码 github 地址](https://github.com/jingboli/spring-mybatis-demo.git)
