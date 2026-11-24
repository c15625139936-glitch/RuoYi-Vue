package com.ruoyi.system.flag;

/**
 * 特性标志（Feature Flag）服务。
 *
 * 未稳定功能包裹在开关之后：代码可以合并到主干，但生产环境默认关闭，
 * 不影响用户。部署与发布解耦，出问题关开关即可回退，无需发版。
 */
public interface FeatureFlagService
{
    /**
     * 全局开关是否开启
     *
     * @param key 标志位 key（不含 flag. 前缀）
     * @return true 开启
     */
    boolean isOn(String key);

    /**
     * 按用户维度判断开关。全局开启则直接返回 true；否则仅白名单用户开启。
     *
     * @param key    标志位 key（不含 flag. 前缀）
     * @param userId 用户 ID，可为 null
     * @return true 开启
     */
    boolean isOn(String key, Long userId);
}
