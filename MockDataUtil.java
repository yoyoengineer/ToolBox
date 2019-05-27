package com.souche.base.model.material.ng;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MockDataUtil {

    /**
     * 生成所传入类型的实例，并填入一些默认数据
     *
     * @param clazz 需要生成实例的类型
     * @param <T>
     * @return 所传类型对应的实例
     */
    public static <T> T makeData(Class<? extends T> clazz) {
        return makeData(clazz, new HashMap<>());
    }

    /**
     * 生成所传入类型的实例，填入一些默认数据，并通过第二个参数为某些属性设置用户指定的值
     *
     * @param clazz  需要生成实例的类型
     * @param params 指定某些属性所对应的值
     * @param <T>
     * @return 所传类型对应的实例
     */
    public static <T> T makeData(Class<? extends T> clazz, Map<String, Object> params) {
        T obj = null;
        try {
            obj = clazz.newInstance();
        } catch (Exception e1) {
            log.error("can not create object for class {}", clazz.toString());
            return null;
        }
        Method[] methods = clazz.getMethods();

        if (methods == null) {
            return null;
        }

        T finalObj = obj;
        Arrays.stream(methods).forEach(method -> {
            String methodName = method.getName();
            if (methodName == null || !methodName.startsWith("set")) {
                return;
            }

            String fieldName = methodName.substring(3, methodName.length());

            if (StringUtils.isEmpty(fieldName)) {
                return;
            }

            fieldName = toLowerCaseFirstOne(fieldName);

            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length != 1) {
                return;
            }
            Class parameterType = parameterTypes[0];

            if (parameterType == null) {
                return;
            }

            Object param = params.get(fieldName);
            try {
                if (param != null) {
                    method.invoke(finalObj, param);
                } else if ("java.lang.String".equals(parameterType.getName())) {
                    method.invoke(finalObj, "test_" + fieldName);
                } else if ("java.util.Date".equals(parameterType.getName())) {
                    method.invoke(finalObj, new Date());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        return finalObj;
    }

    /**
     * 生成所传入字符串的首字母转换为小写字母后的字符串
     *
     * @param origin 原始字符串
     * @return 首字母转换为小写字母后的字符串
     */
    private static String toLowerCaseFirstOne(String origin) {
        if (Character.isLowerCase(origin.charAt(0)))
            return origin;
        else
            return (new StringBuilder()).append(Character.toLowerCase(origin.charAt(0))).append(origin.substring(1)).toString();
    }
}
