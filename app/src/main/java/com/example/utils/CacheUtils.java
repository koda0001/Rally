package com.example.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.data.LoginData;
import com.example.data.User;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;

public class CacheUtils {
    private static final String CACHE_NAME = "cache";
    private static final String LOGIN_DATA = "login_data";

    private static final String USER = "user";

    private final Gson gson;

    private final SharedPreferences appPreferences;

    public CacheUtils(Context context) {
        appPreferences = context.getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE);
        GsonBuilder builder = getDefaultGsonBuilder();
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gson = builder.create();
    }

    private static GsonBuilder getDefaultGsonBuilder() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                    BigDecimal value = BigDecimal.valueOf(src);
                    value = value.setScale(8, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
                    if (value.scale() < 0) value = value.setScale(0);
                    if (src == 0.d) value = value.setScale(0);
                    return new JsonPrimitive(value);
                });
    }

    public void commitLoginData(LoginData data) {
        commitObjectAsJson(LOGIN_DATA, data);
    }

    public LoginData getLoginData() {
        return getSerializedObject(LOGIN_DATA, LoginData.class);
    }

    public void commitUser(User user) {
        commitObjectAsJson(USER, user);
    }

    public User getUser() {
        return getSerializedObject(USER, User.class);
    }

    public void commitObjectAsJson(String key, Object object) {
        commitString(key, object == null ? null : gson.toJson(object));
    }

    public void commitString(String key, String string) {
        SharedPreferences.Editor editor = appPreferences.edit();
        if (string == null || string.isEmpty()) {
            editor.remove(key);
        } else {
            editor.putString(key, string);
        }
        editor.apply();
    }

    public <T extends Serializable> T getSerializedObject(String key, Class<T> clazz) {
        String json = appPreferences.getString(key, null);
        try {
            T result = gson.fromJson(json, clazz);
            if (result == null && json != null && !json.isEmpty()) {
                return stringToObject(json);
            } else {
                return result;
            }
        } catch (Exception e) {
            return stringToObject(json);
        }
    }

    public static <T extends Serializable> T stringToObject(String string) {
        T object = null;
        if (string == null) {
            return null;
        }
        try {
            byte[] bytes = Base64.decode(string.getBytes(), Base64.DEFAULT);
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            object = (T) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public void removeLoginData() {
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.remove(LOGIN_DATA);
        editor.apply();
    }
}
