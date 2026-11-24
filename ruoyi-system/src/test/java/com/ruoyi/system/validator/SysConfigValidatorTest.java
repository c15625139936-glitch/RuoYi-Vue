package com.ruoyi.system.validator;

import org.junit.jupiter.api.Test;
import com.ruoyi.common.exception.ServiceException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * SysConfigValidator 边界单测：纯静态校验，不连库、不 mock 依赖。
 */
class SysConfigValidatorTest
{
    @Test
    void validateConfigKey_null_shouldThrow()
    {
        assertThrows(ServiceException.class,
                () -> SysConfigValidator.validateConfigKey(null));
    }

    @Test
    void validateConfigKey_blank_shouldThrow()
    {
        assertThrows(ServiceException.class,
                () -> SysConfigValidator.validateConfigKey(""));
        assertThrows(ServiceException.class,
                () -> SysConfigValidator.validateConfigKey("   "));
    }

    @Test
    void validateConfigKey_tooLong_shouldThrow()
    {
        String tooLong = "a".repeat(101);
        assertThrows(ServiceException.class,
                () -> SysConfigValidator.validateConfigKey(tooLong));
    }

    @Test
    void validateConfigKey_spaceOrSpecialChar_shouldThrow()
    {
        assertThrows(ServiceException.class,
                () -> SysConfigValidator.validateConfigKey("sys account"));
        assertThrows(ServiceException.class,
                () -> SysConfigValidator.validateConfigKey("sys@account"));
        assertThrows(ServiceException.class,
                () -> SysConfigValidator.validateConfigKey("sys/account"));
        assertThrows(ServiceException.class,
                () -> SysConfigValidator.validateConfigKey("sys_account"));
    }

    @Test
    void validateConfigKey_startsWithDashOrDot_shouldThrow()
    {
        assertThrows(ServiceException.class,
                () -> SysConfigValidator.validateConfigKey("-sys"));
        assertThrows(ServiceException.class,
                () -> SysConfigValidator.validateConfigKey(".sys"));
    }

    @Test
    void validateConfigKey_camelCaseDottedKey_shouldPass()
    {
        assertDoesNotThrow(() -> SysConfigValidator.validateConfigKey("sys.account.captchaEnabled"));
    }

    @Test
    void validateConfigKey_validWithDash_shouldPass()
    {
        assertDoesNotThrow(() -> SysConfigValidator.validateConfigKey("flag.notice-new-view"));
    }

    @Test
    void validateConfigKey_alphanumeric_shouldPass()
    {
        assertDoesNotThrow(() -> SysConfigValidator.validateConfigKey("abc123"));
    }

    @Test
    void validateConfigKey_100Chars_shouldPass()
    {
        assertDoesNotThrow(() -> SysConfigValidator.validateConfigKey("a".repeat(100)));
    }
}
