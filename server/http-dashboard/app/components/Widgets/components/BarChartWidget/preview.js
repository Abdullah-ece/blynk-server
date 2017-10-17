import React from 'react';
import {message, Icon} from 'antd';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {WidgetDevicesPreviewListFetch} from 'data/Widgets/api';
import PropTypes from 'prop-types';
import {List} from 'immutable';

@connect((state) => ({
  devicesList: state.Widgets.getIn(['settingsModal', 'previewAvailableDevices', 'list']),
  devicesLoading: state.Widgets.getIn(['settingsModal', 'previewAvailableDevices', 'loading']),
}), (dispatch) => ({
  fetchDevicesList: bindActionCreators(WidgetDevicesPreviewListFetch, dispatch)
}))
class Preview extends React.Component {

  static propTypes = {

    params: PropTypes.shape({
      id: PropTypes.number.isRequired
    }).isRequired,

    devicesList: PropTypes.instanceOf(List),

    devicesLoading: PropTypes.bool,

    fetchDevicesList: PropTypes.func
  };

  componentWillMount() {

    // if no product id provided we cannot make preview based on productId

    if(this.props.params.id === 0)
      return null;

    if(this.props.devicesList === null && this.props.devicesLoading === false)
      this.props.fetchDevicesList({
        productId: this.props.params.id
      }).catch(() => {
        message.error('Unable to fetch devices list for Preview');
      });
  }

  render() {

    if(this.props.params.id === 0)
      return null;

    if(this.props.devicesLoading || this.props.devicesList === null)
      return (
        <div>
          <Icon type="loading" />
        </div>
      );

    return (
      <div>Preview of widget {this.props.devicesList.size} </div>
    );
  }

}

export default Preview;
