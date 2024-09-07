package com.common.utils.serialization;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gson工具类
 *
 * @author liufeng
 * @date 2017-11-13
 */
public class GsonUtil {
    private static final Gson gson = newGson();

    private GsonUtil() {
    }

    /**
     * 创建Gson对象
     *
     * @return
     */
    private static Gson newGson() {
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapterFactory(new GsonTypeAdapterFactory());
        return gb.setLenient().create();
    }

    /**
     * object转成json
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        return gson.toJson(object);
    }

    /**
     * object转成json
     *
     * @param object
     * @param typeOfT
     * @return
     */
    public static String toJson(Object object, Type typeOfT) {
        if (object == null) {
            return null;
        }
        return gson.toJson(object, typeOfT);
    }

    /**
     * json转成bean
     *
     * @param jsonStr
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T toBean(String jsonStr, Class<T> cls) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        try {
            return gson.fromJson(jsonStr, cls);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * json转成bean
     *
     * @param jsonStr
     * @return
     */
    public static <T> T toBean(String jsonStr, Type typeOfT) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        return gson.fromJson(jsonStr, typeOfT);
    }

    /**
     * object转成bean
     *
     * @param object
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T toBean(Object object, Class<T> cls) {
        return toBean(toJson(object), cls);
    }

    /**
     * object转成bean
     *
     * @param object
     * @param typeOfT
     * @param <T>
     * @return
     */
    public static <T> T toBean(Object object, Type typeOfT) {
        return toBean(toJson(object), typeOfT);
    }

    /**
     * JSONObject转成bean
     *
     * @param jsonObject
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T toBean(JSONObject jsonObject, Class<T> cls) {
        return toBean(jsonObject.toString(), cls);
    }

    /**
     * JSONObject转成bean
     *
     * @param jsonObject
     * @param typeOfT
     * @param <T>
     * @return
     */
    public static <T> T toBean(JSONObject jsonObject, Type typeOfT) {
        return toBean(jsonObject.toString(), typeOfT);
    }

    /**
     * JSONArray转成bean
     *
     * @param jsonArray
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T toBean(JSONArray jsonArray, Class<T> cls) {
        return toBean(jsonArray.toString(), cls);
    }

    /**
     * JSONArray转成bean
     *
     * @param jsonArray
     * @param typeOfT
     * @param <T>
     * @return
     */
    public static <T> T toBean(JSONArray jsonArray, Type typeOfT) {
        return toBean(jsonArray.toString(), typeOfT);
    }

    /**
     * object转成JSONObject
     *
     * @param object
     * @return
     * @throws JSONException
     */
    public static JSONObject toJSONObject(Object object) {
        try {
            return new JSONObject(toJson(object));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * json转成JSONObject
     *
     * @param jsonStr
     * @return
     * @throws JSONException
     */
    public static JSONObject toJSONObject(String jsonStr) {
        try {
            return new JSONObject(jsonStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * object转成JSONArray
     *
     * @param object
     * @return
     * @throws JSONException
     */
    public static JSONArray toJSONArray(Object object) {
        try {
            return new JSONArray(toJson(object));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * json转成JSONArray
     *
     * @param jsonStr
     * @return
     * @throws JSONException
     */
    public static JSONArray toJSONArray(String jsonStr) {
        try {
            return new JSONArray(jsonStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Collection转成JSONArray
     *
     * @param collection
     * @return
     */
    public static JSONArray toJSONArray(Collection collection) {
        try {
            return new JSONArray(toJson(collection));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * json转成list
     *
     * @param jsonStr
     * @return
     */
    public static <T> List<T> toList(String jsonStr) {
        return toBean(jsonStr, new TypeToken<List<T>>() {
        }.getType());
    }

    /**
     * json转成map
     *
     * @param jsonStr
     * @return
     */
    public static <T> Map<String, T> toMap(String jsonStr) {
        return toBean(jsonStr, new TypeToken<Map<String, T>>() {
        }.getType());
    }

    /**
     * Json转List集合,遇到解析不了的，就使用这个
     */
    public static <T> List<T> toList(String json, Class<T> cls) {
        try {
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            List<T> mList = new ArrayList<T>(array.size());
            for (JsonElement elem : array) {
                mList.add(gson.fromJson(elem, cls));
            }
            return mList;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * json转成map,遇到解析不正确的，就使用这个
     *
     * @param jsonStr
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> toMap(String jsonStr, Class<T> cls) {
        try {
            JsonObject object = new JsonParser().parse(jsonStr).getAsJsonObject();
            Map<String, T> dataMap = new HashMap<>(object.size());
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                String key = entry.getKey();
                JsonElement elem = entry.getValue();
                dataMap.put(key, gson.fromJson(elem, cls));
            }

            return dataMap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否json字符串
     *
     * @param content
     * @return
     */
    public static boolean isJson(String content) {
        try {
            Object object = new JSONTokener(content).nextValue();
            return object instanceof JSONObject || object instanceof JSONArray;
        } catch (Exception ignored) {
        }
        return false;
    }


    /**
     * 处理JSON和数据类型不对应时，不抛异常
     */
    public static class GsonTypeAdapterFactory implements TypeAdapterFactory {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            final TypeAdapter<T> adapter = gson.getDelegateAdapter(this, type);
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    adapter.write(out, value);
                }

                @Override
                public T read(JsonReader in) {
                    //gson 库会通过JsonReader对json对象的每个字段进项读取,当发现类型不匹配时抛出异常
                    try {
                        return adapter.read(in);
                    } catch (Throwable e) {
                        //那么我们就在它抛出异常的时候进行处理,让它继续不中断接着往下读取其他的字段就好了
                        try {
                            consumeAll(in);
                        } catch (Throwable ignored) {
                        }
                    }

                    return null;
                }

                private void consumeAll(JsonReader in) throws Exception {
                    if (in.hasNext()) {
                        JsonToken peek = in.peek();
                        if (peek == JsonToken.STRING) {
                            in.nextString();
                        } else if (peek == JsonToken.BEGIN_ARRAY) {
                            in.beginArray();
                            consumeAll(in);
                            in.endArray();
                        } else if (peek == JsonToken.BEGIN_OBJECT) {
                            in.beginObject();
                            consumeAll(in);
                            in.endObject();
                        } else if (peek == JsonToken.END_ARRAY) {
                            in.endArray();
                        } else if (peek == JsonToken.END_OBJECT) {
                            in.endObject();
                        } else if (peek == JsonToken.NUMBER) {
                            in.nextString();
                        } else if (peek == JsonToken.BOOLEAN) {
                            in.nextBoolean();
                        } else if (peek == JsonToken.NAME) {
                            in.nextName();
                            consumeAll(in);
                        } else if (peek == JsonToken.NULL) {
                            in.nextNull();
                        }
                    }
                }
            };
        }
    }
}
