import moment from 'moment';

export const HARDWARES = {
  "Arduino 101": {
    "key": "Arduino 101",
    "value": "Arduino 101"
  },
  "Arduino Due": {
    "key": "Arduino Due",
    "value": "Arduino Due"
  },
  "Arduino Leonardo": {
    "key": "Arduino Leonardo",
    "value": "Arduino Leonardo"
  },
  "Arduino Mega": {
    "key": "Arduino Mega",
    "value": "Arduino Mega"
  },
  "Arduino Micro": {
    "key": "Arduino Micro",
    "value": "Arduino Micro"
  },
  "Arduino Mini": {
    "key": "Arduino Mini",
    "value": "Arduino Mini"
  },
  "Arduino MKR1000": {
    "key": "Arduino MKR1000",
    "value": "Arduino MKR1000"
  },
  "Arduino Nano": {
    "key": "Arduino Nano",
    "value": "Arduino Nano"
  },
  "Arduino Pro Micro": {
    "key": "Arduino Pro Micro",
    "value": "Arduino Pro Micro"
  },
  "Arduino Pro Mini": {
    "key": "Arduino Pro Mini",
    "value": "Arduino Pro Mini"
  },
  "Arduino UNO": {
    "key": "Arduino UNO",
    "value": "Arduino UNO"
  },
  "Arduino Yun": {
    "key": "Arduino Yun",
    "value": "Arduino Yun"
  },
  "Arduino Zero": {
    "key": "Arduino Zero",
    "value": "Arduino Zero"
  },
  "ESP8266": {
    "key": "ESP8266",
    "value": "ESP8266"
  },
  "Generic Board": {
    "key": "Generic Board",
    "value": "Generic Board"
  },
  "Intel Edison": {
    "key": "Intel Edison",
    "value": "Intel Edison"
  },
  "Intel Galileo": {
    "key": "Intel Galileo",
    "value": "Intel Galileo"
  },
  "LinkIt ONE": {
    "key": "LinkIt ONE",
    "value": "LinkIt ONE"
  },
  "Microduino Core+": {
    "key": "Microduino Core+",
    "value": "Microduino Core+"
  },
  "Microduino Core": {
    "key": "Microduino Core",
    "value": "Microduino Core"
  },
  "Microduino CoreRF": {
    "key": "Microduino CoreRF",
    "value": "Microduino CoreRF"
  },
  "Microduino CoreUSB": {
    "key": "Microduino CoreUSB",
    "value": "Microduino CoreUSB"
  },
  "NodeMCU": {
    "key": "NodeMCU",
    "value": "NodeMCU"
  },
  "Particle Core": {
    "key": "Particle Core",
    "value": "Particle Core"
  },
  "Particle Electron": {
    "key": "Particle Electron",
    "value": "Particle Electron"
  },
  "Particle Photon": {
    "key": "Particle Photon",
    "value": "Particle Photon"
  },
  "Raspberry Pi 3 B": {
    "key": "Raspberry Pi 3 B",
    "value": "Raspberry Pi 3 B"
  },
  "Raspberry Pi 2/A+/B+": {
    "key": "Raspberry Pi 2/A+/B+",
    "value": "Raspberry Pi 2/A+/B+"
  },
  "Raspberry Pi B (Rev1)": {
    "key": "Raspberry Pi B (Rev1)",
    "value": "Raspberry Pi B (Rev1)"
  },
  "Raspberry Pi A/B (Rev2)": {
    "key": "Raspberry Pi A/B (Rev2)",
    "value": "Raspberry Pi A/B (Rev2)"
  },
  "RedBearLab CC3200/Mini": {
    "key": "RedBearLab CC3200/Mini",
    "value": "RedBearLab CC3200/Mini"
  },
  "Seeed Wio Link": {
    "key": "Seeed Wio Link",
    "value": "Seeed Wio Link"
  },
  "SparkFun Blynk Board": {
    "key": "SparkFun Blynk Board",
    "value": "SparkFun Blynk Board"
  },
  "SparkFun ESP8266 Thing": {
    "key": "SparkFun ESP8266 Thing",
    "value": "SparkFun ESP8266 Thing"
  },
  "SparkFun Photon RedBoard": {
    "key": "SparkFun Photon RedBoard",
    "value": "SparkFun Photon RedBoard"
  },
  "TI CC3200-LaunchXL": {
    "key": "TI CC3200-LaunchXL",
    "value": "TI CC3200-LaunchXL"
  },
  "TI Tiva C Connected": {
    "key": "TI Tiva C Connected",
    "value": "TI Tiva C Connected"
  },
  "TinyDuino": {
    "key": "TinyDuino",
    "value": "TinyDuino"
  },
  "WeMos D1": {
    "key": "WeMos D1",
    "value": "WeMos D1"
  },
  "WeMos D1 mini": {
    "key": "WeMos D1 mini",
    "value": "WeMos D1 mini"
  },
  "Wildfire v2": {
    "key": "Wildfire v2",
    "value": "Wildfire v2"
  },
  "Wildfire v3": {
    "key": "Wildfire v3",
    "value": "Wildfire v3"
  },
  "Wildfire v4": {
    "key": "Wildfire v4",
    "value": "Wildfire v4"
  },
  "WiPy": {
    "key": "WiPy",
    "value": "WiPy"
  },
  "BBC Micro:bit": {
    "key": "BBC Micro:bit",
    "value": "BBC Micro:bit"
  },
  "Bluz": {
    "key": "Bluz",
    "value": "Bluz"
  },
  "chipKIT Uno32": {
    "key": "chipKIT Uno32",
    "value": "chipKIT Uno32"
  },
  "Digistump Digispark": {
    "key": "Digistump Digispark",
    "value": "Digistump Digispark"
  },
  "Digistump Oak": {
    "key": "Digistump Oak",
    "value": "Digistump Oak"
  },
  "ESP32 Dev Board": {
    "key": "ESP32 Dev Board",
    "value": "ESP32 Dev Board"
  },
  "Konekt Dash": {
    "key": "Konekt Dash",
    "value": "Konekt Dash"
  },
  "Konekt Dash Pro": {
    "key": "Konekt Dash Pro",
    "value": "Konekt Dash Pro"
  },
  "LeMaker Banana Pro": {
    "key": "LeMaker Banana Pro",
    "value": "LeMaker Banana Pro"
  },
  "LeMaker Guitar": {
    "key": "LeMaker Guitar",
    "value": "LeMaker Guitar"
  },
  "LightBlue Bean": {
    "key": "LightBlue Bean",
    "value": "LightBlue Bean"
  },
  "LightBlue Bean+": {
    "key": "LightBlue Bean+",
    "value": "LightBlue Bean+"
  },
  "Onion Omega": {
    "key": "Onion Omega",
    "value": "Onion Omega"
  },
  "panStamp exp-output": {
    "key": "panStamp exp-output",
    "value": "panStamp exp-output"
  },
  "RedBear Duo": {
    "key": "RedBear Duo",
    "value": "RedBear Duo"
  },
  "RedBearLab BLE Nano": {
    "key": "RedBearLab BLE Nano",
    "value": "RedBearLab BLE Nano"
  },
  "RedBearLAb Blend Micro": {
    "key": "RedBearLAb Blend Micro",
    "value": "RedBearLAb Blend Micro"
  },
  "Samsung ARTIK 5": {
    "key": "Samsung ARTIK 5",
    "value": "Samsung ARTIK 5"
  },
  "Simbee": {
    "key": "Simbee",
    "value": "Simbee"
  },
  "STM32F103C Blue Pill": {
    "key": "STM32F103C Blue Pill",
    "value": "STM32F103C Blue Pill"
  },
  "Teensy 3": {
    "key": "Teensy 3",
    "value": "Teensy 3"
  },
  "The AirBoard": {
    "key": "The AirBoard",
    "value": "The AirBoard"
  },
  "TI LM4F120 LaunchPad": {
    "key": "TI LM4F120 LaunchPad",
    "value": "TI LM4F120 LaunchPad"
  },
  "Other": {
    "key": "Other",
    "value": "Other"
  }
};

