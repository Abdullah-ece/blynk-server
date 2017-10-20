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
import BarChartWidget from './index';
import {reduxForm} from 'redux-form';
import Dotdotdot from 'react-dotdotdot';
import './components/Preview/styles.less';

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

    let data = {
      id: 1,
      w: 3,
      h: 2,
      title: 'Bar Chart'
    };

    let fakeData = {
      // y: ['Shift 1', 'Shift 2', 'Shift 3'],
      // x: [1, 2, 3]
    };

    return (
      <div>
        <span>Preview of widget {this.props.devicesList.size} </span>

        <div>
          <Select name="deviceId"
                  values={devicesOptions}
                  placeholder="Please select device" />
        </div>

        <div className="widgets--widget bar-widget-preview">
          <div className="widgets--widget-label">
            <Dotdotdot clamp={1}>{data.title || 'No Bar Title'}</Dotdotdot>
          </div>
          <BarChartWidget fetchRealData={false}
                          params={this.props.params}
                          data={data}
                          fakeData={fakeData}/>
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
