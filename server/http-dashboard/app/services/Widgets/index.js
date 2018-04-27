import _ from 'lodash';

export const WIDGET_TYPES = {
  LABEL: 'WEB_LABEL',
  SLIDER: 'WEB_SLIDER',
  SWITCH: 'WEB_SWITCH',
  LINEAR: 'WEB_LINE_GRAPH',
  BAR: 'WEB_BAR_GRAPH',
};


export const buildDataQueryRequestForWidgets = ({ widgets, timeFrom, timeTo, deviceId }) => {
  let dataQueryRequests = [];

  const filterWidgetByTypes = (widget) => {
    // data builder should build queries only for charts
    return [WIDGET_TYPES.LINEAR, WIDGET_TYPES.BAR].indexOf(widget.type) >= 0;
  };

  widgets.filter(filterWidgetByTypes).forEach((widget) => {
    if (widget.sources && widget.sources.length) {

      widget.sources.forEach((source, sourceIndex) => {

        if (!source || !source.dataStream)
          return null;

        let pin = source.dataStream.pin;

        let timeFilter = {
          from: timeFrom,
          to: timeTo,
        };

        const additionalParams = {};

        if (source.selectedColumns && source.selectedColumns.length) {
          additionalParams.selectedColumns = source.selectedColumns;
        }

        if (source.groupByFields && source.groupByFields.length) {
          additionalParams.groupByFields = source.groupByFields;
        }

        if (source.sortByFields && source.sortByFields.length) {
          additionalParams.sortByFields = source.sortByFields;
        }

        dataQueryRequests.push({
          "deviceId": deviceId,
          "widgetId": widget.id,
          "sourceIndex": sourceIndex,
          "pinType": "VIRTUAL",
          "pin": pin,
          // "sortOrder": source.sortOrder || null,
          "sourceType": source.sourceType,
          "offset": 0,
          "limit": source.limit || 10000,
          ...timeFilter,
          ...additionalParams,
        });

      });
    }
  });

  return dataQueryRequests;

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
  [WIDGET_TYPES.SLIDER]: {
    minW: 3,
    minH: 1,
    maxH: 1,
    color: `e92126`
  },
  [WIDGET_TYPES.SWITCH]: {
    minW: 2,
    minH: 1,
    maxH: 1,
    color: `e92126`
  },
  [WIDGET_TYPES.LABEL]: {
    minW: 2,
    minH: 1,
    maxH: 3,
  },
  [WIDGET_TYPES.LINEAR]: {
    minW: 3,
    minH: 2
  },
  [WIDGET_TYPES.BAR]: {
    minW: 3,
    minH: 2,
    source: {
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
    }
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
  [WIDGET_TYPES.LINEAR]: {
    label: 'Source Label',
    sourceType: WIDGETS_SOURCE_TYPES.RAW_DATA,
    color: '007dc4',
    graphType: WIDGETS_CHART_TYPES.LINE, //LINE, FILLED_LINE, BAR, BINARY
    connectMissingPointsEnabled: false,
    dataStream: {}
  },
  [WIDGET_TYPES.LABEL]: {
    label: 'Source Label',
    sourceType: WIDGETS_SOURCE_TYPES.RAW_DATA,
    color: '007dc4',
    graphType: WIDGETS_CHART_TYPES.LINE, //LINE, FILLED_LINE, BAR, BINARY
    connectMissingPointsEnabled: false,
  },
  [WIDGET_TYPES.SLIDER]: {
    label: 'Source Label',
    sourceType: WIDGETS_SOURCE_TYPES.RAW_DATA,
    color: '007dc4'
  }
};

export const WIDGETS_LABEL_DATA_TYPES = {
  NUMBER: 'Number',
  STRING: 'String',
};

export const WIDGETS_LABEL_TEXT_ALIGNMENT = {
  LEFT: 'LEFT',
  RIGHT: 'RIGHT',
  CENTER: 'MIDDLE',
};

export const WIDGETS_LABEL_LEVEL_POSITION = {
  VERTICAL: 'VERTICAL',
  HORIZONTAL: 'HORIZONTAL',
};

export const WIDGETS_SWITCH_LABEL_ALIGNMENT = {
  LEFT: 'LEFT',
  RIGHT: 'RIGHT',
};

export const WIDGETS_SWITCH_ALIGNMENT = {
  LEFT: 'LEFT',
  CENTER: 'MIDDLE',
};

export const WIDGETS_SLIDER_VALUE_POSITION = {
  LEFT: 'LEFT',
  RIGHT: 'RIGHT',
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
        ...WIDGETS_PREDEFINED_SOURCE_OPTIONS[WIDGET_TYPES.LINEAR]
      }
    ],
    isShowTitleEnabled: false,
    isShowLegendEnabled: false,
    ...WIDGETS_CONFIGS[WIDGET_TYPES.LINEAR],
  },
  [WIDGET_TYPES.BAR]: {
    ...WIDGETS_CONFIGS[WIDGET_TYPES.BAR],
    type: WIDGET_TYPES.BAR,
    id: 0,
    x: 0,
    y: 0,
    sources: [
      {
        id: 1,
        ...WIDGETS_CONFIGS[WIDGET_TYPES.BAR].source
      }
    ],
    w: 3,
    h: 2,
    label: "Bar Chart",
  },
  [WIDGET_TYPES.LABEL]: {
    ...WIDGETS_CONFIGS[WIDGET_TYPES.LABEL],
    type: WIDGET_TYPES.LABEL,
    id: 0,
    x: 0,
    y: 0,
    w: 2,
    h: 1,
    label: "Label",
    sources: [
      {
        id: 1,
        ...WIDGETS_PREDEFINED_SOURCE_OPTIONS[WIDGET_TYPES.LABEL]
      }
    ],
    isColorSetEnabled: false,
    backgroundColor: '#fff',
    textColor: 'DEFAULT',
    dataType: WIDGETS_LABEL_DATA_TYPES.NUMBER,
    colorsSet: [
      {
        min: 0,
        max: 30,
        backgroundColor: '23be1b',
        textColor: 'fff',
        customText: '',
      },
      {
        min: 31,
        max: 60,
        backgroundColor: 'eb7a21',
        textColor: 'fff',
        customText: '',
      },
      {
        min: 61,
        max: 100,
        backgroundColor: 'da1d4e',
        textColor: 'fff',
        customText: '',
      },
    ],
    alignment: WIDGETS_LABEL_TEXT_ALIGNMENT.LEFT,
    level: {
      position: WIDGETS_LABEL_LEVEL_POSITION.VERTICAL,
      color: '007dc4',
      min: 0,
      max: 100,
    }
  },
  [WIDGET_TYPES.SWITCH]: {
    ...WIDGETS_CONFIGS[WIDGET_TYPES.SWITCH],
    type: WIDGET_TYPES.SWITCH,
    id: 0,
    x: 0,
    y: 0,
    w: 2,
    h: 1,
    label: "Switch",
    onValue: 1,
    offValue: 0,
    sources: [],
    alignment: WIDGETS_SWITCH_ALIGNMENT.LEFT,
    labelPosition: WIDGETS_SWITCH_LABEL_ALIGNMENT.LEFT,
  },
  [WIDGET_TYPES.SLIDER]: {
    ...WIDGETS_CONFIGS[WIDGET_TYPES.SLIDER],
    type: WIDGET_TYPES.SLIDER,
    id: 0,
    x: 0,
    y: 0,
    w: 3,
    h: 1,
    textColor: 'DEFAULT',
    label: "Slider",
    sources: [
      {
        id: 1,
        ...WIDGETS_PREDEFINED_SOURCE_OPTIONS[WIDGET_TYPES.SLIDER]
      }
    ],
    sendOnReleaseOn: true,
    step: 1,
    fineControlEnabled: true,
    fineControlStep: 1,
    valuePosition: WIDGETS_SWITCH_ALIGNMENT.LEFT,
    decimalFormat: '',
    valueSuffix: ''
  }
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