export const CONNECTIONS_TYPES = {
  'ETHERNET': {
    key: 'ETHERNET',
    value: 'Ethernet'
  },
  'WIFI': {
    key: 'WI_FI',
    value: 'WiFi'
  },
  'USB': {
    key: 'USB',
    value: 'USB'
  },
  'BLUETOOTH': {
    key: 'BLUETOOTH',
    value: 'Bluetooth'
  },
  'BLE': {
    key: 'BLE',
    value: 'BLE'
  },
  'GSM': {
    key: 'GSM',
    value: 'GSM'
  }
};

export const AVAILABLE_HARDWARE_TYPES = [
  HARDWARES['Arduino 101'],
  HARDWARES['Arduino Due'],
  HARDWARES['Arduino Leonardo'],
  HARDWARES['Arduino Mega'],
  HARDWARES['Arduino Micro'],
  HARDWARES['Arduino Mini'],
  HARDWARES['Arduino MKR1000'],
  HARDWARES['Arduino Nano'],
  HARDWARES['Arduino Pro Micro'],
  HARDWARES['Arduino Pro Mini'],
  HARDWARES['Arduino UNO'],
  HARDWARES['Arduino Yun'],
  HARDWARES['Arduino Zero'],
  HARDWARES['ESP8266'],
  HARDWARES['Generic Board'],
  HARDWARES['Intel Edison'],
  HARDWARES['Intel Galileo'],
  HARDWARES['LinkIt ONE'],
  HARDWARES['Microduino Core+'],
  HARDWARES['Microduino Core'],
  HARDWARES['Microduino CoreRF'],
  HARDWARES['Microduino CoreUSB'],
  HARDWARES['NodeMCU'],
  HARDWARES['Particle Core'],
  HARDWARES['Particle Electron'],
  HARDWARES['Particle Photon'],
  HARDWARES['Raspberry Pi 3 B'],
  HARDWARES['Raspberry Pi 2/A+/B+'],
  HARDWARES['Raspberry Pi B (Rev1)'],
  HARDWARES['Raspberry Pi A/B (Rev2)'],
  HARDWARES['RedBearLab CC3200/Mini'],
  HARDWARES['Seeed Wio Link'],
  HARDWARES['SparkFun Blynk Board'],
  HARDWARES['SparkFun ESP8266 Thing'],
  HARDWARES['SparkFun Photon RedBoard'],
  HARDWARES['TI CC3200-LaunchXL'],
  HARDWARES['TI Tiva C Connected'],
  HARDWARES['TinyDuino'],
  HARDWARES['WeMos D1'],
  HARDWARES['WeMos D1 mini'],
  HARDWARES['Wildfire v2'],
  HARDWARES['Wildfire v3'],
  HARDWARES['Wildfire v4'],
  HARDWARES['WiPy'],
  HARDWARES['BBC Micro:bit'],
  HARDWARES['Bluz'],
  HARDWARES['chipKIT Uno32'],
  HARDWARES['Digistump Digispark'],
  HARDWARES['Digistump Oak'],
  HARDWARES['ESP32 Dev Board'],
  HARDWARES['Konekt Dash'],
  HARDWARES['Konekt Dash Pro'],
  HARDWARES['LeMaker Banana Pro'],
  HARDWARES['LeMaker Guitar'],
  HARDWARES['LightBlue Bean'],
  HARDWARES['LightBlue Bean+'],
  HARDWARES['Onion Omega'],
  HARDWARES['panStamp exp-output'],
  HARDWARES['RedBear Duo'],
  HARDWARES['RedBearLab BLE Nano'],
  HARDWARES['RedBearLAb Blend Micro'],
  HARDWARES['Samsung ARTIK 5'],
  HARDWARES['Simbee'],
  HARDWARES['STM32F103C Blue Pill'],
  HARDWARES['Teensy 3'],
  HARDWARES['The AirBoard'],
  HARDWARES['TI LM4F120 LaunchPad'],
  HARDWARES['Other'],
];

