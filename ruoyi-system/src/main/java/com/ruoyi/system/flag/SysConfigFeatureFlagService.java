package com.ruoyi.system.flag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 基于 sys_config 表的 FeatureFlag 实现。
 *
 * 配置 key 约定：flag.<key>，存入 RuoYi 参数配置表；配置值格式：
 *   "false"        全局关闭（缺省默认）
 *   "true"         全局开启
 *   "false:1,2,3"  仅白名单用户 ID 开启
 *   "true:1,2,3"   全局开启，白名单附加（预留扩展）
 */
@Component
public class SysConfigFeatureFlagService implements FeatureFlagService
{
    private static final String DEFAULT_OFF = "false";
    private static final String PREFIX = "flag.";

    @Autowired
    private ISysConfigService configService;

    @Override
    public boolean isOn(String key)
    {
        return parseGlobal(raw(key));
    }

    @Override
    public boolean isOn(String key, Long userId)
    {
        String[] parts = raw(key).split(":", 2);
        if (parseGlobal(parts[0]))
        {
            return true;
        }
        if (userId != null && parts.length == 2)
        {
            Set<String> whitelist = new HashSet<>(Arrays.asList(parts[1].split(",")));
            return whitelist.contains(String.valueOf(userId));
        }
        return false;
    }

    private String raw(String key)
    {
        String value = configService.selectConfigByKey(PREFIX + key);
        return value == null ? DEFAULT_OFF : value.trim();
    }

    private boolean parseGlobal(String v)
    {
        return "true".equalsIgnoreCase(v.trim());
    }
}
