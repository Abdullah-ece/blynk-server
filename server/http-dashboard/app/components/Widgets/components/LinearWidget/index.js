import React from 'react';
import Plotlyjs from 'plotly';
import {
  Plotly
} from 'components';
import moment from 'moment';
// import Widget from '../Widget';
import PropTypes from 'prop-types';
import LinearWidgetSettings from './settings';
import './styles.less';
import {connect} from 'react-redux';
import {Map, fromJS, List} from 'immutable';
import _ from 'lodash';

@connect((state) => ({
  widgets: state.Widgets && state.Widgets.get('widgetsData'),
}))
class LinearWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,
    params: PropTypes.object,

    editable: PropTypes.bool,

    fetchRealData: PropTypes.bool,

    onWidgetDelete: PropTypes.func,

    widgets: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);

    this.handleHover = this.handleHover.bind(this);
    this.handleUnHover = this.handleUnHover.bind(this);
    this.handleMouseMove = this.handleMouseMove.bind(this);
  }

  state = {
    data: []
  };

  componentWillMount() {

    if(!this.props.fetchRealData) {
      this.generateFakeData();
    }

  }

  componentDidMount() {
    const body = document.querySelector('body');

    if(this.hoverElement && !this.isHoverPlaceChanged) {
      body.parentNode.appendChild(this.hoverElement);
      this.isHoverPlaceChanged = true;
    }
  }

  componentDidUpdate(prevProps) {
    if(!this.props.fetchRealData && this.state.data.length !== this.props.data.sources.length) {
      this.generateFakeData();
    }

    if(!_.isEqual(prevProps.data.sources, this.props.data.sources)) {
      this.generateFakeData();
    }
  }

  componentWillUnmount() {
    if(this.hoverElement)
      this.hoverElement.parentNode.removeChild(this.hoverElement);
  }

  isHoverPlaceChanged = false;

  getTimeFormatForRange(dateFrom = 0, dateTo = 0) {

    dateFrom = moment(parseInt(dateFrom));
    dateTo = moment(parseInt(dateTo));

    if (dateTo.diff(dateFrom, 'hours') === 0) {
      return {
        tickFormat: '%I:%M %p',
        hoverFormat: '%a, %d %b, %I:%M:%S %p'
      };
    } else if (dateTo.diff(dateFrom, 'days') === 0) {
      return {
        tickFormat: '%I:%M %p',
        hoverFormat: '%a, %d %b, %I:%M %p'
      };
    } else if (dateTo.diff(dateFrom, 'days') >= 1 && dateTo.diff(dateFrom, 'days') <= 6) {
      return {
        tickFormat: '%a, %I:%M %p',
        hoverFormat: '%a, %d %b, %I:%M %p'
      };
    } else if (dateTo.diff(dateFrom, 'days') >= 7 && dateTo.diff(dateFrom, 'month') === 0) {
      return {
        tickFormat: '%d %b, %I:%M %p',
        hoverFormat: '%a, %d %b, %I:%M %p'
      };
    } else if (dateTo.diff(dateFrom, 'month') >= 1) {
      return {
        tickFormat: '%d %b, %I:%M %p',
        hoverFormat: '%a, %d %b, %I:%M %p, %Y'
      };
    }

  }

  getMinXFromLegendsList(data) {
    let min = new Date().getTime();

    if(data && data instanceof List && data.size) {
      data.forEach(item => {
        if(item.has('x') && item.get('x').size) {
          min = item.get('x').reduce((min, value) => {

            let valueTimestamp = min;

            if(value)
              valueTimestamp = moment(value).format('x');

            return valueTimestamp <= min ? valueTimestamp : min;
          }, min);
        }
      });
    }

    return min;
  }

  getMaxXFromLegendsList(data) {
    let max = 0;

    if(data && data instanceof List && data.size) {
      data.forEach(item => {
        if(item.has('x') && item.get('x').size) {
          max = item.get('x').reduce((max, value) => {

            let valueTimestamp = 0;

            if(value)
              valueTimestamp = moment(value).format('x');

            return valueTimestamp >= max ? valueTimestamp : max;
          }, max);
        }
      });
    }

    return max;
  }

  generateFakeData() {
    const data = [];

    this.props.data.sources.forEach((source) => {

      const y = [];

      for (let i = 0; i < 5; i++) {
        y.push(_.random(0, 10));
      }

      let item = _.find(this.state.data, (data) => data.name === source.label);

      const date = new Date().getTime();

      const format = (x) => moment(Number(x)).format('YYYY-MM-DD HH:mm:ss');

      data.push({
        ...this.dataChartConfig,
        name: source.label,
        x: [
          format(date-1000*60*40),
          format(date-1000*60*30),
          format(date-1000*60*20),
          format(date-1000*60*10),
          format(date),
        ],
        y: item ? item.y : y,
        marker: {
          color: source.color
        },
        hoveron: 'points',
        hoverinfo: 'x',
      });

    });

    this.setState({
      data: data
    });
  }

  layout = {
    hovermode: 'x',
    hoverlabel: {
      bgcolor: 'transparent',
      bordercolor: 'transparent',
      font: {
        color: 'transparent'
      },
      bgcolorsrc: 'transparent',
    },
    hoveron: 'tonext',
    autosize: true,
    margin: {
      t: 10,
      r: 30,
      l: 30,
      b: 5,
    },
    yaxis: {
      fixedrange: true,
      showline: true,
      linecolor: 'rgb(204,204,204)',
      ticklen: 3,
      tickcolor: '#fff',
      tickfont: {
        family: 'PF DinDisplay Pro',
        size: '12',
        color: '#9a9a9a'
      },
      zerolinecolor: 'rgb(204,204,204)',
    },
    dragmode: 'pan',
    xaxis: {
      hoverlabel: {
        bgcolor: 'transparent',
        bordercolor: 'transparent',
        font: {
          color: 'transparent'
        },
        bgcolorsrc: 'transparent',
      },
      zerolinecolor: 'rgb(204,204,204)',
      showline: true,
      linecolor: 'rgb(204,204,204)',
      tickfont: {
        family: 'PF DinDisplay Pro',
        size: '10',
        color: '#9a9a9a'
      },
      tickformat: '%I:%M %p',
      hoverformat: '%I:%M %p',
      ticklen: 3,
      tickangle: 0,
      nticks: 12,
      tickcolor: '#fff',
      // range selector
      rangeselector: {
        x: 0,
        y: 3
      },
      rangeslider: {
        range: []
      }
    },
    legend: {
      font: {
        family: 'PF DinDisplay Pro',
        size: '12',
        color: '#212227',
      },
    }
  };

  dataChartConfig = {
    line: {
      width: 1.5,
      shape: 'linear'
    },
    mode: 'lines',
  };

  config = {
    displayModeBar: false
  };

  renderRealDataChart() {
    if (!this.props.data.sources || !this.props.data.sources.length)
      return null;

    const data = [];

    if (!this.props.widgets.hasIn([String(this.props.params.id), 'loading']) || this.props.widgets.getIn([this.props.params.id, 'loading']))
      return null;

    if(this.hoverElement && !this.isHoverPlaceChanged) {
      const body = document.querySelector('body');
      body.parentNode.appendChild(this.hoverElement);
      this.isHoverPlaceChanged = true;
    }

    let minX = false;
    let maxX = false;

    let minY = false;
    let maxY = false;

    this.props.data.sources.forEach((source, sourceIndex) => {

      if (!source.dataStream || source.dataStream.pin === -1)
        return null;

      const PIN = this.props.widgets.getIn([
        String(this.props.params.id),
        String(this.props.data.id),
        String(sourceIndex),
      ]);

      if (!PIN)
        return null;

      let x = [];
      let y = [];

      if(source.dataStream.max)
        maxY = source.dataStream.max;

      if(source.dataStream.min)
        minY = source.dataStream.min;

      PIN.get('data').forEach((item) => {

        if (minX === false || parseInt(item.get('x')) < minX)
          minX = parseInt(item.get('x'));

        if (maxX === false || parseInt(item.get('x')) > maxX)
          maxX = parseInt(item.get('x'));

        if (maxY === false || parseInt(item.get('y')) > maxY)
          maxY = parseInt(item.get('y'));

        if (minY === false || parseInt(item.get('y')) < minY)
          minY = parseInt(item.get('y'));

        x.push(moment(Number(item.get('x'))).format('YYYY-MM-DD HH:mm:ss'));
        y.push(item.get('y'));
      });

      data.push({
        ...this.dataChartConfig,
        name: source.label,
        x: x,
        y: y,
        marker: {
          color: source.color
        },
        hoveron: 'points',
        hoverinfo: 'x',
      });

    });

    let formats = this.getTimeFormatForRange(
      this.getMinXFromLegendsList(fromJS(data)),
      this.getMaxXFromLegendsList(fromJS(data)),
    );

    const layout = {
      ...this.layout,
      xaxis: {
        ...this.layout.xaxis,
        nticks: this.getNTicks(this.props.data.width),
        tickformat: (data[0] && data[0].x.length ? formats.tickFormat : null),
        hoverformat: (data[0] && data[0].x.length ? formats.hoverFormat : null),
        rangeslider: {
          ...this.layout.rangeslider,
          range: minX === false || maxX === false ? [] : [minX, maxX]
        }
      },
      yaxis: {
        ...this.layout.yaxis,
        range: [minY, maxY]
      }
    };

    return this.renderChartByParams({
      data: data,
      config: this.config,
      layout: layout,
      handleHover: this.handleHover,
    });
  }

  getNTicks(width) {
    const defaultValue = 6;

    width = parseInt(width);

    if (width === 8)
      return 12;

    if (width === 7)
      return 9;

    if (width === 6)
      return 8;

    if (width === 5)
      return 4;

    if (width === 4)
      return 4;

    if (width === 3)
      return 3;

    if (width === 2)
      return 3;

    return defaultValue;
  }

  renderFakeDataChart() {

    const layout = {
      ...this.layout,
      xaxis: {
        ...this.layout.xaxis,
        nticks: this.getNTicks(this.props.data.width),
        rangeslider: undefined,
        rangeselector: undefined,
      },
      dragmode: "zoom",
      margin: {
        ...this.layout.margin,
        b: 30,
      }
    };

    return this.renderChartByParams({
      data: this.state.data,
      config: this.config,
      layout: layout,
      handleHover: this.handleHover,
    });
  }

  renderChartByParams(params) {
    return (
      <div onMouseLeave={this.handleUnHover} onMouseMove={this.handleMouseMove} className="grid-linear-widget"
           ref={(element) => (this.chartElement = element)}>

        <Plotly data={params.data || []}
                config={params.config || {}}
                layout={params.layout || {}}
                handleHover={this.handleHover}
        />

        <div className="grid-linear-widget-chart-hover-container"
             ref={(element) => (this.hoverElement = element)}/>

        <div className={`grid-linear-widget-chart-hover-line ${this.props.fetchRealData ? 'real-data-chart' : null}`}
             ref={(element) => (this.hoverLine = element)}/>

      </div>
    );
  }

  handleMouseMove(event) {
    const x = event.clientX;

    const chartBoundingClient = this.chartElement.getBoundingClientRect();
    const hoverBoundingClient = this.hoverElement.getBoundingClientRect();

    if(x - chartBoundingClient.left <= 29) {
      this.handleUnHover();
    }

    this.hoverElement.style.top = `${chartBoundingClient.top - hoverBoundingClient.height}px`;
    this.hoverElement.style.left = `${x - (hoverBoundingClient.width / 2)}px`;
    this.hoverLine.style.top = `0px`;
    this.hoverLine.style.left = `${x - chartBoundingClient.left + 15}px`;
  }

  handleHover(data) {

    const getXValue = (data) => {
      if (data.points.length)
        return data.points[0].x;
    };

    const getYValues = (data) => {
      const points = [];
      if (data.points.length)
        data.points.forEach((point) => {
          points.push({
            name: point.data.name,
            color: point.data.marker.color,
            value: point.y,
          });
        });

      return points;
    };

    const getHoverFormat = (data) => {

      if (data.points.length)
        return data.points[0].xaxis.hoverformat;
    };

    const formatXValue = (value, format) => {
      return Plotlyjs.d3.time.format(format)(new Date(value));
    };

    this.hoverElement.innerHTML = `\
      <div class="chart-tooltip-name">${formatXValue(getXValue(data), getHoverFormat(data))}</div> \
    `;

    getYValues(data).forEach((point) => {
      this.hoverElement.innerHTML = this.hoverElement.innerHTML + `\
      <div class="chart-tooltip-legend">
        <div class="chart-tooltip-legend-dot" style="background: #${point.color}"></div>
        <div class="chart-tooltip-legend-name">${point.name}:</div>
        <div class="chart-tooltip-legend-value">${point.value}</div>
      </div>
      `;
    });

    const boundingClient = this.chartElement.getBoundingClientRect();
    const hoverElementBoundingClient = this.hoverElement.getBoundingClientRect();

    this.hoverLine.style.display = 'block';
    this.hoverElement.style.display = 'block';

    let x = data.event.clientX;

    this.hoverElement.style.top = `${boundingClient.top - hoverElementBoundingClient.height}px`;
    this.hoverElement.style.left = `${x}px`;

  }

  handleUnHover() {
    this.hoverElement.style.display = 'none';
    this.hoverLine.style.display = 'none';
  }

  render() {

    if (!this.props.fetchRealData)
      return this.renderFakeDataChart();

    return this.renderRealDataChart();
  }

}

LinearWidget.Settings = LinearWidgetSettings;

export default LinearWidget;


/*
* 1) Get data for own PINS
* 2) Draw data for these own PINS
* 3) Display labels for these PINS
* 4) Fix DataStreams
* 5) Multiple sources support */