export const AVAILABLE_CONNECTION_TYPES = [
  CONNECTIONS_TYPES.ETHERNET,
  CONNECTIONS_TYPES.WIFI,
  CONNECTIONS_TYPES.USB,
  CONNECTIONS_TYPES.BLUETOOTH,
  CONNECTIONS_TYPES.BLE,
  CONNECTIONS_TYPES.GSM,
];

export const AVAILABLE_HARDWARE_TYPES_LIST = AVAILABLE_HARDWARE_TYPES.map((item) => item.key);

export const AVAILABLE_CONNECTION_TYPES_LIST = AVAILABLE_CONNECTION_TYPES.map((item) => item.key);

export const DEFAULT_HARDWARE_TYPE = HARDWARES["Particle Electron"].key;
export const DEFAULT_CONNECTION_TYPE = CONNECTIONS_TYPES["GSM"].key;

export const TIMELINE_TYPE_FILTERS = {
  'ALL': {
    'key': 'ALL',
    'value': 'All events'
  },
  'CRITICAL': {
    'key': 'CRITICAL',
    'value': 'Critical'
  },
  'WARNING': {
    'key': 'WARNING',
    'value': 'Warning'
  },
  'RESOLVED': {
    'key': 'RESOLVED',
    'value': 'Resolved'
  },
};

export const STATUS = {
  ONLINE: 'ONLINE',
  OFFLINE: 'OFFLINE'
};

