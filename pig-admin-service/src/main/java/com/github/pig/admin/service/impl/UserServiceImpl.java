package com.github.pig.admin.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.admin.entity.SysUser;
import com.github.pig.admin.mapper.SysUserMapper;
import com.github.pig.admin.mapper.SysUserRoleMapper;
import com.github.pig.admin.service.UserService;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lengleng
 * @date 2017/10/31
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    @Cacheable(value = "user_details", key = "#username")
    public UserVo findUserByUsername(String username) {
        return sysUserMapper.selectUserVoByUsername(username);
    }

    @Override
    public Page selectWithRolePage(Query query) {
        query.setRecords(sysUserMapper.selectUserVoPage(query,query.getCondition()));
        return query;
    }

    @Override
    @CacheEvict(value = "user_details", key = "#username")
    public void clearCache(String username) {

    }

    @Override
    public void deleteUserById(Integer id) {
        // 删除用互相角色
        sysUserRoleMapper.deleteByUserId(id);
        sysUserMapper.deleteById(id);
        // 删除用户的相关redis缓存，如果他有登录,此处代码设计不合理，传id的换缓存可以直接用用户到的id来
//        后面改造
        clearCache(UserUtils.getUserName());
        throw new RuntimeException();
    }
}
