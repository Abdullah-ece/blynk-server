import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

@connect((state, ownProps) => {

  if(ownProps.fetchData === false)
    return {
      value  : [],
      loading: false
    };

  if (ownProps.isLive) {

    let values = [];

    const pinsData = ownProps.pins.map((pin) => {
      return state.Devices.deviceDashboardChartLiveData[pin];
    });

    pinsData.forEach((pinValue) => {
      if(!pinValue) {
        values.push({
          value: [],
          loading: true,
        });
      } else {
        values.push({
          value: pinValue.data,
          loading: pinValue.loading
        });
      }

    });

    return {
      values : values,
      loading: values.some((value) => value.loading)
    };
  }


  if (!ownProps.isLive) {

    let values = [];

    const pinsData = ownProps.pins.map((pin) => {
      return state.Devices.deviceDashboardChartData[ownProps.widgetId] && state.Devices.deviceDashboardChartData[ownProps.widgetId][pin] || '';
    });

    pinsData.forEach((pinValue) => {
      if(!pinValue) {
        values.push({
          value: [],
          loading: true,
        });
      } else {
        values.push({
          value: pinValue.data,
          loading: pinValue.loading
        });
      }

    });

    return {
      values: values,
      loading: values.some((value) => value.loading)
    };
  }

})
class LineChartWidgetDataWrapper extends React.Component {

  static propTypes = {
    children: PropTypes.element,

    values: PropTypes.array,

    loading: PropTypes.bool,
  };

  render() {

    const {values, loading} = this.props;

    return (
      React.cloneElement(this.props.children, {values: values, loading: loading})
    );
  }

}

export default LineChartWidgetDataWrapper;