export const TIMELINE_TIME_FILTERS = {
  'LIVE': {
    'key': 'LIVE',
    'value': 'Live',
    'time': 60 * 60 * 1000,
    'get': () => moment().subtract(1, 'hour').valueOf()
  },
  'HOUR': {
    'key': 'HOUR',
    'value': '1 hour',
    'time': 60 * 60 * 1000,
    'get': () => moment().subtract(1, 'hour').valueOf()
  },
  'DAY': {
    'key': 'DAY',
    'value': '1 day',
    'time': 24 * 60 * 60 * 1000,
    'get': () => moment().subtract(1, 'day').valueOf()
  },
  'WEEK': {
    'key': 'WEEK',
    'value': '1 week',
    'time': 7 * 24 * 60 * 60 * 1000,
    'get': () => moment().subtract(1, 'week').valueOf()
  },
  'MONTH': {
    'key': 'MONTH',
    'value': 'Month',
    'time': 30 * 24 * 60 * 60 * 1000,
    'get': () => moment().subtract(1, 'month').valueOf()
  },
  'CUSTOM': {
    'key': 'CUSTOM',
    'value': 'Custom Range'
  }
};

export const DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME = 'DEVICE_DASHBOARD_TIME_FILTERING';

export const TIMELINE_ITEMS_PER_PAGE = 25;

export const DEVICES_SORT = {
  REQUIRE_ATTENTION: {
    key: 'REQUIRE_ATTENTION',
    compare: (a, b) => {
      const aCritical = a.criticalSinceLastView || 0;
      const aWarning = a.warningSinceLastView || 0;
      const bCritical = b.criticalSinceLastView || 0;
      const bWarning = b.warningSinceLastView || 0;
      if (aCritical === bCritical) {
        return (aWarning > bWarning) ? -1 : (aWarning < bWarning) ? 1 : 0;
      } else {
        return (aCritical > bCritical) ? -1 : 1;
      }
    }
  },
  AZ: {
    key: 'AZ',
    compare: (a, b) => {
      return String(a.name) > String(b.name) ? 1 : -1;
    }
  },
  ZA: {
    key: 'ZA',
    compare: (a, b) => {
      return String(a.name) > String(b.name) ? -1 : 1;
    }
  },
  DATE_ADDED_ASC: {
    key: 'DATE_ADDED_ASC',
    compare: (a, b) => {
      return Number(a.createdAt) > Number(b.createdAt) ? -1 : 1;
    }
  },
  DATE_ADDED_DESC: {
    key: 'DATE_ADDED_DESC',
    compare: (a, b) => {
      return Number(a.createdAt) > Number(b.createdAt) ? 1 : -1;
    }
  },
  LAST_REPORTED_ASC: {
    key: 'LAST_REPORTED_ASC',
    compare: (a, b) => {
      return Number(a.dataReceivedAt) > Number(b.dataReceivedAt) ? -1 : 1;
    }
  },
  LAST_REPORTED_DESC: {
    key: 'LAST_REPORTED_DESC',
    compare: (a, b) => {
      return Number(a.dataReceivedAt) > Number(b.dataReceivedAt) ? 1 : -1;
    },
  },

};

export const FILTERED_DEVICES_SORT = {
  REQUIRE_ATTENTION: {
    key: 'REQUIRE_ATTENTION',
    compare: (a, b) => {
      const aCritical = a.items.reduce((val, device) => val + (device.criticalSinceLastView || 0), 0);
      const bCritical = b.items.reduce((val, device) => val + (device.criticalSinceLastView || 0), 0);
      const aWarning = a.items.reduce((val, device) => val + (device.warningSinceLastView || 0), 0);
      const bWarning = b.items.reduce((val, device) => val + (device.warningSinceLastView || 0), 0);

      if (aCritical === bCritical) {
        return (aWarning > bWarning) ? -1 : (aWarning < bWarning) ? 1 : 0;
      } else {
        return (aCritical > bCritical) ? -1 : 1;
      }
    }
  },
  AZ: {
    key: 'AZ',
    compare: (a, b) => {
      if(a.isOthers)
        return 1;
      if(b.isOthers)
        return -1;
      return String(a.name) > String(b.name) ? 1 : -1;
    }
  },
  ZA: {
    key: 'ZA',
    compare: (a, b) => {
      if(a.isOthers)
        return 1;
      if(b.isOthers)
        return -1;
      return String(a.name) > String(b.name) ? -1 : 1;
    }
  },
};


export const DEVICES_SEARCH_FORM_NAME = 'devicesSearchForm';

export const DEVICES_FILTER_FORM_NAME = 'devicesFilterForm';

export const DEVICES_FILTERS = {
  DEFAULT: 'ALL_DEVICES',
  ALL_DEVICES: 'ALL_DEVICES',
  BY_LOCATION: 'BY_LOCATION',
  BY_PRODUCT: 'BY_PRODUCT'
};
