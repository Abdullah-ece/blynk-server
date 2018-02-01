import React from 'react';

import {Chart} from 'components';

import {Map} from 'immutable';

import PropTypes from 'prop-types';

import {connect} from 'react-redux';

import BarChartSettings from './settings';

import _ from 'lodash';

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

    deviceId: PropTypes.number,

    editable: PropTypes.bool,
    previewMode: PropTypes.bool,
    name: PropTypes.string,

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
    }),
    isChartPreview: PropTypes.bool,
  };

  defaultParams = {
    chartConfigs: {
      toolTip:{
        enabled: !(this.props.editable && !this.props.previewMode),
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

  generateFakeData(){
    let labels = ['Column 1','Column 2','Column 3','Column 4','Column 5'];
    const length = _.random(2,5);
    let dataSource = [];
    for(let i = 0; i < length; i++) {
      dataSource.push({
        label: labels.splice(0, 1).toString(),
        y: _.random(1000),
      });
    }
    return dataSource;
  }
  renderFakeData() {
    let config = {
      ...this.defaultParams.chartConfigs
    };

    let data = this.props.fakeData;
    if(!data || !data.x || !data.y
      || ( Array.isArray(data.x) && !data.x.length )
      || ( Array.isArray(data.y) && !data.y.length )){


      if(this.props.isChartPreview){
        return (
          <div className="bar-chart-widget-no-data">No Data</div>
        );
      }else {
        config.data[0].dataPoints = this.generateFakeData();
        return this.renderChartByParams(config);
      }
    }

    const length = data.x.length;
    let dataSource = [];
    for(let i = 0; i < length; i++) {
      dataSource.push({
        label:data.y[i],
        y: data.x[i],
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
        String(this.props.deviceId),
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

    if (!dataSource.length)
      return (<div className="bar-chart-widget-no-data">No data</div>);

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
