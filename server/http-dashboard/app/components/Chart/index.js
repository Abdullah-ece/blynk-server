import React from 'react';
import Canvasjs from 'canvasjs';
import PropTypes from 'prop-types';
import _ from 'lodash';
import './styles.less';

class Chart extends React.Component {

  static propTypes = {
    config: PropTypes.shape({
      data: PropTypes.array,
      axisY: PropTypes.object,
      axisX: PropTypes.object,
      legend: PropTypes.object,
    }),
    colorSets: PropTypes.array,
    className: PropTypes.string,
    name:PropTypes.string,
  };

  componentDidMount() {
    const chartConfig = _.merge(this.chartDefaultOptions, this.props.config || {});

    if(Array.isArray(this.props.colorSets))
      this.props.colorSets.map((colorSet) => {
        Canvasjs.addColorSet(colorSet.name, colorSet.colors);
      });

    this.chart = new Canvasjs.Chart(this.chartRef, chartConfig);

    this.chart.render();
  }

  componentDidUpdate(prevProps) {

    const config = _.cloneDeep(this.props.config);
    const dataPoints = _.cloneDeep(config.data[0].dataPoints);
    delete config.data[0].dataPoints;

    const prevConfig = _.cloneDeep(prevProps.config);
    const prevDataPoints = _.cloneDeep(prevConfig.data[0].dataPoints);
    delete prevConfig.data[0].dataPoints;

    if(!_.isEqual(config, prevConfig)) {

      this.chart.options = _.merge({},
        this.chart.options,
        this.props.config,
      );

      this.chart.render();

    } else {
      if(!_.isEqual(dataPoints, prevDataPoints)) {

        const points = dataPoints.slice(prevDataPoints.length);

        points.forEach((point) => {
          this.chart.data[0].dataPoints.push(_.clone(point));
        });

        this.chart.render();
      }
    }
  }

  componentWillUnmount(){
    this.destroyChart();
  }

  destroyChart(){
    // destroy instance of chart to prevent memory leaks
    if (this.chart){
      this.chart.destroy();
    }
  }

  legendDefaultOptions = {
    fontFamily: 'PF DinDisplay Pro',
    fontWeight: 300,
    fontColor: 'rgba(33, 34, 39, 0.75)',
    fontSize: 12,

    verticalAlign: 'top',
    horizontalAlign: 'left',
    markerMargin: 8,
  };

  axisYDefaultOptions = {
    gridColor: 'rgba(0,0,0,0)',
    gridDashType: 'none',
    labelFontFamily: 'PF DinDisplay Pro',
    labelFontWeight: 300,
    labelFontColor: 'rgba(33, 34, 39, 0.75)',
    labelFontSize: 12,
    includeZero: false,
    tickLength: 2,
    tickColor: '#fff',
    margin: 0,
    lineColor: 'rgba(33, 34, 39, 0.25)',
    lineThickness: 1,
  };

  axisXDefaultOptions = {
    gridColor: 'rgba(33, 34, 39, 0.05)',
    gridThickness: 1,
    labelFontSize: 10,
    labelFontFamily: 'PF DinDisplay Pro',
    labelFontWeight: 300,
    labelFontColor: 'rgba(33, 34, 39, 0.75)',
    tickLength: 12,
    tickColor: '#fff',
    margin: 12,
    lineColor: 'rgba(33, 34, 39, 0.25)'
  };

  chartDefaultOptions = {
    animationEnabled: false,
    axisY:{
      includeZero: false,
      ...this.axisYDefaultOptions
    },
    axisX: {
      ...this.axisXDefaultOptions,
    },
    data: [],
    legend: {
      ...this.legendDefaultOptions,
    }
  };

  render() {
    return (
      <div className={this.props.className}>
        <div className="canvasjs-widget-container" ref={(ref) => this.chartRef = ref}/>
      </div>
    );
  }

}

export default Chart;