const DASHBOARD_WIDTH = {
  lg: 8,
};

export const getCoordinatesToSet = (newWidget, widgets = [], breakpoint) => {
  if(!widgets.length){
    return {x:0,y:0};
  }
  const highestWidget = widgets.reduce(function(prev, current) {
    return (prev.y+prev.h > current.y+current.h) ? prev : current;
  });
  const dashboardHeight = highestWidget.y + highestWidget.h + newWidget.h + 1;
  const dashboard = {
    x: 0,
    y: 0,
    w: DASHBOARD_WIDTH[breakpoint],
    h: dashboardHeight
  };
  return checkEachDashboardPoint(newWidget, widgets, dashboard);
};

const checkEachDashboardPoint = (newWidget, widgets, dashboard) => {
  let settableCoordinates;

  for (let i = 0; i < dashboard.h; i++) {
    for (let j = 0; j < dashboard.w; j++) {

      settableCoordinates = checkEachWidgetPoint(newWidget, i, j, dashboard, widgets);
      if (settableCoordinates) {

        return settableCoordinates;
      }
    }
  }

  return false;
};

const checkEachWidgetPoint = (newWidget, dashboardY, dashboardX, dashboard, widgets) => {

  for (let i = 0; i < newWidget.h; i++) {
    for (let j = 0; j < newWidget.w; j++) {

      const point = {x: dashboardX + j + 0.5, y: dashboardY + i + 0.5};
      if (overflowDashboard(point, dashboard)) {

          return false;
      }
      if (isCollapseWithWidgets(point, widgets)) {

        return false;
      }
    }
  }

  return {x: dashboardX, y: dashboardY};
};

