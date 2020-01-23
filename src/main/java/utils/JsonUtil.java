package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static String toJson(Object any) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(any);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static <T> T fromJson(String json, TypeReference<T> t) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

    }
}
