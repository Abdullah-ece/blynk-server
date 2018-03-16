import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {blynkWsHardware} from 'store/blynk-websocket-middleware/actions';
import {bindActionCreators} from 'redux';

@connect((state, ownProps) => {

  if(ownProps.fetchData === false)
    return {
      value  : null,
      loading: false
    };

  const pin = state.Devices.deviceDashboardLiveData[ownProps.pin];

  if (ownProps.isLive)
    return {
      value: pin === true ? null : pin
    };

  if (!ownProps.isLive)
    return {
      value: state.Devices.deviceDashboardData[ownProps.widgetId].value
    };

}, (dispatch) => ({
  blynkWsHardware: bindActionCreators(blynkWsHardware, dispatch),
}))
class SwitchWidgetDataWrapper extends React.Component {

  static propTypes = {
    children: PropTypes.element,

    value: PropTypes.string,

    deviceId: PropTypes.number,

    blynkWsHardware: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleWriteToVirtualPin = this.handleWriteToVirtualPin.bind(this);
  }

  handleWriteToVirtualPin({pin, value}) {

    this.props.blynkWsHardware({
      deviceId: this.props.deviceId,
      pin: pin,
      value: value
    });
  }

  render() {

    const {value} = this.props;

    return (
      React.cloneElement(this.props.children, {value, onWriteToVirtualPin: this.handleWriteToVirtualPin})
    );
  }

}

export default SwitchWidgetDataWrapper;
