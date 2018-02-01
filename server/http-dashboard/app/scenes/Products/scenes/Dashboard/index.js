import React from 'react';
import {fromJS, Map} from 'immutable';

import {
  Dashboard
} from '../../components/ProductManage/components';

import {FORMS} from 'services/Products';

import {DevicesListForProductDashboardPreviewFetch} from 'data/Product/api';
import {ProductDashboardDeviceIdForPreviewChange} from 'data/Product/actions';
import {buildDataQueryRequestForWidgets, getCoordinatesToSet} from 'services/Widgets';
import {WidgetsHistory} from 'data/Widgets/api';

import {
  getFormValues,
  change,
} from 'redux-form';
import PropTypes from 'prop-types';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

@connect((state) => ({
  orgId: state.Account.orgId,
  dashboard: fromJS(getFormValues(FORMS.DASHBOARD)(state) || {}),
  devicesList: state.Product.dashboardPreview.devicesList,
  selectedDeviceId: state.Product.dashboardPreview.selectedDeviceId,
  historyLoading: state.Widgets.get('widgetsData'),
}), (dispatch) => ({
  changeFormValue: bindActionCreators(change, dispatch),
  fetchDevicesListForPreview: bindActionCreators(DevicesListForProductDashboardPreviewFetch, dispatch),
  changeDeviceIdForPreview: bindActionCreators(ProductDashboardDeviceIdForPreviewChange, dispatch),
  fetchWidgetHistory: bindActionCreators(WidgetsHistory, dispatch),
}))
class DashboardScene extends React.Component {

  static propTypes = {
    dashboard: PropTypes.instanceOf(Map),

    params: PropTypes.shape({
      id: PropTypes.number.isRequired
    }).isRequired,

    orgId: PropTypes.any,
    historyLoading: PropTypes.any,

    devicesList: PropTypes.array,
    selectedDeviceId: PropTypes.any,

    changeFormValue: PropTypes.func,
    fetchWidgetHistory: PropTypes.func,
    fetchDevicesListForPreview: PropTypes.func,
    changeDeviceIdForPreview: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
    this.handleWidgetsChange = this.handleWidgetsChange.bind(this);
    this.handleDeviceForPreviewChange = this.handleDeviceForPreviewChange.bind(this);
  }

  componentWillMount() {
    this.props.fetchDevicesListForPreview({
      orgId: this.props.orgId,
    });
  }

  componentDidUpdate(prevProps) {
    if(prevProps.selectedDeviceId !== this.props.selectedDeviceId) {
      this.getDataForWidgets();
    }
  }

  handleWidgetAdd(widget) {

    widget.id = this.props.dashboard.get('widgets').reduce((acc, item) => {
      return Number(item.get('id')) > acc ? Number(item.get('id')) : acc;
    }, 0) + 1;

    const coords = (getCoordinatesToSet(widget, this.props.dashboard.get('widgets').toJS(), 'lg'));

    widget.x = coords.x;
    widget.y = coords.y;

    widget.name = widget.type + widget.id;
    this.props.changeFormValue(FORMS.DASHBOARD, 'widgets', this.props.dashboard.get('widgets').unshift(fromJS(widget) ));
  }

  handleWidgetsChange(widgets) {

    let updatedWidgets = widgets.map((widget) => {
      let data = this.props.dashboard.get('widgets').find((w) => Number(w.get('id')) === Number(widget.i));

      return fromJS({
        ...data.toJS(),
        ...widget
      });
    });

    this.props.changeFormValue(FORMS.DASHBOARD, 'widgets', updatedWidgets);
  }

  handleDeviceForPreviewChange(value) {
    this.props.changeDeviceIdForPreview(value);
  }

  getDataForWidgets() {

    let dataQueryRequests = [];

    const { dashboard } = this.props;

    if (dashboard.has('widgets') && dashboard.get('widgets').size && this.props.selectedDeviceId)

      dataQueryRequests = buildDataQueryRequestForWidgets({
        widgets: dashboard.get('widgets').toJS(),
        deviceId: this.props.selectedDeviceId,
        timeFrom: new Date().getTime() - 1000 * 60 * 60 * 24 * 7, // 7 days ago,
        timeTo: new Date().getTime()
      });

    if (dataQueryRequests.length)
      this.props.fetchWidgetHistory({
        deviceId: this.props.selectedDeviceId,
        dataQueryRequests: dataQueryRequests,
      });

  }

  render() {

    const widgets = this.props.dashboard.get('widgets');

    return (
      <Dashboard widgets={widgets}
                 params={this.props.params}
                 onWidgetAdd={this.handleWidgetAdd}
                 onWidgetsChange={this.handleWidgetsChange}
                 devicesListForPreview={this.props.devicesList}
                 selectedDeviceIdForPreview={this.props.selectedDeviceId}
                 onDeviceForPreviewChange={this.handleDeviceForPreviewChange}
      />
    );
  }

}

export default DashboardScene;
