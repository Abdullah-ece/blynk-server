import _ from 'lodash';

export const WIDGET_TYPES = {
  LABEL: 'WEB_LABEL',
  LINEAR: 'WEB_LINE_GRAPH',
  BAR: 'WEB_BAR_GRAPH',
};

export const BAR_CHART_PARAMS = {

  MAX_ROWS: {
    list: [
      {
        key: '0',
        value: 'None'
      },
      {
        key: '5',
        value: '5'
      },
      {
        key: '10',
        value: '10'
      },
      {
        key: '15',
        value: '15'
      },
      {
        key: '20',
        value: '20'
      },
    ],
    defaultKey: '0'
  },

  DATA_TYPE: {
    list: [
      {
        key: 'RAW_DATA',
        value: 'Raw Data',
      },
      {
        key: 'SUM',
        value: 'SUM of',
      },
      {
        key: 'AVG',
        value: 'AVG of',
      },
      {
        key: 'MED',
        value: 'MED of',
      },
      {
        key: 'MIN',
        value: 'MIN of',
      },
      {
        key: 'MAX',
        value: 'MAX of',
      },
      {
        key: 'COUNT',
        value: 'COUNT of',
      },
    ],
    defaultKey: 'RAW_DATA'
  },

  DATA_SOURCE: {
    list: {
      'Data Streams': [
        {
          key: 'Flow Rate',
          value: 'Flow Rate'
        },
        {
          key: 'Temperature',
          value: 'Temperature'
        },
        {
          key: 'Humidity',
          value: 'Humidity'
        },
      ],
      'Metadata': [
        {
          key: 'Cycles',
          value: 'Cycles'
        },
        {
          key: 'Shifts',
          value: 'Shifts'
        },
        {
          key: 'Formula Names',
          value: 'Formula Names'
        },
        {
          key: 'Pumps',
          value: 'Pumps'
        },
      ],
      'Platform': [
        {
          key: 'Devices',
          value: 'Devices'
        },
        {
          key: 'Products',
          value: 'Products'
        },
        {
          key: 'Organizations',
          value: 'Organizations'
        },
      ]
    }
  },

  GROUP_BY: {
    list: {
      'Data Streams': [
        {
          key: 'Flow Rate',
          value: 'Flow Rate'
        },
        {
          key: 'Temperature',
          value: 'Temperature'
        },
        {
          key: 'Humidity',
          value: 'Humidity'
        },
      ],
      'Metadata': [
        {
          key: 'Cycles',
          value: 'Cycles'
        },
        {
          key: 'Shifts',
          value: 'Shifts'
        },
        {
          key: 'Formula Names',
          value: 'Formula Names'
        },
        {
          key: 'Pumps',
          value: 'Pumps'
        },
      ],
      'Platform': [
        {
          key: 'Devices',
          value: 'Devices'
        },
        {
          key: 'Products',
          value: 'Products'
        },
        {
          key: 'Organizations',
          value: 'Organizations'
        },
      ]
    }
  },

  SORT_BY: {
    list: [
      {
        key: 'Cycles',
        value: 'Cycles'
      },
      {
        key: 'Shifts',
        value: 'Shifts'
      },
      {
        key: 'Formula Names',
        value: 'Formula Names'
      },
      {
        key: 'Pumps',
        value: 'Pumps'
      },
    ]
  },

  SORT_BY_ORDER: {
    list: [
      {
        key: 'ASC',
        value: 'Ascending'
      },
      {
        key: 'DESC',
        value: 'Descending'
      },
    ],
    defaultKey: 'ASC'
  },

  COLOR: {
    defaultKey: '007dc4'
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

export const WIDGETS_CONFIGS = {
  [WIDGET_TYPES.LABEL]: {
    'sourceType': WIDGETS_SOURCE_TYPES.RAW_DATA
  },
  [WIDGET_TYPES.LINEAR]: {
    minW: 3,
    minH: 2
  },
  [WIDGET_TYPES.BAR]: {
    'sourceType' : BAR_CHART_PARAMS.DATA_TYPE.defaultKey,
    'dataStream' : {},
    'selectedColumns' : [
      // {
      //   'name': 'load_weight',
      //   'label': 'Load Weight',
      //   'type': 'COLUMN'
      // }
    ],
    'groupByFields' : [
      // {
      //   'name': 'Shift 1',
      //   'type': 'METADATA'
      // }
    ],
    'sortByFields' : [
      //   {
      //   'name' : 'load_weight',
      //   'label' : 'Load Weight',
      //   'type' : 'COLUMN'
      // }
    ],
    'sortOrder' : BAR_CHART_PARAMS.SORT_BY_ORDER.defaultKey,
    'limit' : BAR_CHART_PARAMS.MAX_ROWS.defaultKey,
    'color': BAR_CHART_PARAMS.COLOR.defaultKey,
  },
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
    type: WIDGET_TYPES.BAR,
    id: 0,
    x: 0,
    y: 0,
    sources: [
      {
        id: 1,
        ...WIDGETS_CONFIGS[WIDGET_TYPES.BAR]
      }
    ],
    w: 3,
    h: 2,
    label: "Bar Chart",
  },
  [WIDGET_TYPES.LABEL]: {
    type: WIDGET_TYPES.LABEL,
    id: 0,
    x: 0,
    y: 0,
    w: 3,
    h: 2,
    label: "Label",
    sources: [
      {
        id: 1,
        ...WIDGETS_CONFIGS[WIDGET_TYPES.LABEL],
        isColorSetEnabled: false,
        backgroundColor: '#fff',
        textColor: '000'
      }
    ]
  }
};

export const WIDGETS_LABEL_DATA_FORMATS = {
  NUMBER: 'NUMBER',
  STRING: 'STRING',
};

export const WIDGETS_LABEL_TEXT_ALIGNMENT = {
  LEFT: 'LEFT',
  RIGHT: 'RIGHT',
  CENTER: 'CENTER',
};

export const WIDGETS_LABEL_LEVEL_POSITION = {
  VERTICAL: 'VERTICAL',
  HORIZONTAL: 'HORIZONTAL',
};

export const prepareWidgetForProductEdit = (widget) => {

  const getConfigByWidgetType = (type) => {
    if(type === WIDGET_TYPES.BAR) return {};

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
