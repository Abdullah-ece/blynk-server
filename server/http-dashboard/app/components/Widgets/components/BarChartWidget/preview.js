import React from 'react';
import {
  message,
  Icon,
  Button
} from 'antd';
import {MetadataSelect as Select} from 'components/Form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {WidgetDevicesPreviewListFetch} from 'data/Widgets/api';
import PropTypes from 'prop-types';
import {List} from 'immutable';
import {reduxForm} from 'redux-form';

@connect((state) => ({
  devicesList: state.Widgets.getIn(['settingsModal', 'previewAvailableDevices', 'list']),
  devicesLoading: state.Widgets.getIn(['settingsModal', 'previewAvailableDevices', 'loading']),
}), (dispatch) => ({
  fetchDevicesList: bindActionCreators(WidgetDevicesPreviewListFetch, dispatch)
}))
@reduxForm({
  form: 'bar-chart-widget-preview'
})
class Preview extends React.Component {

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

    this.updateChart = this.updateChart.bind(this);
  }

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

  updateChart() {
    alert('Updating...');
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

    let devicesOptions = [];

    this.props.devicesList.forEach((device) => {
      devicesOptions.push({
        key: String(device.get('id')),
        value: `${device.get('productName')} - ${String(device.get('name'))} - ${device.get('token')}`
      });
    });

    return (
      <div>
        <span>Preview of widget {this.props.devicesList.size} </span>

        <div>
          <Select name="deviceId"
                  values={devicesOptions}
                  placeholder="Please select device" />
        </div>

        <div>
          <Button onClick={this.updateChart}>
            Update Chart
          </Button>
        </div>

      </div>
    );
  }

}

export default Preview;
