package com.zjjg.digitize.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 国际化消息服务类
 * 用于加载和获取不同语言的错误消息
 */
public class MessageService {
    
    private static final String BUNDLE_NAME = "i18n.messages";
    
    /**
     * 根据消息键和语言环境获取国际化消息
     * @param key 消息键
     * @param locale 语言环境
     * @param args 消息参数
     * @return 国际化消息
     */
    public static String getMessage(String key, Locale locale, Object... args) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
            String message = bundle.getString(key);
            
            // 替换消息中的参数
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    message = message.replace("{" + i + "}", String.valueOf(args[i]));
                }
            }
            
            return message;
        } catch (Exception e) {
            // 如果获取消息失败，返回原始键
            return key;
        }
    }
    
    /**
     * 根据消息键和默认语言环境获取国际化消息
     * @param key 消息键
     * @param args 消息参数
     * @return 国际化消息
     */
    public static String getMessage(String key, Object... args) {
        return getMessage(key, Locale.getDefault(), args);
    }
}