package be.civadis.poc.s3.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import feign.gson.GsonDecoder;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CustomGsonDecoder extends GsonDecoder {

    public CustomGsonDecoder() {
        super(createGsonBuilder().create());
    }

    public static GsonBuilder createGsonBuilder(){
        return new GsonBuilder().registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, type, jsonDeserializationContext) -> {
                String date = json.getAsJsonPrimitive().getAsString();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("GMT"));

                try {
                    return format.parse(date);
                } catch (ParseException exp) {

                    return null;
                }
            }).registerTypeAdapter(ZonedDateTime.class, (JsonDeserializer<ZonedDateTime>) (json, type, jsonDeserializationContext) -> ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString())).registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>) (jsonElement, type, jsonDeserializationContext) -> {
                String stringOffSet = jsonElement.getAsJsonPrimitive().getAsString();
                //Il semble que parfois on reçoive une mauvaise date en STring
                // 1/   "eventTime": "2022-01-13T08:45:10.597",
                //2/   "eventTime": "2022-01-10T09:00:06.192+01:00",
                //3/   "eventTime": "2022-01-14T10:30:06.835Z",

                if (!stringOffSet.toUpperCase().endsWith("Z") && !stringOffSet.matches("^(\\d{4}(-\\d\\d){2})[tT].*(\\+|-)\\d.*")) {
                    stringOffSet = stringOffSet + ZoneOffset.UTC;
                }
                return OffsetDateTime.parse(stringOffSet);
            });
    }

    public static String extractListModel(Gson decoder, String json){

        Type listOfMyClassObject = new TypeToken<List<Object>>() {}.getType();
        List<Object> objList = decoder.fromJson(json, listOfMyClassObject);

        if (!objList.isEmpty()) {
            JsonElement jsonElt = decoder.toJsonTree(objList.get(0));
            if (jsonElt.isJsonArray()) {
                return decoder.toJson(jsonElt);
            }
        }

        return json;
    }

    public static String extractListModel(Gson decoder, List<Object> objList){
        if (!objList.isEmpty()) {
            JsonElement jsonElt = decoder.toJsonTree(objList.get(0));
            if (jsonElt.isJsonArray()) {
                return decoder.toJson(jsonElt);
            } else {
                return decoder.toJson(decoder.toJsonTree(objList));
            }
        }
        return "[]";
    }

}
