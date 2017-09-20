import React from 'react';
import {
  Plotly
} from 'components';
import moment from 'moment';
// import Widget from '../Widget';
import {VIRTUAL_PIN_PREFIX} from 'services/Widgets';
import PropTypes from 'prop-types';
import LinearWidgetSettings from './settings';
import './styles.less';
import {connect} from 'react-redux';
import {Map} from 'immutable';
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

  state = {
    data: []
  };

  componentWillMount() {

    if(!this.props.fetchRealData) {
      this.generateFakeData();
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
        }
      });

    });

    this.setState({
      data: data
    });
  }

  layout = {
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
      zerolinecolor: 'rgb(204,204,204)',
      showline: true,
      linecolor: 'rgb(204,204,204)',
      tickfont: {
        family: 'PF DinDisplay Pro',
        size: '10',
        color: '#9a9a9a'
      },
      tickformat: '%I:%M %p',
      hoverformat: '%d %b %Y %I:%M:%S %p',
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
    },
    showlegend: true,
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

    if (!this.props.widgets.hasIn([this.props.params.id, 'loading']) || this.props.widgets.getIn([this.props.params.id, 'loading']))
      return null;

    let minX = false;
    let maxX = false;

    let minY = false;
    let maxY = false;

    this.props.data.sources.forEach((source) => {

      if (!source.dataStream || source.dataStream.pin === -1)
        return null;

      const PIN = this.props.widgets.getIn([this.props.params.id, `${VIRTUAL_PIN_PREFIX}${source.dataStream.pin}`]);

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
      });

    });

    const layout = {
      ...this.layout,
      xaxis: {
        ...this.layout.xaxis,
        nticks: this.getNTicks(this.props.data.width),
        tickformat: (data[0] && data[0].x.length ? this.layout.xaxis.tickformat : null),
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

    return (
      <div className="grid-linear-widget">
        <Plotly data={data} config={this.config} layout={layout}/>
      </div>
    );
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

    return (
      <div className="grid-linear-widget">
        <Plotly data={this.state.data} config={this.config} layout={layout}/>
      </div>
    );
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
