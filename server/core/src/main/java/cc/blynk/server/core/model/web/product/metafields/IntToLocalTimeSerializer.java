package cc.blynk.server.core.model.web.product.metafields;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.10.17.
 */
public class IntToLocalTimeSerializer extends JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonParser jsonParser,
                               DeserializationContext deserializationContext) throws IOException {
        return LocalTime.ofSecondOfDay(jsonParser.readValueAs(int.class));
    }

}
