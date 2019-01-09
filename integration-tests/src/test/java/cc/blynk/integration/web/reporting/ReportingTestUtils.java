package cc.blynk.integration.web.reporting;

import cc.blynk.server.core.model.widgets.web.FieldType;
import cc.blynk.server.core.model.widgets.web.SelectedColumn;
import cc.blynk.server.db.dao.descriptor.Column;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 10.10.17.
 */
public final class ReportingTestUtils {

    private ReportingTestUtils() {
    }

    public static SelectedColumn columnFrom(String label) {
        return new SelectedColumn(Column.labelTrim(label), label, FieldType.COLUMN);
    }

    public static SelectedColumn metaDataFrom(String name) {
        return new SelectedColumn(name, null, FieldType.METADATA);
    }

}
