package com.course.security;

import com.course.entity.User;
import com.course.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义用户详情服务
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户信息: {}", username);
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("用户已被禁用: {}", username);
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        
        log.debug("用户信息加载成功: {}", username);
        return UserPrincipal.create(user);
    }

    /**
     * 根据用户ID加载用户信息
     */
    public UserDetails loadUserById(Long id) {
        log.debug("根据ID加载用户信息: {}", id);
        
        User user = userService.getUserById(id);
        if (user == null) {
            log.warn("用户不存在: {}", id);
            throw new UsernameNotFoundException("用户不存在: " + id);
        }
        
        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("用户已被禁用: {}", id);
            throw new UsernameNotFoundException("用户已被禁用: " + id);
        }
        
        log.debug("用户信息加载成功: {}", id);
        return UserPrincipal.create(user);
    }
}
