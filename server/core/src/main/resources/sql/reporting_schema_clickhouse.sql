CREATE DATABASE blynk_local_reporting;
USE blynk_local_reporting;

CREATE TABLE reporting_events (
  id UInt64,
  device_id UInt32,
  ts DateTime DEFAULT now(),
  event_hashcode Int32,
  type UInt8,
  description String
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(ts)
ORDER BY (device_id, ts, event_hashcode, type);

CREATE TABLE reporting_events_resolved (
  id UInt64,
  resolved_at DateTime DEFAULT now(),
  resolved_by String,
  resolved_comment String,
  is_resolved UInt8 DEFAULT 1
)
ENGINE = ReplacingMergeTree(resolved_at)
PARTITION BY toYYYYMM(resolved_at)
ORDER BY (id);

CREATE TABLE reporting_events_last_seen (
  device_id UInt32,
  email String,
  ts DateTime DEFAULT now()
)
ENGINE = ReplacingMergeTree(ts)
ORDER BY (device_id, email);

CREATE TABLE reporting_device_raw_data (
  device_id UInt32,
  pin UInt8,
  pin_type UInt8,
  ts DateTime,
  value Float64
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(ts)
ORDER BY (device_id, pin, pin_type, ts);

CREATE TABLE reporting_app_stat_minute (
  region String,
  ts DateTime,
  active UInt32,
  active_week UInt32,
  active_month UInt32,
  minute_rate UInt32,
  connected UInt32,
  online_apps UInt32,
  online_hards UInt32,
  total_online_apps UInt32,
  total_online_hards UInt32,
  registrations UInt32
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(ts)
ORDER BY (region, ts);

CREATE TABLE reporting_command_stat_minute (
  region String,
  ts DateTime,
  command_code UInt16,
  counter UInt32
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(ts)
ORDER BY (region, ts, command_code);

CREATE TABLE reporting_shipment_events (
  shipment_id UInt32,
  device_id UInt32,
  ts DateTime DEFAULT now(),
  status UInt8
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(ts)
ORDER BY (shipment_id, device_id, ts);

CREATE MATERIALIZED VIEW reporting_command_stat_monthly
(region String,  ts Date,  command_code UInt16,  counter UInt64)
ENGINE = SummingMergeTree()
PARTITION BY toYYYYMM(ts)
ORDER BY (region, command_code, ts)
AS SELECT
    region,
    toStartOfMonth(ts) AS ts,
    command_code,
    toUInt64(counter) AS counter
FROM reporting_command_stat_minute;

CREATE MATERIALIZED VIEW reporting_average_minute
ENGINE = AggregatingMergeTree()
PARTITION BY toYYYYMM(ts)
ORDER BY (device_id, pin, pin_type, ts)
AS SELECT
    device_id,
    pin,
    pin_type,
    toStartOfMinute(ts) as ts,
    avgState(value) as value
FROM reporting_device_raw_data
GROUP BY device_id, pin, pin_type, ts;

CREATE MATERIALIZED VIEW reporting_average_5_minute
ENGINE = AggregatingMergeTree()
PARTITION BY toYYYYMM(ts)
ORDER BY (device_id, pin, pin_type, ts)
AS SELECT
    device_id,
    pin,
    pin_type,
    toStartOfFiveMinute(ts) as ts,
    avgState(value) as value
FROM reporting_device_raw_data
GROUP BY device_id, pin, pin_type, ts;

CREATE MATERIALIZED VIEW reporting_average_15_minute
ENGINE = AggregatingMergeTree()
PARTITION BY toYYYYMM(ts)
ORDER BY (device_id, pin, pin_type, ts)
AS SELECT
    device_id,
    pin,
    pin_type,
    toStartOfFifteenMinutes(ts) as ts,
    avgState(value) as value
FROM reporting_device_raw_data
GROUP BY device_id, pin, pin_type, ts;

CREATE MATERIALIZED VIEW reporting_average_hourly
ENGINE = AggregatingMergeTree()
PARTITION BY toYYYYMM(ts)
ORDER BY (device_id, pin, pin_type, ts)
AS SELECT
    device_id,
    pin,
    pin_type,
    toStartOfHour(ts) as ts,
    avgState(value) as value
FROM reporting_device_raw_data
GROUP BY device_id, pin, pin_type, ts;

CREATE MATERIALIZED VIEW reporting_average_daily
ENGINE = AggregatingMergeTree()
ORDER BY (device_id, pin, pin_type, ts)
AS SELECT
    device_id,
    pin,
    pin_type,
    toStartOfDay(ts) as ts,
    avgState(value) as value
FROM reporting_device_raw_data
GROUP BY device_id, pin, pin_type, ts;