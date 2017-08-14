import React from 'react';
import {Button, Tooltip} from 'antd';
import DeviceCreateModal from '../DeviceCreateModal';
import {
  reduxForm,
} from 'redux-form';
import {
  DEVICES_FILTER_FORM_NAME,
  DEVICES_FILTERS,
} from 'services/Devices';
import './styles.less';

@reduxForm({
  form: DEVICES_FILTER_FORM_NAME,
  initialValues: {
    filter: DEVICES_FILTERS.DEFAULT
  }
})
class DevicesToolbar extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object,
  };

  static propTypes = {
    filterValue: React.PropTypes.string,

    onFilterChange: React.PropTypes.func,

    location: React.PropTypes.object,
    params: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    this.handleAllDevicesSelect = this.handleFilterSelect.bind(this, DEVICES_FILTERS.ALL_DEVICES);
    this.handleByLocationSelect = this.handleFilterSelect.bind(this, DEVICES_FILTERS.BY_LOCATION);
    this.handleByProductSelect = this.handleFilterSelect.bind(this, DEVICES_FILTERS.BY_PRODUCT);
  }

  state = {
    isDeviceCreateModalVisible: false
  };

  componentWillMount() {
    this.checkModalVisibility();
  }

  shouldComponentUpdate(nextProps, nextState) {
    return (
      this.props.filterValue !== nextProps.filterValue ||
      this.props.location.pathname !== nextProps.location.pathname ||
      this.state.isDeviceCreateModalVisible !== nextState.isDeviceCreateModalVisible
    );
  }

  componentDidUpdate() {
    this.checkModalVisibility();
  }

  onDeviceCreateModalClose() {
    this.context.router.push(`/devices/${this.props.params.id}`);
  }

  handleDeviceCreateClick() {
    this.context.router.push(`/devices/${this.props.params.id}/create`);
  }

  checkModalVisibility() {
    if (this.props.location.pathname.indexOf('create') !== -1 && !this.state.isDeviceCreateModalVisible) {
      this.setState({
        isDeviceCreateModalVisible: true
      });
    } else if (this.props.location.pathname.indexOf('create') === -1 && this.state.isDeviceCreateModalVisible) {
      this.setState({
        isDeviceCreateModalVisible: false
      });
    }
  }

  handleFilterSelect(value) {
    this.props.onFilterChange(value);
  }

  render() {

    const {filterValue} = this.props;

    return (
      <div className="devices--toolbar">
        <Tooltip placement="bottomRight" title="All Devices" mouseEnterDelay={.75}>
          <Button icon="switcher" size="small"
                  onClick={this.handleAllDevicesSelect}
                  className={filterValue === DEVICES_FILTERS.ALL_DEVICES ? 'active' : null}/>
        </Tooltip>
        <Tooltip placement="bottomRight" title="Filter By Location"  mouseEnterDelay={.75}>
          <Button icon="environment-o" size="small"
                  onClick={this.handleByLocationSelect}
                  className={filterValue === DEVICES_FILTERS.BY_LOCATION ? 'active' : null}/>
        </Tooltip>
        <Tooltip placement="bottom" title="Filter By Product"  mouseEnterDelay={.75}>
          <Button icon="appstore-o" size="small" disabled={true}
                  onClick={this.handleByProductSelect}
                  className={filterValue === DEVICES_FILTERS.BY_PRODUCT ? 'active' : null}/>
        </Tooltip>
        <span/>
        <Tooltip placement="bottomRight" title="Create new device">
          <Button icon="plus-square-o" size="small" onClick={this.handleDeviceCreateClick.bind(this)}/>
        </Tooltip>

        <DeviceCreateModal visible={this.state.isDeviceCreateModalVisible}
                           onClose={this.onDeviceCreateModalClose.bind(this)}/>

      </div>
    );
  }

}

export default DevicesToolbar;
