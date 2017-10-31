package cc.blynk.server.core.reporting.raw;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.db.dao.descriptor.TableDataMapper;
import cc.blynk.server.db.dao.descriptor.TableDescriptor;
import cc.blynk.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Simply stores every record in memory that should be stored in reporting DB lately.
 * Could cause OOM at high request rate. However we don't use it very high loads.
 * So this is fine for now.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.01.17.
 */
public class RawDataProcessor {

    public final Queue<TableDataMapper> rawStorage;

    public RawDataProcessor(boolean enable) {
        rawStorage = new ConcurrentLinkedQueue<>();
    }

    public void collect(int deviceId, PinType pinType, byte pin, String stringValue) {
        if (stringValue.contains(StringUtils.BODY_SEPARATOR_STRING)) {
            //storing for now just first part for multi value
            stringValue = stringValue.split(StringUtils.BODY_SEPARATOR_STRING)[0];
        }

        rawStorage.add(
                new TableDataMapper(
                    TableDescriptor.BLYNK_DEFAULT_INSTANCE,
                    deviceId, pin, pinType, LocalDateTime.now(),
                    stringValue
                )
        );
    }

}
