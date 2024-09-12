package com.yk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yk.common.BaseResponse;
import com.yk.common.ErrorCode;
import com.yk.common.ResultUtils;
import com.yk.exception.BusinessException;
import com.yk.model.domain.User;
import com.yk.model.domain.register.UserLoginRequest;
import com.yk.model.domain.register.UserRegisterRequest;
import com.yk.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yk.constant.UserConstant.ADMIN_ROLE;
import static com.yk.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Api(tags = "对于用户的增删查")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "用户的注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }


    @PostMapping("/login")
    @ApiOperation(value = "用户的登录")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        User result = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        Long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    @ApiOperation(value = "根据用户名查询用户")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        //权限校验,只有管理员可以操作
        if (!is_Admin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH,"权限不足");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);

    }

    //逻辑删除
    @PostMapping("/delete")
    @ApiOperation(value = "用户的逻辑删除")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        //权限校验,只有管理员可以操作
        if (is_Admin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH,"权限不足");
        }
        if (id < 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"参数为空");
        }
        boolean remove = userService.removeById(id);
        return ResultUtils.success(remove);
    }

    //用户注销
    @PostMapping("/logout")
    @ApiOperation(value = "用户的注销")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        int rest = userService.userLogout(request);
        return ResultUtils.success(rest);
    }

    /**
     * 判断是否为管理员
     *
     * @param request 是否为管理员
     * @return true
     */
    public boolean is_Admin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user == null || user.getUserRole() != ADMIN_ROLE;
    }
}
