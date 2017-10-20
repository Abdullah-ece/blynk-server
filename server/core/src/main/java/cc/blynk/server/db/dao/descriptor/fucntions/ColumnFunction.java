package cc.blynk.server.db.dao.descriptor.fucntions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.10.17.
 */

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({

        //web widgets
        @JsonSubTypes.Type(value = ReplaceFunction.class, name = "REPLACE")
})
public interface ColumnFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);

}
