export const WIDGET_TYPES = {
  LINEAR: 'WEB_GRAPH'
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
        label: 'Source Label',
        sourceType: WIDGETS_SOURCE_TYPES.RAW_DATA,
        color: '',
        graphType: WIDGETS_CHART_TYPES.LINE, //LINE, FILLED_LINE, BAR, BINARY
        connectMissingPointsEnabled: false,
        dataStream: {}
      }
    ],
    isShowTitleEnabled: false,
    isShowLegendEnabled: false,
    ...WIDGETS_CONFIGS[WIDGET_TYPES.LINEAR],
  }
};


export const prepareWidgetForProductEdit = (widget) => {
  let sources = widget.sources;

  if (sources.length)
    sources = sources.map((source) => {

      let dataStreamId = null;

      if (source.dataStream && source.dataStream.id)
        dataStreamId = source.dataStream.id;

      return {
        ...source,
        dataStreamId
      };
    });

  return {
    ...widget,
    w: widget.width,
    h: widget.height,
    i: widget.id.toString(),
    static: false,
    moved: false,
    sources
  };
};
