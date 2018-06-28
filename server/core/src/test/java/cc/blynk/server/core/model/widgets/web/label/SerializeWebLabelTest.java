package cc.blynk.server.core.model.widgets.web.label;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.outputs.TextAlignment;
import cc.blynk.server.core.model.widgets.web.WebSource;
import org.junit.Test;

import static cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType.RAW_DATA;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 18.12.17.
 */
public class SerializeWebLabelTest {

    @Test
    public void print() throws Exception {
        WebLabel webLabel = new WebLabel();

        webLabel.id = 1;
        webLabel.sources = new WebSource[] {new WebSource("some Label", "#334455",
                false, RAW_DATA, new DataStream((byte) 1, PinType.VIRTUAL),
                null,
                null,
                null, SortOrder.ASC, 10, false, null, false)};
        webLabel.dataType = DataType.Number;
        webLabel.decimalFormat = "#.#";
        webLabel.valueSuffix = "Loads";
        webLabel.alignment = TextAlignment.MIDDLE;
        webLabel.colorsSet = new ColorSet[] {
                new ColorSet(0, 10, "#000", "#fff", "#333")
        };
        webLabel.backgroundColor = "#000";
        webLabel.textColor = "#fff";
        webLabel.level = new Level(0, 100, Position.HORIZONTAL, "#000");

        System.out.println(JsonParser.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(webLabel));
    }

}
