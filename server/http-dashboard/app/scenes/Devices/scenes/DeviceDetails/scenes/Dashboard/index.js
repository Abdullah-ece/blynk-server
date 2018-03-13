import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {Icon} from 'antd';
import {bindActionCreators} from 'redux';
import {
  DeviceDashboardFetch
} from 'data/Devices/api';

@connect((state) => ({
  orgId: state.Organization.id,
  dashboard: state.Devices.deviceDashboard,
  loading: state.Devices.deviceDashboardLoading,
}), (dispatch) => ({
  fetchDeviceDashboard: bindActionCreators(DeviceDashboardFetch, dispatch)
}))
class DashboardScene extends React.Component {

  static propTypes = {
    params: PropTypes.object,

    orgId: PropTypes.number,

    loading: PropTypes.bool,

    fetchDeviceDashboard: PropTypes.func,
  };

  componentDidMount() {
    // loading dash widgets
    this.fetchDashboard();
  }

  componentDidUpdate(prevProps) {
    // check props to make new fetch
    if(prevProps.params.id !== this.props.params.id)
      this.fetchDashboard();

    if(!prevProps.orgId && this.props.orgId)
      this.fetchDashboard();
  }

  fetchData() {

  }

  fetchDashboard() {
    if (this.props.orgId) {
      return this.props.fetchDeviceDashboard({
        orgId: this.props.orgId
      });
    }
  }

  render() {
    return (
      <div>
        { this.props.loading ? <Icon type="loading" /> : "dash"}
      </div>
    );
  }

}

export default DashboardScene;
