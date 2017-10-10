package cc.blynk.integration.https.reporting;

import cc.blynk.server.core.model.widgets.web.FieldType;
import cc.blynk.server.core.model.widgets.web.SelectedColumnDTO;
import cc.blynk.server.db.dao.descriptor.Column;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 10.10.17.
 */
final class ReportingTestUtils {

    private ReportingTestUtils() {
    }

    static SelectedColumnDTO columnFrom(String label) {
        return new SelectedColumnDTO(Column.labelTrim(label), label, FieldType.COLUMN);
    }

    static SelectedColumnDTO metaDataFrom(String name) {
        return new SelectedColumnDTO(name, null, FieldType.METADATA);
    }

}
