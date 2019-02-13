package cc.blynk.server.core.model.web.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.atomic.AtomicInteger;

public final class ShipmentProgress {

    public final AtomicInteger started;
    public final AtomicInteger requestSent;
    public final AtomicInteger firmwareRequested;
    public final AtomicInteger firmwareUploaded;
    public final AtomicInteger success;
    public final AtomicInteger failure;
    public final AtomicInteger downloadLimitReached;

    public ShipmentProgress() {
        this(0, 0, 0, 0, 0, 0, 0);
    }

    @JsonCreator
    public ShipmentProgress(@JsonProperty("started") int started,
                            @JsonProperty("requestSent") int requestSent,
                            @JsonProperty("firmwareRequested") int firmwareRequested,
                            @JsonProperty("firmwareUploaded") int firmwareUploaded,
                            @JsonProperty("success") int success,
                            @JsonProperty("failure") int failure,
                            @JsonProperty("downloadLimitReached") int downloadLimitReached) {
        this.started =              new AtomicInteger(started);
        this.requestSent =          new AtomicInteger(requestSent);
        this.firmwareRequested =    new AtomicInteger(firmwareRequested);
        this.firmwareUploaded =     new AtomicInteger(firmwareUploaded);
        this.success =              new AtomicInteger(success);
        this.failure =              new AtomicInteger(failure);
        this.downloadLimitReached = new AtomicInteger(downloadLimitReached);
    }

    public boolean isFinished(int deviceCount) {
        return success.get() + failure.get() + downloadLimitReached.get() >= deviceCount;
    }

}
