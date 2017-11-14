import React from 'react';

import {Chart} from 'components';

import {Map} from 'immutable';

import PropTypes from 'prop-types';

import {connect} from 'react-redux';

import BarChartSettings from './settings';

@connect((state) => ({
  widgets: state.Widgets && state.Widgets.get('widgetsData'),
}))
class BarChartWidget extends React.Component {

  static propTypes = {
    fetchRealData: PropTypes.bool,

    widgets: PropTypes.instanceOf(Map),

    params: PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    }),

    data: PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      w: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      h: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      sources: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      }))
    }),

    fakeData: PropTypes.shape({
      x: PropTypes.arrayOf(PropTypes.number),
      y: PropTypes.arrayOf(PropTypes.string)
    })
  };

  constructor(props) {
    super(props);

    this.handleHover = this.handleHover.bind(this);
    this.handleUnhover = this.handleUnhover.bind(this);
    this.handleMouseMove = this.handleMouseMove.bind(this);
    this.handleChartMouseEnter = this.handleChartMouseEnter.bind(this);
    this.handleChartMouseLeave = this.handleChartMouseLeave.bind(this);
    this.handleDataPointBlur = this.handleDataPointBlur.bind(this);
    this.handleDataPointHover = this.handleDataPointHover.bind(this);
  }

  handleChartMouseEnter() {
    this.hoverElement.style.display = 'block';
  }

  handleChartMouseLeave() {
    this.hoverElement.style.display = 'none';
  }

  handleHover(data, container, plotly) {

    const hoverColor = 'rgba(33,179,130, .28)';
    const staticColor = 'rgba(33,179,130, 1)';

    const color = new Array(data.points[0].data.x.length).fill(true).map((color, i) => i === data.points[0].pointNumber ? staticColor : hoverColor);

    const update = {
      marker: {
        color: color
      }
    };

    plotly.restyle(container, update, 0);

    this.hoverElement.innerHTML = `${data.points[0].x}`;

    this.hoverElement.style.opacity = 1;

    this.repositionHoverElement(data.event);

  }

  repositionHoverElement(event) {
    const boundingClient = this.chartElement.getBoundingClientRect();
    const hoverBoundingClient = this.hoverElement.getBoundingClientRect();

    let x = event.clientX - boundingClient.left;
    let y = event.clientY - boundingClient.top;

    this.hoverElement.style.top = `${y + 10 + (hoverBoundingClient.height / 2)}px`;
    this.hoverElement.style.left = `${x + 40}px`;
  }

  handleUnhover(data, container, plotly) {

    const staticColor = 'rgba(33,179,130, 1)';

    const update = {
      marker: {
        color: staticColor
      }
    };

    plotly.restyle(container, update, 0);

    this.hoverElement.style.opacity = 0;
  }

  handleMouseMove(event) {
    this.repositionHoverElement(event);
  }

  layout = {
    hovermode: 'y',
    hoverlabel: {
      bgcolor: 'transparent',
      bordercolor: 'transparent',
      font: {
        color: 'transparent'
      },
      bgcolorsrc: 'transparent',
    },
    hoveron: 'tonext',
    margin: {
      t: 25,
      b: 10,
      r: 15,
      l: 65,
    },
    yaxis: {
      type: 'category',
      fixedrange: true,
      showline: false,
      linecolor: 'rgb(204,204,204)',
      ticklen: 8,
      tickcolor: '#fff',
      tickfont: {
        family: 'PF DinDisplay Pro',
        size: '12',
        color: '#212227',
      },
      zeroline: false,
      hoverlabel: {
        bgcolor: 'transparent',
        bordercolor: 'transparent',
        font: {
          color: 'transparent'
        },
        bgcolorsrc: 'transparent',
      },
    },
    xaxis: {
      fixedrange: true,
      side: 'top',
      zeroline: true,
      showline: false,
      zerolinecolor: '#e2e2e2',
      linecolor: 'rgb(204,204,204)',
      tickfont: {
        family: 'PF DinDisplay Pro',
        size: '12',
        color: '#9a9a9a'
      },
      ticklen: 8,
      tickangle: 0,
      nticks: 12,
      tickcolor: '#fff',
    },
  };

  legendConfig = {
    width: [0.27, 0.27, 0.27],
    type: 'bar',
    orientation: 'h',
    marker: {color: 'rgba(33,179,130, 1)'},
    hoveron: 'points',
    hoverinfo: 'y',
  };

  dataChartConfig = {};

  config = {
    displayModeBar: false
  };

  hoverColor = '#B5F3E0';
  defaultColor = '#29DEAF';

  handleDataPointHover(e){
    const length = e.dataSeries.dataPoints.length;
    for(let i = 0; i < length; i++){
      e.dataSeries.dataPoints[i].color = this.hoverColor;
    }
    e.dataPoint.color = this.defaultColor;
    return e.dataSeries.dataPoints;
  }
  handleDataPointBlur(e) {
    const length = e.dataSeries.dataPoints.length;
    for(let i = 0; i < length; i++){
      e.dataSeries.dataPoints[i].color = this.defaultColor;
    }
    return e.dataSeries.dataPoints;
  }

  renderChartByParams(data = [], config = {}, layout = {}) {
    let maxYLabelWidth = 0;

    data.map((legend) => {
      // calculate max Y label width
      if (legend && Array.isArray(legend.y) && legend.y.length) {
        legend.y.forEach((label = '') => {
          if (String(label).length)
            maxYLabelWidth = label.length > maxYLabelWidth ? label.length : maxYLabelWidth;
        });
      }

      //calculate width of bars

      const countOfYLabels = legend && Array.isArray(legend.y) && legend.y.length || 1;

      const CELL_HEIGHT = 101;
      const TOP_PADDING = 50;
      const BOTTOM_PADDING = 30;
      const CHART_HEIGHT = this.props.data.h * CELL_HEIGHT - TOP_PADDING - BOTTOM_PADDING;
      const MAX_RATIO_BAR_WIDTH_TO_CHART = 0.9;

      const MIN_BAR_WIDTH_PX = 8;
      const MAX_BAR_WIDTH_PX = 40;

      const MIN_BAR_OFFSET_PX = 8;
      const MAX_BAR_OFFSET_PX = 16;

      const maxHeightForBar = CHART_HEIGHT / countOfYLabels;

      if (maxHeightForBar < MIN_BAR_WIDTH_PX + MIN_BAR_OFFSET_PX) {
        // if height is smaller then min height of bar
        legend.width = (new Array(countOfYLabels)).fill(MAX_RATIO_BAR_WIDTH_TO_CHART);
      } else if (maxHeightForBar >= (MIN_BAR_WIDTH_PX + MIN_BAR_OFFSET_PX) && maxHeightForBar <= (MAX_BAR_WIDTH_PX + MAX_BAR_OFFSET_PX)) {
        // if height is smaller than max and bigger then min

        let barHeight = maxHeightForBar / (MAX_BAR_WIDTH_PX + MAX_BAR_OFFSET_PX) * MAX_BAR_WIDTH_PX;

        let barWidthPercent = barHeight / maxHeightForBar;

        legend.width = (new Array(countOfYLabels)).fill(barWidthPercent);


      } else if (maxHeightForBar > (MAX_BAR_WIDTH_PX + MAX_BAR_OFFSET_PX)) {
        // if height is bigger than max

        let barHeightPercent = MAX_BAR_WIDTH_PX / maxHeightForBar;

        legend.width = (new Array(countOfYLabels)).fill(barHeightPercent);
      }

      return legend;
    });

    if (layout && layout.margin && layout.margin.l) {

      const AVG_SYMBOL_LENGTH = 6;

      const ADDITIONAL_OFFSET = 12;

      layout.margin.l = Math.round(maxYLabelWidth * AVG_SYMBOL_LENGTH) + ADDITIONAL_OFFSET;
    }
    config = {
      toolTip:{
        content: "${y}",
        cornerRadius: 6,
        borderThickness: 0,
        borderColor:"white",
        backgroundColor:" white"
      },
      colorSet: "default",
      title: {
        fontWeight: "bold",
        fontFamily: "Arial",
        fontColor: "#17161A",
        horizontalAlign: "left",
        text: "Total Cost Per Product, $"
      },

      axisY2:{
        gridColor: "#F5F5F5",
        gridThickness: 1,
        lineColor: "white",
        tickColor: "white",
        labelFontColor: "#646368"
      },
      axisX: {
        labelFontSize: "12px",
        labelFontColor: "#3A3A3F",
        labelFontFamily: "Arial",
        lineColor: "#F5F5F5",
        tickColor: "white"
      },
      data: [
        {
          highlightEnabled: false,
          axisYType: "secondary",
          type: "bar",
        }
      ]
    };
    const colorSets = [
      {
        name:'default',
        colors: ['#29DEAF']
      },
      {
        name: 'hover',
        colors: ['#B5F3E0']
      }
    ];
    const length = data[0].x.length;
    let dataSource = [];
    for(let i = 0; i < length; i++) {
      dataSource.push({
        label: data[0].y[i],
            y: data[0].x[i]
      });
    }
    config.data[0].dataPoints = dataSource;
    return (
      <div className="widgets--widget-container">
        <Chart config={config}
               colorSets={colorSets}
               onDataPointHover={this.handleDataPointHover}
               onDataPointBlur={this.handleDataPointBlur}
        />
      </div>
    );
  }

  renderFakeData() {

    if(!this.props.fakeData || !this.props.fakeData.x || !this.props.fakeData.y
      || ( Array.isArray(this.props.fakeData.x) && !this.props.fakeData.x.length )
      || ( Array.isArray(this.props.fakeData.y) && !this.props.fakeData.y.length ))
      return (
        <div className="bar-chart-widget-no-data">No Data</div>
      );

    const data = [];

    let legendData = {
      x: this.props.fakeData.x,
      y: this.props.fakeData.y,
      ...this.legendConfig
    };

    data.push(legendData);

    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    return this.renderChartByParams(data, config, layout);
  }

  renderRealData() {

    const data = [];

    this.props.data.sources.forEach((source, sourceIndex) => {

      let legendData = {
        x: [],
        y: [],
        ...this.legendConfig
      };

      const storage = this.props.widgets.getIn([
        String(this.props.params.id),
        String(this.props.data.id),
        String(sourceIndex),
        'data',
      ]) || [];

      storage.forEach((item) => {
        legendData.x.push(item.get('value'));
        legendData.y.push(item.get('name'));
      });

      data.push(legendData);

    });

    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    return this.renderChartByParams(data, config, layout);
  }

  render() {

    if(!this.props.fetchRealData)
      return this.renderFakeData();

    return this.renderRealData();

  }

}

BarChartWidget.Settings = BarChartSettings;

export default BarChartWidget;
