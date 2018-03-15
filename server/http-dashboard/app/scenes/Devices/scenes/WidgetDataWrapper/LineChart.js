import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

@connect((state, ownProps) => {

  const pin = state.Devices.deviceDashboardChartLiveData[ownProps.pin];

  if(!pin)
    return {
      value: null
    };

  if (ownProps.isLive)
    return {
      value: pin.data,
      loading: pin.loading
    };

  if (!ownProps.isLive)
    return {
      value: state.Devices.deviceDashboardData[ownProps.widgetId].value
    };

})
class LineChartWidgetDataWrapper extends React.Component {

  static propTypes = {
    children: PropTypes.element,

    value: PropTypes.array,

    loading: PropTypes.bool,
  };

  render() {

    const {value, loading} = this.props;

    return (
      React.cloneElement(this.props.children, {value: value, loading: loading})
    );
  }

}

export default LineChartWidgetDataWrapper;
