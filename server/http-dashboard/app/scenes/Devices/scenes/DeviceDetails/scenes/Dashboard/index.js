import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Icon } from 'antd';
import { bindActionCreators } from 'redux';
import { WIDGET_TYPES } from 'services/Widgets';
import { Grids } from "components";
import {
  DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME,
  TIMELINE_TIME_FILTERS
} from 'services/Devices';
import { getFormValues } from 'redux-form';
import {
  DeviceDashboardFetch
} from 'data/Devices/api';
import { WidgetStatic } from "components/Widgets";
import { blynkWsChartDataFetch } from 'store/blynk-websocket-middleware/actions';

@connect((state) => ({
  orgId: state.Account.selectedOrgId,
  dashboard: state.Devices.deviceDashboard,
  loading: state.Devices.deviceDashboardLoading,
  timeFilter: getFormValues(DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME)(state)
}), (dispatch) => ({
  fetchDeviceDashboard: bindActionCreators(DeviceDashboardFetch, dispatch),
  fetchDeviceDashboardData: bindActionCreators(blynkWsChartDataFetch, dispatch)
}))
class DashboardScene extends React.Component {

  static propTypes = {
    dashboard: PropTypes.shape({
      widgets: PropTypes.array,
    }),
    params: PropTypes.object,
    timeFilter: PropTypes.object,

    orgId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

    loading: PropTypes.bool,

    fetchDeviceDashboard: PropTypes.func,
    fetchDeviceDashboardData: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.fetchDashboardData = this.fetchDashboardData.bind(this);
  }

  componentDidMount() {
    // loading dash widgets
    if (this.props.orgId) {
      this.fetchDashboard().then(this.fetchDashboardData).catch(
        err => console.error(err));
    }
  }

  componentDidUpdate(prevProps) {
    // check props to make new fetch
    if (prevProps.params.id !== this.props.params.id && this.props.orgId)
      this.fetchDashboard().then(this.fetchDashboardData);

    if (!prevProps.orgId && this.props.orgId) {
      this.fetchDashboard().then(this.fetchDashboardData);
    }

    if (prevProps.timeFilter.time !== this.props.timeFilter.time)
      this.fetchDashboardData();

    if (prevProps.timeFilter.customTime[0] !== this.props.timeFilter.customTime[0] || prevProps.timeFilter.customTime[1] !== this.props.timeFilter.customTime[1])
      this.fetchDashboardData();

  }

  fetchDashboardData() {

    const data = this.getTimeFilerRange();

    this.props.dashboard.widgets.filter((widget) => (
      widget.type === WIDGET_TYPES.LINEAR
    )).forEach((widget) => {
      this.props.fetchDeviceDashboardData({
        deviceId: this.props.params.id,
        widgetId: widget.id,
        period: data.time,
        customRange: data.customTime
      });
    });
  }

  getTimeFilerRange() {
    const time = this.props.timeFilter.time;
    const customTime = this.props.timeFilter.customTime;

    return {
      time: time,
      customTime: customTime,
    };
  }

  fetchDashboard() {
    if (this.props.orgId) {
      return this.props.fetchDeviceDashboard({
        orgId: this.props.orgId,
        deviceId: this.props.params.id,
      });
    }
  }

  render() {

    if (this.props.loading)
      return (
        <Icon type="loading" className="devices--device-dashboard-loading"/>
      );

    if (this.props.dashboard && ((!this.props.dashboard.widgets) || (!this.props.dashboard.widgets.length))) {
      return (
        <div className="product-no-fields">No Dashboard widgets</div>
      );
    }

    const widgets = this.props.dashboard.widgets.map((widget) => (
      <WidgetStatic deviceId={Number(this.props.params.id)}
                    isLive={this.props.timeFilter.time === TIMELINE_TIME_FILTERS.LIVE.key}
                    widget={widget}
                    key={widget.id}
                    history={[]}
                    loading={false}
      />
    ));

    return (
      <Grids.GridStatic deviceId={Number(this.props.params.id)}
                        widgets={widgets} webDashboard={this.props.dashboard}/>
    );
  }

}

export default DashboardScene;
