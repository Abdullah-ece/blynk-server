import React from 'react';

import {Chart} from 'components';

import {Map} from 'immutable';

import {Icon} from 'antd';

import PropTypes from 'prop-types';

import BarChartSettings from './settings';

import Dotdotdot from 'react-dotdotdot';

import './styles.less';

class BarChartWidget extends React.Component {

  static propTypes = {
    loading: PropTypes.oneOfType([
      PropTypes.bool,
      PropTypes.object,
    ]),

    history: PropTypes.instanceOf(Map),

    deviceId: PropTypes.any,

    name: PropTypes.string,

    data: PropTypes.shape({
      label: PropTypes.string,
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      w: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      h: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      sources: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      }))
    }),

    parentElementProps: PropTypes.shape({
      id         : PropTypes.string,
      onMouseUp  : PropTypes.func,
      onTouchEnd : PropTypes.func,
      onMouseDown: PropTypes.func,
      style      : PropTypes.object,
    }),

    tools        : PropTypes.element,
    settingsModal: PropTypes.element,
    resizeHandler: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.element),
      PropTypes.element,
    ]),

  };

  defaultParams = {
    chartConfigs: {
      toolTip:{
        enabled: true,
        fontColor: "#0f0f13",
        fontSize: 12,
        fontFamily: 'PF DinDisplay Pro',
        fontWeight: 300,
        content: "{label}: {y}",
        cornerRadius: 6,
        borderThickness: 0,
        borderColor:"white",
        backgroundColor:" white"
      },
      colorSet: "default",
      axisY2:{
        labelFontSize: "12",
        labelAngle: 0,
        gridColor: "rgba(33, 34, 39, 0.05)",
        gridThickness: 1,
        lineColor: "white",
        tickColor: "white",
        labelFontColor: "#646368"
      },
      axisX: {
        interval: 1,
        labelAngle: 0,
        gridThickness: 0,
        labelFontSize: "12",
        labelFontColor: "#3A3A3F",
        labelFontWeight: 300,
        labelFontFamily: 'PF DinDisplay Pro',
        lineColor: "#F5F5F5",
        tickColor: "white",
        labelMaxWidth: 1000,
        labelWrap: true,
      },
      data: [
        {
          axisYType: "secondary",
          type: "bar",
        }
      ]
    },
    colors: {
      hoverColor: '#B5F3E0',
      unhoveredColor: '#29DEAF',
    },
    colorSets: [
        {
          name:'default',
          colors: ['#29DEAF']
        },
        {
          name: 'hover',
          colors: ['#B5F3E0']
        }
    ],
  };

  renderChartByParams(config = {}) {
    return (
      <div className="widgets--widget-container">
        <Chart config={config}
               colorSets={this.defaultParams.colorSets}
               className = "bar-chart"
               name={this.props.name}
        />
      </div>
    );
  }

  renderRealData() {

    if (!this.props.data.sources || !this.props.data.sources.length || !this.props.history || this.props.loading === undefined)
      return (<div className="bar-chart-widget-no-data">No Data</div>);

    if (this.props.loading)
      return (<Icon type="loading"/>);

    let config = {
      ...this.defaultParams.chartConfigs
    };

    let dataSource = [];

    this.props.data.sources.forEach((source, sourceIndex) => {
      const storage = this.props.history.getIn([
        String(sourceIndex),
        'data',
      ]) || [];

      storage.forEach((item) => {
        dataSource.push({
          label: item.get('name'),
          y: item.get('value')
        });
      });
    });

    if (!dataSource.length)
      return (<div className="bar-chart-widget-no-data">No data</div>);

    config.data[0].dataPoints = dataSource;

    return this.renderChartByParams(config);
  }

  render() {

    return (
      <div {...this.props.parentElementProps} className={`widgets--widget`}>
        <div className="widgets--widget-label">
          <Dotdotdot clamp={1}>{this.props.data.label || 'No Widget Name'}</Dotdotdot>
          {this.props.tools}
        </div>

        { /* widget content */ }

        { this.renderRealData() }

        { /* end widget content */ }

        {this.props.settingsModal}
        {this.props.resizeHandler}
      </div>
    );
  }

}

BarChartWidget.Settings = BarChartSettings;

export default BarChartWidget;
