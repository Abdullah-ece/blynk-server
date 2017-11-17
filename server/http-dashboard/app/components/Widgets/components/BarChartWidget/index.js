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

  defaultParams = {
    chartConfigs: {
      toolTip:{
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


    let config = {
      ...this.defaultParams.chartConfigs
    };

    const length = this.props.fakeData.x.length;
    let dataSource = [];
    for(let i = 0; i < length; i++) {
      dataSource.push({
        label:this.props.fakeData.y[i],
        y: this.props.fakeData.x[i],
      });
    }
    config.data[0].dataPoints = dataSource;

    return this.renderChartByParams(config);
  }

  renderRealData() {

    let config = {
      ...this.defaultParams.chartConfigs
    };

    let dataSource = [];

    this.props.data.sources.forEach((source, sourceIndex) => {
      const storage = this.props.widgets.getIn([
        String(this.props.params.id),
        String(this.props.data.id),
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

    config.data[0].dataPoints = dataSource;

    return this.renderChartByParams(config);
  }

  render() {

    if(!this.props.fetchRealData)
      return this.renderFakeData();

    return this.renderRealData();
  }

}

BarChartWidget.Settings = BarChartSettings;

export default BarChartWidget;
