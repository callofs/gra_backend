package com.graProject.graBackend.common.job;

import com.graProject.graBackend.common.utils.UsernameBloomFilterUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 用户名布隆过滤器刷新定时任务。
 *
 * 定期从数据库重新加载用户名数据，降低布隆过滤器与数据库数据不一致的风险。
 */
@Component
public class UsernameBloomFilterRefreshJob {

    /**
     * 用户名布隆过滤器工具。
     */
    private final UsernameBloomFilterUtil usernameBloomFilterUtil;

    /**
     * 构造方法。
     *
     * @param usernameBloomFilterUtil 用户名布隆过滤器工具
     */
    public UsernameBloomFilterRefreshJob(UsernameBloomFilterUtil usernameBloomFilterUtil) {
        this.usernameBloomFilterUtil = usernameBloomFilterUtil;
    }

    /**
     * 定时刷新用户名布隆过滤器。
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void refreshBloomFilter() {
        usernameBloomFilterUtil.refreshUsernames();
    }
}
