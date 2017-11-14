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

    this.handleDataPointBlur = this.handleDataPointBlur.bind(this);
    this.handleDataPointHover = this.handleDataPointHover.bind(this);
  }

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

  renderChartByParams(data = []) {

    let config = {
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

    return this.renderChartByParams(data);
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

    return this.renderChartByParams(data);
  }

  render() {

    if(!this.props.fetchRealData)
      return this.renderFakeData();

    return this.renderRealData();

  }

}

BarChartWidget.Settings = BarChartSettings;

export default BarChartWidget;
