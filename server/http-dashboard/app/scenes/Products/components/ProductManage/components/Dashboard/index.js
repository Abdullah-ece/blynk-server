import React from 'react';
import {
  AddWidgetTools,
  Grid,
  DeviceSelect,
} from './components';
import PropTypes from 'prop-types';
import {getNextId} from 'services/Products';
import {ProductDashboardDeviceIdForPreviewChange} from 'data/Product/actions';
import {fromJS, List} from 'immutable';
import {getCoordinatesToSet, buildDataQueryRequestForWidgets} from 'services/Widgets';
import './styles.less';
import _ from 'lodash';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {DevicesListForProductDashboardPreviewFetch} from 'data/Product/api';
import {WidgetsHistory} from 'data/Widgets/api';

@connect((state) => ({
  orgId: Number(state.Account.orgId),
  devicesListForPreview: fromJS(state.Product.dashboardPreview.devicesList || []),
  devicePreviewId: Number(state.Product.dashboardPreview.selectedDeviceId),
  devicesLoading: state.Devices.devicesLoading,
}), (dispatch) => ({
  changeDeviceIdForPreview: bindActionCreators(ProductDashboardDeviceIdForPreviewChange, dispatch),
  fetchDevicesListForPreview: bindActionCreators(DevicesListForProductDashboardPreviewFetch, dispatch),
  fetchWidgetHistory: bindActionCreators(WidgetsHistory, dispatch),
}))
class Dashboard extends React.Component {

  static propTypes = {
    onWidgetAdd: PropTypes.func,
    onWidgetsChange: PropTypes.func,
    fetchWidgetHistory: PropTypes.func,
    changeDeviceIdForPreview: PropTypes.func,

    orgId: PropTypes.number,
    productId: PropTypes.number,

    devicePreviewId: PropTypes.number,

    devicesListForPreview: PropTypes.instanceOf(List),

    widgets: PropTypes.instanceOf(List),

    fetchDevicesListForPreview: PropTypes.func,

    devicesLoading: PropTypes.bool,

    fields: PropTypes.object,
  };

  constructor(props) {
    super(props);

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
    this.handleWidgetDelete = this.handleWidgetDelete.bind(this);
    this.handleWidgetClone = this.handleWidgetClone.bind(this);
    this.handleDevicePreviewIdChange = this.handleDevicePreviewIdChange.bind(this);
  }

  componentWillMount() {
    this.props.fetchDevicesListForPreview({
      orgId: this.props.orgId,
      productId: this.props.productId,
    });
  }

  componentDidUpdate(prevProps) {

    if(!_.isEqual(prevProps.fields, this.props.fields)) {
      console.log('new fields', prevProps.fields.getAll(), this.props.fields.getAll());
    }

    if(prevProps.devicePreviewId !== this.props.devicePreviewId) {
      this.getDataForWidgets();
    }
  }

  getDataForWidgets() {


    let dataQueryRequests = [];

    console.log('dataQueryRequestFields', this.props.fields.getAll());

    if (this.props.fields.getAll() && this.props.fields.getAll().length && this.props.devicePreviewId)

      dataQueryRequests = buildDataQueryRequestForWidgets({
        widgets: this.props.fields.getAll(),
        deviceId: this.props.devicePreviewId,
        timeFrom: new Date().getTime() - 1000 * 60 * 60 * 24 * 7, // 7 days ago,
        timeTo: new Date().getTime()
      });

    if (dataQueryRequests.length)
      this.props.fetchWidgetHistory({
        deviceId: this.props.devicePreviewId,
        dataQueryRequests: dataQueryRequests,
      });

  }

  handleWidgetDelete(id) {
    let fieldIndex = null;

    this.props.fields.getAll().forEach((field, index) => {
      if(Number(field.id) === Number(id))
        fieldIndex = index;
    });

    this.props.fields.remove(fieldIndex);
  }

  handleWidgetClone(id, breakPoint) {

    const widgets = this.props.fields.getAll();

    const widget = _.find(widgets, (widget) => Number(widget.id) === id);

    const coordinatesForNewWidget = getCoordinatesToSet(widget, widgets, breakPoint);

    this.props.fields.push({
      ...widget,
      id: getNextId(this.props.fields.getAll()),
      label: `${widget.label} Copy`,
      x: coordinatesForNewWidget.x,
      y: coordinatesForNewWidget.y,
    });

  }

  handleWidgetAdd(widget) {

    const widgets = this.props.fields.getAll();

    const coordinatesForNewWidget = getCoordinatesToSet(widget, widgets, 'lg'); //hardcoded breakPoint as we have only lg for now

    this.props.fields.push({
      ...widget,
      id: getNextId(this.props.fields.getAll()),
      x: coordinatesForNewWidget.x,
      y: coordinatesForNewWidget.y,
      width: widget.w,
      height: widget.h,
    });
  }

  handleDevicePreviewIdChange(id) {
    this.props.changeDeviceIdForPreview(id);
  }

  render() {

    const {devicesListForPreview} = this.props;

    const widgets = fromJS(this.props.fields.map((prefix, index, fields) => {
      const field = fields.get(index);
      return {
        ...field,
        fieldName: prefix,
        w: field.width,
        h: field.height,
      };
    }));

    const params = {
      id: 1
    };

    return (
      <div className="products-manage-dashboard">

        <div className={`products-manage-dashboard--tools`}>
          <div className={`products-manage-dashboard--tools--widget-add`}>
            <AddWidgetTools onWidgetAdd={this.handleWidgetAdd}/>
          </div>
          { this.props.productId && (
            <div className={`products-manage-dashboard--tools--device-select`}>
              <DeviceSelect loading={this.props.devicesLoading} devicesList={devicesListForPreview}
                            value={Number(this.props.devicePreviewId)} onChange={this.handleDevicePreviewIdChange}/>
            </div>
          )}
        </div>

        <Grid widgets={widgets}
              params={params}
              deviceId={Number(this.props.devicePreviewId)}
              onWidgetDelete={this.handleWidgetDelete}
              onWidgetClone={this.handleWidgetClone}
              onChange={this.handleWidgetsChange}
        />

      </div>
    );
  }

}

export default Dashboard;
