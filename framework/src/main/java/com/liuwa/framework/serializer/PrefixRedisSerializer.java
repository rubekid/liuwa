package com.liuwa.framework.serializer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

/**
 * 带前缀键名系列器
 */
public class PrefixRedisSerializer extends StringRedisSerializer {

    private String prefix;

    private PrefixRedisSerializer(){

    }

    public PrefixRedisSerializer(String prefix){
        this.prefix = prefix;
    }


    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.serializer.RedisSerializer#deserialize(byte[])
     */
    @Override
    public String deserialize(@Nullable byte[] bytes) {
        String key = super.deserialize(bytes);
        if(StringUtils.isNotEmpty(prefix) && key.startsWith(prefix)){
            return key.substring(prefix.length() + 1);
        }
        return key;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.serializer.RedisSerializer#serialize(java.lang.Object)
     */
    @Override
    public byte[] serialize(@Nullable String string) {
        if(StringUtils.isNotEmpty(prefix) && !string.startsWith(prefix + ":")){
            string = prefix + ":" + string;
        }
        return super.serialize(string);
    }

}
