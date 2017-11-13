import React from 'react';
import {Chart} from 'components';
// import Widget from '../Widget';
import {
  Icon
} from 'antd';
import PropTypes from 'prop-types';
import LinearWidgetSettings from './settings';
import './styles.less';
import {connect} from 'react-redux';
import {Map, fromJS, List} from 'immutable';
import moment from 'moment';
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

    this.generateData = this.generateData.bind(this);
    this.generateFakeData = this.generateFakeData.bind(this);
  }

  dataDefaultOptions = {
    type: 'line',
    markerType: 'none',
    lineThickness: 1,
  };

  getMinMaxXFromLegendsList(data) {
    let min = new Date().getTime();
    let max = 0;

    if(data && data instanceof List && data.size) {

      data.forEach(item => {

        if (item.has('dataPoints') && item.get('dataPoints').size) {
          [min, max] = item.get('dataPoints').reduce(([min, max], dataPoint) => {

            let value = dataPoint.get('x');

            if (value) {
              let valueTimestamp = moment(value).format('x');

              return [
                valueTimestamp <= min ? valueTimestamp : min,
                valueTimestamp >= max ? valueTimestamp : max];

            } else {
              return [min, max];
            }
          }, [min, max]);

        }
      });
    }

    return [min,max];
  }

  getTimeFormatForRange([dateFrom = 0, dateTo = 0]) {

    dateFrom = moment(parseInt(dateFrom));
    dateTo = moment(parseInt(dateTo));

    if (dateTo.diff(dateFrom, 'hours') === 0) {
      return {
        tickFormat: 'hh:mm TT',
        hoverFormat: 'DDD, DD MMM, hh:mm:ss TT',
        labelMaxWidth: 50,
      };
    } else if (dateTo.diff(dateFrom, 'days') === 0) {
      return {
        tickFormat: 'hh:mm TT',
        hoverFormat: 'DDD, DD MMM, hh:mm TT',
        labelMaxWidth: 50,
      };
    } else if (dateTo.diff(dateFrom, 'days') >= 1 && dateTo.diff(dateFrom, 'days') <= 6) {
      return {
        tickFormat: 'DDD, hh:mm TT',
        hoverFormat: 'DDD, DD MMM, hh:mm TT',
        labelMaxWidth: 60,
      };
    } else if (dateTo.diff(dateFrom, 'days') >= 7 && dateTo.diff(dateFrom, 'month') === 0) {
      return {
        tickFormat: 'DD MMM, hh:mm TT',
        hoverFormat: 'DDD, DD MMM, hh:mm TT',
        labelMaxWidth: 100,
      };
    } else if (dateTo.diff(dateFrom, 'month') >= 1) {
      return {
        tickFormat: 'DD MMM, hh:mm TT',
        hoverFormat: 'DDD, DD MMM, hh:mm TT, YYYY',
        labelMaxWidth: 100,
      };
    }

    return {
      tickFormat: null,
      hoverFormat: null
    };

  }

  generateFakeData(source) {
    let dataSource = fromJS({
      ...this.dataDefaultOptions,
      name: source.get('label') || null,
      dataPoints: [],
      xValueFormatString: 'DD-MMMs',
      yValueFormatString: '#,###,##',
    });

    for(let i = 0; i < 7; i++) {
      dataSource = dataSource.update('dataPoints', (dataPoints) => dataPoints.push(
        { x: moment().subtract(i, 'days').toDate(), y: _.random(0,500)}
      ));
    }

    return dataSource.toJS();
  }

  renderFakeDataChart() {

    const data = fromJS(this.props.data);

    if(!data.has('sources') || !data.get('sources').size)
      return null;

    const dataSources = data.get('sources').map(this.generateFakeData).toJS();

    const config = {
      axisX: {
        valueFormatString: "DD-MMM"
      },
      data: dataSources
    };

    return (
      <div className="widgets--widget-container">
        <Chart config={config}/>
      </div>
    );

  }

  generateData(source, sourceIndex) {

    if(!source.has('dataStream') || !source.hasIn(['dataStream', 'pin']))
      return null;

    const pin = this.props.widgets.getIn([
      String(this.props.params.id),
      String(this.props.data.id),
      String(sourceIndex)
    ]);

    if(!pin)
      return null;

    const dataPoints = pin.get('data').map((item) => {

      return fromJS({
        x: moment(Number(item.get('x'))).toDate(),
        y: item.get('y')
      });
    });

    let dataSource = fromJS({
      ...this.dataDefaultOptions,
      name: source.get('label') || null,
      dataPoints: dataPoints || [],
    });

    return dataSource;
  }

  renderRealDataChart() {

    if (!this.props.data.sources || !this.props.data.sources.length)
      return (<div>No data</div>);

    if (!this.props.widgets.hasIn([String(this.props.params.id), 'loading']) || this.props.widgets.getIn([this.props.params.id, 'loading']))
      return (<Icon type="loading" />);

    const sources = fromJS(this.props.data.sources);

    const dataSources = sources.map(this.generateData).filter((source) => source !== null);

    let formats = this.getTimeFormatForRange(
      this.getMinMaxXFromLegendsList(dataSources)
    );

    dataSources.map(dataSource =>
      dataSource.set('xValueFormatString', formats.hoverFormat).set('yValueFormatString', '###,###,###,###')
    );

    const config = {
      axisX: {
        labelMaxWidth: formats.labelMaxWidth,
        labelWrap: true,
        labelAngle: 0,
        valueFormatString: formats.tickFormat,
      },
      data: dataSources.toJS()
    };

    return (
      <div className="widgets--widget-container">
        <Chart config={config}/>
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
