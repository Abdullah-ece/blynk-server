import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

@connect((state, ownProps) => {

  if (ownProps.isLive) {
    const pin = state.Devices.deviceDashboardChartLiveData[ownProps.pin];

    if (!pin)
      return {
        value  : [],
        loading: true
      };

    return {
      value  : pin.data,
      loading: pin.loading
    };
  }


  if (!ownProps.isLive) {
    const widget = state.Devices.deviceDashboardChartData[ownProps.widgetId];

    if(!widget)
      return {
        value  : [],
        loading: true
      };

    return {
      value: widget.data,
      loading: widget.loading
    };
  }

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
