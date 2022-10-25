package com.nju.edu.erp.service.Impl;

import com.auth0.jwt.interfaces.Claim;
import com.nju.edu.erp.config.JwtConfig;
import com.nju.edu.erp.dao.UserDao;
import com.nju.edu.erp.enums.Role;
import com.nju.edu.erp.exception.MyServiceException;
import com.nju.edu.erp.model.po.User;
import com.nju.edu.erp.model.vo.UserVO;
import com.nju.edu.erp.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final JwtConfig jwtConfig;

    @Autowired
    public UserServiceImpl(UserDao userDao, JwtConfig jwtConfig) {
        this.userDao = userDao;
        this.jwtConfig = jwtConfig;
    }


    @Override
    public Map<String, String> login(UserVO userVO) {
        User user = userDao.findByUsernameAndPassword(userVO.getName(), userVO.getPassword());
        if (null == user ) {
            throw new MyServiceException("A0000", "用户名或密码错误");
        }
        Map<String, String> authToken = new HashMap<>();
        String token = jwtConfig.createJWT(user);
        authToken.put("token", token);
        return authToken;
    }

    @Override
    public void register(UserVO userVO) {
        User user = userDao.findByUsername(userVO.getName());
        if (user != null) {
            throw new MyServiceException("A0001", "用户名已存在");
        }
        User userSave = new User();
        BeanUtils.copyProperties(userVO, userSave);
        userDao.createUser(userSave);
    }

    @Override
    public UserVO auth(String token) {
        Map<String, Claim> claims = jwtConfig.parseJwt(token);
        UserVO userVO = UserVO.builder()
                .name(claims.get("name").as(String.class))
                .role(Role.valueOf(claims.get("role").as(String.class)))
                .build();
        return userVO;
    }

    @Override
    public String checkIN(String id) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Calendar nowTime = Calendar.getInstance();
        nowTime.setTime(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(simpleDateFormat.parse("07:30"));
        System.out.println(nowTime.getTime());
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = simpleDateFormat2.format(date);
        if (userDao.getLastCardDate(id) == null || !userDao.getLastCardDate(id).equals(today)) {
            userDao.update(today, id);
            if (nowTime.after(beginTime)) {
                System.out.println("late");
                userDao.checkInLate(id);
                return "late";
            } else {
                userDao.checkIn(id);
                return "onTime";
            }
        } else {
            return "did";
        }
    }
}
