import React from 'react';
import './styles.less';
import { Link } from 'react-router';
import { Icon } from 'antd';
// import {Map} from 'immutable';
import PropTypes from 'prop-types';
// import {Icon} from 'antd';
// import {TIMELINE_TIME_FILTERS} from 'services/Devices';
import {connect} from 'react-redux';
import { VerifyPermission, PERMISSIONS_INDEX } from "services/Roles";
// import {bindActionCreators} from 'redux';
// import {WidgetsHistory} from 'data/Widgets/api';
// import {TimeFiltering} from './scenes';
// import {getFormValues, initialize} from 'redux-form';
//
// import {buildDataQueryRequestForWidgets} from 'services/Widgets';
// import {Grids} from "components";
// import {WidgetStatic} from "components/Widgets";
//
// import {DeviceTimeFilterUpdate} from 'data/Devices/actions';
//
// import {blynkWsHardware} from 'store/blynk-websocket-middleware/actions';

// const DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME = 'device-dashboard-time-filtering';

import TimeFiltering
  from 'scenes/Devices/scenes/DeviceDetails/scenes/TimeFiltering';
import WidgetsDashboard
  from 'scenes/Devices/scenes/DeviceDetails/scenes/Dashboard';

@connect((state) => ({
  permissions: state.RolesAndPermissions.currentRole.permissionGroup1,
}), () => ({}))
class Dashboard extends React.Component {

  static propTypes = {
    // dashboard: PropTypes.instanceOf(Map),
    // widgets: PropTypes.instanceOf(Map),
    params: PropTypes.object,
    productId: PropTypes.number,
    permissions: React.PropTypes.number,
    // timeFilteringValues: PropTypes.object,
    // fetchWidgetHistory: PropTypes.func,
    // initializeForm: PropTypes.func,
    // blynkWsHardware: PropTypes.func,
    // deviceTimeFilterUpdate: PropTypes.func,
  };

  constructor(props) {
    super(props);

    // this.state = {
    //   filter: this.FILTERS.HOUR,
    //   editable: false
    // };
    //
    // this.handleTimeFilterChange = this.handleTimeFilterChange.bind(this);
    // this.handleWidgetWriteVirtualPin = this.handleWidgetWriteVirtualPin.bind(this, props.params.id);
  }

  // componentWillMount() {
  //
  //   this.fetchWidgetsData();
  // }

  // componentDidUpdate(prevProps) {
  //
  //   if((prevProps.params && prevProps.params.id && Number(prevProps.params.id) !== Number(this.props.params.id))) {
  //     this.fetchWidgetsData();
  //   }
  //
  // }

  // fetchWidgetsData(params = {}) {
  //
  //   let dataQueryRequests = [];
  //
  //   const dashboard = this.props.dashboard;
  //
  //   let timeFilter = {};
  //
  //   if (params.from === undefined) {
  //     timeFilter = this.getTimeOffsetForData({time: TIMELINE_TIME_FILTERS.LIVE.key});
  //   } else {
  //     timeFilter = {
  //       from: params.from,
  //       to: params.to,
  //     };
  //   }
  //
  //   if (dashboard.has('widgets') && dashboard.get('widgets').size)
  //
  //     dataQueryRequests = buildDataQueryRequestForWidgets({
  //       widgets: dashboard.get('widgets').toJS(),
  //       deviceId: this.props.params.id,
  //       timeFrom: timeFilter.from,
  //       timeTo: timeFilter.to,
  //     });
  //
  //   if (dataQueryRequests.length)
  //     this.props.fetchWidgetHistory({
  //       deviceId: this.props.params.id,
  //       dataQueryRequests: dataQueryRequests,
  //     });
  //
  // }

  // FILTERS = {
  //   HOUR: 'hour',
  //   DAY: 'day',
  //   WEEK: 'week',
  //   MONTH: 'month',
  //   CUSTOM: 'custom'
  // };

  // FILTER_BUTTONS = [{
  //   key: this.FILTERS.HOUR,
  //   value: 'Last hour'
  // }, {
  //   key: this.FILTERS.DAY,
  //   value: 'Last day'
  // }, {
  //   key: this.FILTERS.WEEK,
  //   value: 'Last Week'
  // }, {
  //   key: this.FILTERS.MONTH,
  //   value: 'Last Month'
  // }, {
  //   key: this.FILTERS.CUSTOM,
  //   value: 'Custom Range'
  // }];