const isCollapseWithWidgets = (point, allWidgets) => {
  for (let i = 0; i < allWidgets.length; i++) {
    if (isInWidget(point, allWidgets[i])) {

      return true;
    }
  }

  return false;
};

const isInWidget = (point, widget) => {

  const wCorners = getCornersOfWidget(widget);

  return pointInWidgetArea(point, widget, wCorners);
};

const overflowDashboard = (point, dashboard) => {
  const dashboardCorners = getCornersOfWidget(dashboard);

  return !pointInWidgetArea(point, dashboard, dashboardCorners);
};

const getCornersOfWidget = (widget) => {
  return {
    A: {x: widget.x,              y: widget.y},
    B: {x: (widget.x+widget.w), y: widget.y},
    C: {x: (widget.x+widget.w), y: (widget.y+widget.h)},
    D: {x: widget.x,              y: (widget.y+widget.h)}
  };
};

const pointInWidgetArea = (point, widget, wCorners) => {
  // Widget area
  const wArea = getWidgetArea(widget);
  const sumTrianglesArea = parseFloat((
    getTriangleArea(point, wCorners.A, wCorners.B) +
    getTriangleArea(point, wCorners.B, wCorners.C) +
    getTriangleArea(point, wCorners.C, wCorners.D) +
    getTriangleArea(point, wCorners.D, wCorners.A)
  ).toFixed(6));

  return wArea === sumTrianglesArea;
};

const getTriangleArea = (A, B, C) => {
  const triangleSides = {
    AB: getLineLengthByCoords(A, B),
    BC: getLineLengthByCoords(B, C),
    CA: getLineLengthByCoords(C, A)
  };
  // half of perimeter of triangle
  const hp = ((triangleSides.AB + triangleSides.BC + triangleSides.CA) / 2);

  return Math.sqrt(hp * (hp-triangleSides.AB) * (hp-triangleSides.BC) * (hp-triangleSides.CA));
};

const getLineLengthByCoords = (startPoint, endPoint) => {

  return Math.sqrt(Math.pow((endPoint.x - startPoint.x), 2) + Math.pow((endPoint.y - startPoint.y), 2));
};

const getWidgetArea = (widget) => {
  return (
    getLineLengthByCoords({x: widget.x, y: widget.y}, {x: (widget.x + widget.w), y: widget.y})
    *
    getLineLengthByCoords({x: widget.x, y: widget.y}, {x: widget.x, y: (widget.y + widget.h)})
  );
};
