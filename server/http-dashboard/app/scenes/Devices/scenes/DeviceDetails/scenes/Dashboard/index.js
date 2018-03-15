import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {Icon} from 'antd';
import {bindActionCreators} from 'redux';
import {buildDataQueryRequestForWidgets} from 'services/Widgets';
import {Grids} from "components";
import {
  DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME,
  TIMELINE_TIME_FILTERS
} from 'services/Devices';
import {getFormValues} from 'redux-form';
import {
  DeviceDashboardDataFetch,
  DeviceDashboardFetch
} from 'data/Devices/api';
import {WidgetStatic} from "components/Widgets";

@connect((state) => ({
  orgId: state.Organization.id,
  dashboard: state.Devices.deviceDashboard,
  loading: state.Devices.deviceDashboardLoading,
  timeFilter: getFormValues(DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME)(state)
}), (dispatch) => ({
  fetchDeviceDashboard: bindActionCreators(DeviceDashboardFetch, dispatch),
  fetchDeviceDashboardData: bindActionCreators(DeviceDashboardDataFetch, dispatch)
}))
class DashboardScene extends React.Component {

  static propTypes = {
    dashboard: PropTypes.shape({
      widgets: PropTypes.array,
    }),
    params    : PropTypes.object,
    timeFilter: PropTypes.object,

    orgId: PropTypes.number,

    loading: PropTypes.bool,

    fetchDeviceDashboard    : PropTypes.func,
    fetchDeviceDashboardData: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.fetchDashboardData = this.fetchDashboardData.bind(this);
  }

  componentDidMount() {
    // loading dash widgets
    if(this.props.orgId) {
      this.fetchDashboard().then(this.fetchDashboardData);
    }
  }

  componentDidUpdate(prevProps) {
    // check props to make new fetch
    if(prevProps.params.id !== this.props.params.id && this.props.orgId)
      this.fetchDashboard().then(this.fetchDashboardData);

    if(!prevProps.orgId && this.props.orgId)
      this.fetchDashboard().then(this.fetchDashboardData);

    if(prevProps.timeFilter.time !== this.props.timeFilter.time)
      this.fetchDashboardData();

    if(prevProps.timeFilter.customTime[0] !== this.props.timeFilter.customTime[0] || prevProps.timeFilter.customTime[1] !== this.props.timeFilter.customTime[1])
      this.fetchDashboardData();

  }

  fetchDashboardData() {

    const time = this.getTimeFilerRange();

    const dataQueryRequests = buildDataQueryRequestForWidgets({
      widgets: this.props.dashboard && this.props.dashboard.widgets || [],
      timeFrom: time[0],
      timeTo: time[1],
      deviceId: this.props.params.id
    });

    if(dataQueryRequests && dataQueryRequests.length)
      this.props.fetchDeviceDashboardData({
        deviceId         : this.props.params.id,
        dataQueryRequests: dataQueryRequests,
        isLive           : this.props.timeFilter.time === TIMELINE_TIME_FILTERS.LIVE.key
      });
  }

  getTimeFilerRange() {
    const time = this.props.timeFilter.time;

    if(time === TIMELINE_TIME_FILTERS.CUSTOM.key) {
      return this.props.timeFilter.customTime;
    } else {
      return [
        TIMELINE_TIME_FILTERS[time].get(),
        new Date().getTime(),
      ];
    }
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

    if(this.props.loading)
      return (
        <Icon type="loading" className="devices--device-dashboard-loading"/>
      );

    if(this.props.dashboard && ((!this.props.dashboard.widgets) || (!this.props.dashboard.widgets.length))) {
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
      <Grids.GridStatic deviceId={Number(this.props.params.id)} widgets={widgets} webDashboard={this.props.dashboard}/>
    );
  }

}

export default DashboardScene;
