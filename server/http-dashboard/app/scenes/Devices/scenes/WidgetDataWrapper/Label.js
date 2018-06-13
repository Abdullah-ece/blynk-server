import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

@connect((state, ownProps) => {

  if(ownProps.fetchData === false || !ownProps.pins.length || isNaN(Number(ownProps.pins[0])))
    return {
      value  : null,
      loading: false
    };

  const pin = state.Devices.deviceDashboardLiveData[ownProps.pins[0]];

  // if (ownProps.isLive)
    return {
      value: pin === true ? null : pin
    };

  // if (!ownProps.isLive)
  //   return {
  //     value: state.Devices.deviceDashboardData[ownProps.widgetId].value
  //   };

})
class LabelWidgetDataWrapper extends React.Component {

  static propTypes = {
    children: PropTypes.element,

    value: PropTypes.string,
  };

  render() {

    const {value} = this.props;

    return (
      React.cloneElement(this.props.children, {value})
    );
  }

}

export default LabelWidgetDataWrapper;
