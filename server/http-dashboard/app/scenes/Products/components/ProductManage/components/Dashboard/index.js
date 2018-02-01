import React from 'react';
import {
  AddWidgetTools,
  Grid,
  DeviceSelect,
} from './components';
import PropTypes from 'prop-types';
import {getNextId} from 'services/Products';
import {fromJS, List} from 'immutable';
import {getCoordinatesToSet} from 'services/Widgets';
import './styles.less';
import _ from 'lodash';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {DevicesFetch} from 'data/Devices/api';

@connect((state, ownProps) => ({
  orgId: Number(state.Account.orgId),
  devicesListForPreview: state.Devices.get('devices').filter((device) => {
    return Number(device.get('productId')) === Number(ownProps.productId);
  }),
  devicesLoading: state.Devices.devicesLoading,
}), (dispatch) => ({
  fetchDevicesList: bindActionCreators(DevicesFetch, dispatch)
}))
class Dashboard extends React.Component {

  static propTypes = {
    onWidgetAdd: PropTypes.func,
    onWidgetsChange: PropTypes.func,
    fetchDevicesList: PropTypes.func,
    onDeviceForPreviewChange: PropTypes.func,

    orgId: PropTypes.number,
    productId: PropTypes.number,

    selectedDeviceIdForPreview: PropTypes.any,

    devicesListForPreview: PropTypes.instanceOf(List),

    widgets: PropTypes.instanceOf(List),

    devicesLoading: PropTypes.bool,

    fields: PropTypes.object,
  };

  constructor(props) {
    super(props);

    this.state = {
      devicePreviewId: '',
    };

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
    this.handleWidgetDelete = this.handleWidgetDelete.bind(this);
    this.handleWidgetClone = this.handleWidgetClone.bind(this);
    this.handleDevicePreviewIdChange = this.handleDevicePreviewIdChange.bind(this);
  }

  componentWillMount() {
    this.props.fetchDevicesList({
      orgId: this.props.orgId
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
    });
  }

  handleDevicePreviewIdChange(id) {
    this.setState({
      devicePreviewId: id
    });
  }

  render() {

    const {devicesListForPreview} = this.props;

    const widgets = fromJS(this.props.fields.map((prefix, index, fields) => {
      return {
        ...fields.get(index),
        fieldName: prefix,
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
                            value={this.state.devicePreviewId} onChange={this.handleDevicePreviewIdChange}/>
            </div>
          )}
        </div>

        <Grid widgets={widgets}
              params={params}
              deviceId={Number(this.state.devicePreviewId)}
              onWidgetDelete={this.handleWidgetDelete}
              onWidgetClone={this.handleWidgetClone}
              onChange={this.handleWidgetsChange}
        />

      </div>
    );
  }

}

export default Dashboard;
