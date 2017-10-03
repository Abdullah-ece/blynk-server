import _ from 'lodash';

export const WIDGET_TYPES = {
  LINEAR: 'WEB_GRAPH',
  BAR: 'WEB_BAR',
};

export const WIDGETS_CONFIGS = {
  [WIDGET_TYPES.LINEAR]: {
    minW: 3,
    minH: 2
  }
};

export const WIDGETS_CHART_TYPES = {
  'LINE': 'LINE',
  'DOTS': 'DOTS',
};

export const WIDGETS_CHART_TYPES_LIST = [
  {
    key: WIDGETS_CHART_TYPES.LINE,
    value: 'Line'
  },
  {
    key: WIDGETS_CHART_TYPES.DOTS,
    value: 'Dots'
  }
];

export const WIDGETS_SOURCE_TYPES = {
  'RAW_DATA': 'RAW_DATA',
  'SUM': 'SUM',
  'AVG': 'AVG',
  'MED': 'MED',
  'MIN': 'MIN',
  'MAX': 'MAX',
  'COUNT': 'COUNT',
  'CUMULATIVE_COUNT': 'CUMULATIVE_COUNT',
};

export const WIDGETS_SOURCE_TYPES_LIST = [
  {
    key: WIDGETS_SOURCE_TYPES.RAW_DATA,
    value: 'Raw Data'
  },
  {
    key: WIDGETS_SOURCE_TYPES.SUM,
    value: 'SUM of'
  },
  {
    key: WIDGETS_SOURCE_TYPES.AVG,
    value: 'AVG of'
  },
  {
    key: WIDGETS_SOURCE_TYPES.MED,
    value: 'MED of'
  },
  {
    key: WIDGETS_SOURCE_TYPES.MIN,
    value: 'MIN of'
  },
  {
    key: WIDGETS_SOURCE_TYPES.MAX,
    value: 'MAX of'
  },
  {
    key: WIDGETS_SOURCE_TYPES.COUNT,
    value: 'COUNT of'
  },
  {
    key: WIDGETS_SOURCE_TYPES.CUMULATIVE_COUNT,
    value: 'Cumulative Count of'
  },


];

export const WIDGETS_PREDEFINED_SOURCE_OPTIONS = {
  label: 'Source Label',
  sourceType: WIDGETS_SOURCE_TYPES.RAW_DATA,
  color: '007dc4',
  graphType: WIDGETS_CHART_TYPES.LINE, //LINE, FILLED_LINE, BAR, BINARY
  connectMissingPointsEnabled: false,
  dataStream: {}
};

export const WIDGETS_PREDEFINED_OPTIONS = {
  [WIDGET_TYPES.LINEAR]: {
    id: 1,
    type: WIDGET_TYPES.LINEAR,
    label: 'Chart',
    x: 0,
    y: 0,
    w: 3,
    h: 2,
    sources: [
      {
        id: 1,
        ...WIDGETS_PREDEFINED_SOURCE_OPTIONS
      }
    ],
    isShowTitleEnabled: false,
    isShowLegendEnabled: false,
    ...WIDGETS_CONFIGS[WIDGET_TYPES.LINEAR],
  },
  [WIDGET_TYPES.BAR]: {
    type: "WEB_BAR",
    id: 0,
    x: 0,
    y: 0,
    color: 0,
    w: 3,
    h: 2,
    maxRows: 0,
    dataType: "",  // raw data etc.
    dataSource: "", // dropdown
    groupDataBy: "", // dropdown
    sortBy: "", // dropdown
    sortType: "", // ASC or DESC
    label: "Bar Chart",
  }
};


export const prepareWidgetForProductEdit = (widget) => {

  const getConfigByWidgetType = (type) => {
    return WIDGETS_CONFIGS[type] || {};
  };

  let sources = widget.sources || [];

  if (sources && sources.length)
    sources = sources.map((source) => {

      let dataStreamPin = null;

      if (source.dataStream && source.dataStream.pin !== undefined)
        dataStreamPin = source.dataStream.pin;

      return {
        id: _.random(1,999999999),
        ...source,
        dataStreamPin
      };
    });

  return {
    ...widget,
    w: widget.width,
    h: widget.height,
    i: widget.id.toString(),
    static: false,
    moved: false,
    ...getConfigByWidgetType(widget.type),
    sources,
  };
};

export const VIRTUAL_PIN_PREFIX = 'V';
