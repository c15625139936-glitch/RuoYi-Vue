package com.ruoyi.system.validator;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.common.exception.ServiceException;

/**
 * 参数配置校验器。
 *
 * service 层兜底格式校验，与 Controller 上的 Bean Validation（@NotBlank/@Size）互补：
 * 即使绕过 Controller 的内部调用，非法键名也进不了库和缓存。
 */
public class SysConfigValidator
{
    /** 参数键名最大长度 */
    private static final int KEY_MAX_LENGTH = 100;

    /** 键名规则：字母/数字开头，后续仅允许字母、数字、点、中划线 */
    private static final Pattern KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9.\\-]*$");

    private SysConfigValidator()
    {
    }

    /**
     * 校验参数键名，非法时抛 ServiceException
     *
     * @param configKey 参数键名
     */
    public static void validateConfigKey(String configKey)
    {
        if (StringUtils.isBlank(configKey))
        {
            throw new ServiceException("参数键名不能为空");
        }
        if (configKey.length() > KEY_MAX_LENGTH)
        {
            throw new ServiceException("参数键名长度不能超过" + KEY_MAX_LENGTH + "个字符");
        }
        if (!KEY_PATTERN.matcher(configKey.trim()).matches())
        {
            throw new ServiceException("参数键名格式不正确，仅支持字母、数字、点、中划线，且以字母或数字开头");
        }
    }
}
