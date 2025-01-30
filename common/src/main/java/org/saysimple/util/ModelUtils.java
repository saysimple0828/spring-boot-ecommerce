package org.saysimple.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public final class ModelUtils {

    private static final ModelMapper MAPPER = new ModelMapper();

    static {
        MAPPER.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public static <T> T map(Object source, Class<T> targetClass) {
        return MAPPER.map(source, targetClass);
    }

    public static <T> T strictMap(Object source, Class<T> targetClass) {
        ModelMapper strictMapper = new ModelMapper();
        strictMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return strictMapper.map(source, targetClass);
    }

    public static <T> T convert(Object source, T target) {
        MAPPER.map(source, target);
        return target;
    }
}
