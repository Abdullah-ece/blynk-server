import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import PropTypes from 'prop-types';
import {List} from 'immutable';
import {Preview} from '../../components';
import {WidgetDevicesPreviewListFetch} from 'data/Widgets/api';
import {
  message
} from 'antd';

@connect((state) => ({
  devicesList: state.Widgets.getIn(['settingsModal', 'previewAvailableDevices', 'list']),
  devicesLoading: state.Widgets.getIn(['settingsModal', 'previewAvailableDevices', 'loading']),
}), (dispatch) => ({
  fetchDevicesList: bindActionCreators(WidgetDevicesPreviewListFetch, dispatch)
}))
export default class PreviewScene extends React.Component {

  static propTypes = {

    params: PropTypes.shape({
      id: PropTypes.number.isRequired
    }).isRequired,

    devicesList: PropTypes.instanceOf(List),

    devicesLoading: PropTypes.bool,

    fetchDevicesList: PropTypes.func
  };

  constructor(props) {
    super(props);

    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentWillMount() {

    // if no product id provided we cannot make preview based on productId

    if (this.props.params.id === 0)
      return null;

    if (this.props.devicesList === null && this.props.devicesLoading === false)
      this.props.fetchDevicesList({
        productId: this.props.params.id
      }).catch(() => {
        message.error('Unable to fetch devices list for Preview');
      });
  }

  handleSubmit(/*values*/) {
    // console.log('submitted', values);
  }

  render() {

    const widgetData = {
      id: 1,
      w: 3,
      h: 2,
    };

    const chartData = {
      x: [],
      y: [],
    };

    return (
      <Preview onSubmit={this.handleSubmit}
               params={this.props.params}
               form={'bar-chart-widget-preview'}
               chartData={chartData}
               widgetData={widgetData}
               devicesList={this.props.devicesList}
               devicesLoading={this.props.devicesLoading}/>
    );
  }

}
