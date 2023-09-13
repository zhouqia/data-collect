package com.ipharmacare.collect.common.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 序列化反序列化工具 对外屏蔽实现细节  当前使用gson实现， 可根据实际情况 替换实现细节 比如使用fastjson/jackson
 *
 * @Author: zhouqiang
 * @Date: 2020/10/21 10:11
 */
public class GsonUtil {

    private static Gson gson = null;

    static {
        if (gson == null) {
            //序列化
            JsonSerializer<LocalDateTime> jsonSerializer = (localDateTime, type, jsonSerializationContext) -> new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//反序列化
            JsonDeserializer<LocalDateTime> jsonDeserializer = (jsonElement, type, jsonDeserializationContext) -> LocalDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            //当使用GsonBuilder方式时属性为空的时候输出来的json字符串是有键值key的,显示形式是"key":null，而直接new出来的就没有"key":null的
            gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                        @Override
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            if (json == null) {
                                return null;
                            }
                            String param = json.getAsJsonPrimitive().getAsString();
                            if (param.contains("\"\"")) {
                                param = param.replaceAll("\"", "");
                            }

                            try {
                                //兼容时间戳字符串
                                if (!param.contains("-")) {
                                    return new Date(Long.parseLong(param));
                                }
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                return simpleDateFormat.parse(json.getAsJsonPrimitive().getAsString());
                            } catch (ParseException e) {
                                throw new JsonParseException("数据解析失败：" + json.getAsJsonPrimitive().getAsString(), e);
                            }
                        }
                    })
                    .disableHtmlEscaping()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .registerTypeAdapter(LocalDateTime.class, jsonSerializer)
                    .registerTypeAdapter(LocalDateTime.class, jsonDeserializer)
//                    .serializeNulls() // null是否转化进json字符串中
                    .create();
        }
    }

    private GsonUtil() {

    }

    /**
     * 将对象转成json格式
     *
     * @param object
     * @return String
     * @throws UnsupportedOperationException if object.equal("class")
     */
    public static String toString(Object object) {
        if (null == object) {
            return null;
        }
        return gson.toJson(object);
    }

    /**
     * 将json转成特定的cls的对象
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T toBean(String gsonString, Class<T> cls) {
        if (StringUtils.isEmpty(gsonString)) {
            return null;
        }
        return gson.fromJson(gsonString, cls);
    }

    /**
     * json字符串转成list
     *
     * @param json
     * @param cls
     * @return
     */
    public static <T> List<T> toList(String json, Class<T> cls) {
        if (StringUtils.isEmpty(json)) {
            return new ArrayList<T>();
        }
        ArrayList<T> mList = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        array.forEach(jsonElement -> mList.add(gson.fromJson(jsonElement, cls)));
        return mList;
    }

    /**
     * json字符串转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> toListMap(String gsonString) {
        if (StringUtils.isEmpty(gsonString)) {
            return null;
        }
        return gson.fromJson(gsonString, new TypeToken<List<Map<String, T>>>() {
        }.getType());
    }

    /**
     * json字符串转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> toMap(String gsonString) {
        if (StringUtils.isEmpty(gsonString)) {
            return null;
        }
        return gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
        }.getType());
    }

    /**
     * json字符串转成map的
     *
     * @param gsonString
     * @return
     */
    public static Map<String, List<Long>> toMapLong(String gsonString) {
        if (StringUtils.isEmpty(gsonString)) {
            return null;
        }
        return gson.fromJson(gsonString, new TypeToken<Map<String, List<Long>>>() {
        }.getType());
    }

    public static final Gson holder = new Gson();
}
