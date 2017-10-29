import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import PropTypes from 'prop-types';
import {
  List,
  Map,
  fromJS,
} from 'immutable';
import {Preview} from '../../components';
import {
  WidgetDevicesPreviewListFetch,
  WidgetDevicesPreviewHistoryFetch
} from 'data/Widgets/api';
import {
  WidgetDevicesPreviewListClear
} from 'data/Widgets/actions';

@connect((state, ownProps) => ({
  orgId: parseInt(state.Account.orgId || 0),
  devicesList: state.Widgets.getIn(['settingsModal', 'previewAvailableDevices', 'list']),
  devicesLoading: state.Widgets.getIn(['settingsModal', 'previewAvailableDevices', 'loading']),
  devicePreviewData: state.Widgets.getIn(['settingsModal', 'previewData', ownProps.widgetId, 'data']) || fromJS([]),
  source: fromJS(ownProps.source || {}),
}), (dispatch) => ({
  fetchDevicesList: bindActionCreators(WidgetDevicesPreviewListFetch, dispatch),
  fetchDevicePreviewHistory: bindActionCreators(WidgetDevicesPreviewHistoryFetch, dispatch),
  clearWidgetDevicesPreviewList: bindActionCreators(WidgetDevicesPreviewListClear, dispatch),
}))
export default class PreviewScene extends React.Component {

  static propTypes = {

    params: PropTypes.shape({
      id: PropTypes.number.isRequired
    }).isRequired,

    devicesList: PropTypes.instanceOf(List),
    devicePreviewData: PropTypes.instanceOf(List),

    orgId: PropTypes.number,
    widgetId: PropTypes.number,

    devicesLoading: PropTypes.bool,

    fetchDevicesList: PropTypes.func,
    fetchDevicePreviewHistory: PropTypes.func,
    clearWidgetDevicesPreviewList: PropTypes.func,

    source: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);

    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentWillMount() {

    // if no product id provided we cannot make preview based on productId

    if (this.props.params.id === 0)
      return null;

    this.props.fetchDevicesList({
      productId: this.props.params.id,
      orgId: this.props.orgId
    });
  }

  componentWillUnmount() {
    this.props.clearWidgetDevicesPreviewList();
  }

  handleSubmit(values) {

    const additionalFields = {};

    ['selectedColumns', 'groupByFields', 'sortByFields'].forEach((item) => {
      if(this.props.source.get(item) && this.props.source.get(item).size)
        additionalFields[item] = this.props.source.get(item).toJS();
    });

    return this.props.fetchDevicePreviewHistory({
      widgetId: parseInt(this.props.widgetId),
      deviceId: parseInt(values.deviceId),
    },{
      dataQueryRequests: [
        {
          sourceType: this.props.source.get('sourceType') || null,
          pinType: this.props.source.getIn(['dataStream','pinType']) || null,
          pin: this.props.source.getIn(['dataStream', 'pin']),
          offset: 0,
          from: 0,
          limit: parseInt(this.props.source.get('limit')) || 1000,
          to: new Date().getTime(),
          deviceId: values.deviceId,
          ...additionalFields,
        }
      ]
    });
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

    if(this.props.devicePreviewData)
      this.props.devicePreviewData.forEach((item) => {
        chartData.x.push(item.get('value'));
        chartData.y.push(item.get('name'));
      });

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