  // filterBy(key) {
  //   this.setState({
  //     filter: key
  //   });
  // }
  //
  // startEditDashboard() {
  //   this.setState({
  //     editable: true
  //   });
  // }
  //
  // finishEditDashboard() {
  //   this.setState({
  //     editable: false
  //   });
  // }
  //
  // getTimeOffsetForData(values) {
  //
  //   const params = {};
  //
  //   if (values.time === TIMELINE_TIME_FILTERS.CUSTOM.key) {
  //     if (!values.customTime || !values.customTime.length)
  //       return false;
  //     params.from = values.customTime[0];
  //     params.to = values.customTime[1];
  //   } else {
  //     params.from = new Date().getTime() - TIMELINE_TIME_FILTERS[values.time].time;
  //     params.to = new Date().getTime();
  //   }
  //
  //   return params;
  // }
  //
  // handleTimeFilterChange(values) {
  //
  //   const params = {
  //     ...this.getTimeOffsetForData(values) || {}
  //   };
  //
  //   this.props.deviceTimeFilterUpdate(values.time);
  //
  //   this.fetchWidgetsData(params);
  //
  // }
  //
  // handleWidgetWriteVirtualPin(deviceId, {pin, value}) {
  //   this.props.blynkWsHardware({
  //     deviceId: deviceId,
  //     pin: pin,
  //     value: value
  //   });
  // }

  render() {

    // const deviceId = Number(this.props.params.id);
    //
    // let isLoading = false;
    // if (this.props.widgets.hasIn([deviceId, 'loading']) && this.props.widgets.getIn([deviceId, 'loading']))
    //   isLoading = true;
    //
    // if (!isLoading && (!this.props.dashboard.has('widgets') || !this.props.dashboard.get('widgets').size))
    //   return (
    //      <div className="devices--device-dashboard">
    //        <div className="product-no-fields" style={{padding: 0}}>No Dashboard widgets</div>
    //      </div>
    //    );
    //
    // let widgets = this.props.dashboard.get('widgets').map((widget) => {
    //
    //   const history = this.props.widgets.getIn([
    //     String(deviceId),
    //     String(widget.get('id'))
    //   ]);
    //
    //   const loading = this.props.widgets.getIn([
    //     String(deviceId),
    //     'loading'
    //   ]);
    //
    //   return (
    //     <WidgetStatic onWriteToVirtualPin={this.handleWidgetWriteVirtualPin} widget={widget.toJS()} key={widget.get('id')} history={history} loading={loading}/>
    //   );
    // }).toJS();
    //
    // const timeFilteringInitialValues = {
    //   time: TIMELINE_TIME_FILTERS.LIVE.key
    // };

    return (
      <div className="devices--device-dashboard">

        <div>
          <div className="devices--device-dashboard-header-container">
            <TimeFiltering params={this.props.params}/>
            {VerifyPermission(this.props.permissions, PERMISSIONS_INDEX.PRODUCT_EDIT)  && (<div className="devices--device-dashboard-edit-link">
              <Link
                to={"/products/edit/" + this.props.productId + "/dashboard"}>
                <Icon type="edit" style={{
                  fontSize: 25,
                  color: 'rgba(32, 34, 39, 0.75)'
                }}/>
              </Link>
            </div>)}
          </div>

          <WidgetsDashboard params={this.props.params}/>
        </div>

      </div>
    );

    // return (
    //   <div className="devices--device-dashboard">
    //
    //     <div>
    //       <TimeFiltering onChange={this.handleTimeFilterChange} form={DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME}
    //                      formValues={this.props.timeFilteringValues} initialValues={timeFilteringInitialValues}/>
    //     </div>
    //
    //     { isLoading && (
    //         <Icon type="loading" className="devices--device-dashboard-loading"/>
    //     ) || (
    //       <Grids.GridStatic deviceId={deviceId} widgets={widgets} webDashboard={this.props.dashboard.toJS()}/>
    //     )}
    //
    //   </div>
    // );
  }

}

export default Dashboard;
