package com.graProject.graBackend.common.utils;

import cn.hutool.bloomfilter.BitSetBloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graProject.graBackend.entity.UserDO;
import com.graProject.graBackend.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户名布隆过滤器工具类。
 *
 * 用于缓存系统中已存在的用户名，降低重复注册场景下对数据库的直接访问压力。
 */
@Component
public class UsernameBloomFilterUtil {

    /**
     * 布隆过滤器预估容量。
     */
    private static final int DEFAULT_CAPACITY = 100000;

    /**
     * 布隆过滤器预计写入数量。
     */
    private static final int EXPECTED_INSERTIONS = 50000;

    /**
     * 哈希函数个数。
     */
    private static final int HASH_COUNT = 5;

    /**
     * 用户 Mapper。
     */
    private final UserMapper userMapper;

    /**
     * 用户名布隆过滤器实例。
     */
    private volatile BitSetBloomFilter bloomFilter = createBloomFilter();

    /**
     * 构造方法。
     *
     * @param userMapper 用户 Mapper
     */
    public UsernameBloomFilterUtil(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 初始化布隆过滤器数据。
     */
    @PostConstruct
    public void init() {
        refreshUsernames();
    }

    /**
     * 判断用户名是否可能已存在。
     *
     * @param username 用户名
     * @return 是否可能存在
     */
    public boolean mightContain(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        return bloomFilter.contains(username.trim());
    }

    /**
     * 向布隆过滤器中追加用户名。
     *
     * @param username 用户名
     */
    public void addUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        bloomFilter.add(username.trim());
    }

    /**
     * 从数据库全量刷新用户名布隆过滤器。
     */
    public synchronized void refreshUsernames() {
        BitSetBloomFilter latestBloomFilter = createBloomFilter();
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .select(UserDO::getUsername)
                .eq(UserDO::getIsDelete, 0);
        List<UserDO> userList = userMapper.selectList(wrapper);
        for (UserDO userDO : userList) {
            if (userDO != null && StringUtils.hasText(userDO.getUsername())) {
                latestBloomFilter.add(userDO.getUsername().trim());
            }
        }
        this.bloomFilter = latestBloomFilter;
    }

    /**
     * 创建布隆过滤器实例。
     *
     * @return 布隆过滤器实例
     */
    private BitSetBloomFilter createBloomFilter() {
        return BloomFilterUtil.createBitSet(DEFAULT_CAPACITY, EXPECTED_INSERTIONS, HASH_COUNT);
    }
}
