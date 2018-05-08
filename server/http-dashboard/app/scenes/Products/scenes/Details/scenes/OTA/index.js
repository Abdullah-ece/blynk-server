import React from 'react';
import OTA from './components';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {FILE_UPLOAD_URL} from 'services/API';
import {
  ProductInfoOTAFirmwareUploadUpdate,
  ProductInfoOTADevicesSelectedDevicesUpdate
} from 'data/Product/actions';
import {OTA_STEPS} from 'services/Products';
import {
  ProductInfoDevicesOTAFetch,
  ProductInfoDevicesOTAFirmwareInfoFetch
} from 'data/Product/api';
import {message} from 'antd';

@connect((state) => ({
  orgId: state.Account.orgId,
  devices: state.Product.OTADevices.data,
  devicesLoading: state.Product.OTADevices.loading,
  selectedDevicesIds: state.Product.OTADevices.selectedDevicesIds,
  firmwareUploadInfo: state.Product.OTADevices.firmwareUploadInfo,
  firmwareFetchInfo: state.Product.OTADevices.firmwareFetchInfo,
}), (dispatch) => ({
  fetchDevices: bindActionCreators(ProductInfoDevicesOTAFetch, dispatch),
  updateSelectedDevicesList: bindActionCreators(ProductInfoOTADevicesSelectedDevicesUpdate, dispatch),
  firmwareUploadChange: bindActionCreators(ProductInfoOTAFirmwareUploadUpdate, dispatch),
  firmwareInfoFetch: bindActionCreators(ProductInfoDevicesOTAFirmwareInfoFetch, dispatch),
}))
class OTAScene extends React.Component {

  static propTypes = {
    devices: PropTypes.arrayOf(PropTypes.shape({
      id            : PropTypes.number,
      name          : PropTypes.string,
      status        : PropTypes.oneOf(['ONLINE', 'OFFLINE']), // use this for column "status" and display like a green / gray dot
      disconnectTime: PropTypes.number, // display "Was online N days ago" when user do mouseover the gray dot (idea is to display last time when device was online if it's offline right now)
      hardwareInfo  : PropTypes.shape({
        version: PropTypes.string
      })
    })),

    updateSelectedDevicesList: PropTypes.func,

    firmwareUploadInfo: PropTypes.shape({
      uploadPercent: PropTypes.number,
      status: PropTypes.oneOf([-1,0,1,2]),
      link: PropTypes.string,
    }),

    firmwareFetchInfo: PropTypes.shape({
      loading: PropTypes.bool,
      data: PropTypes.any,
    }),

    selectedDevicesIds: PropTypes.arrayOf(PropTypes.number),

    orgId: PropTypes.number,

    devicesLoading: PropTypes.bool,

    fetchDevices: PropTypes.func,
    firmwareUploadChange: PropTypes.func,
    firmwareInfoFetch: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleFileUploadChange = this.handleFileUploadChange.bind(this);
  }

  componentWillMount() {
    if(!isNaN(Number(this.props.orgId))) {
      console.log('mount');
      this.fetchDevices();
    }
  }

  componentWillReceiveProps(nextProps) {

    console.log(this.props.orgId, nextProps.orgId);

    if(isNaN(Number(this.props.orgId)) && !isNaN(Number(nextProps.orgId))) {
      console.log('nice');
      this.fetchDevices();
    }

    if(!this.props.firmwareUploadInfo.link && nextProps.firmwareUploadInfo.link) {
      this.props.firmwareInfoFetch({
        firmwareUploadUrl: nextProps.firmwareUploadInfo.link,
      });
    }
  }

  fileUploadOptions = {
    name: 'file',
    action: FILE_UPLOAD_URL,
    showUploadList: false,
    accept: 'application/octet-stream',
    onChange: this.handleFileUploadChange.bind(this)
  };

  steps = {
    UPLOAD_FIRMWARE: 'UPLOAD_FIRMWARE',
    START_UPDATE   : 'START_UPDATE',
    UPDATING       : 'UPDATING',
    SUCCESS        : 'SUCCESS'
  };

  fetchDevices() {
    console.log('Do fetch devices');
    this.props.fetchDevices({
      orgId: this.props.orgId
    }).catch(() => {
      message.error('Cannot fetch devices for OTA update');
    });
  }

  handleFilterUpdate() {

  }

  handleSortUpdate() {

  }

  handleDeviceSelect() {

  }

  handleFileUploadChange(info) {
    const UPLOADING = 'uploading';
    const DONE = 'done';
    const ERROR = 'error';

    if(info.file.status === UPLOADING) {
      this.props.firmwareUploadChange({
        uploadPercent: info.file.status.percent,
        status: 0,
        name: info.file.name,
      });
    }

    if(info.file.status === DONE) {
      this.props.firmwareUploadChange({
        uploadPercent: 100,
        status: 1,
        name: info.file.name,
        link: info.file.response,
      });
    }

    if(info.file.status === ERROR) {
      this.props.firmwareUploadChange({
        uploadPercent: 0,
        status: 2,
        name: info.file.name,
      });
    }

  }

  handleUpdateFirmware() {

  }

  handleCancelUpdateFirmware() {

  }

  handleCloseSuccessUpdateFirmware() {

  }

  render() {

    const {devices, devicesLoading, firmwareUploadInfo, firmwareFetchInfo, selectedDevicesIds} = this.props;

    let step = OTA_STEPS.UPLOAD_FIRMWARE;

    if(firmwareUploadInfo.status === 1) {
      step = OTA_STEPS.START_UPDATE;
    }

    return (
      <OTA step={step}
           selectedDevicesIds={selectedDevicesIds}
           devices={devices}
           devicesLoading={devicesLoading}
           fileUploadOptions={this.fileUploadOptions}
           firmwareFetchInfo={firmwareFetchInfo}
           firmwareUploadInfo={firmwareUploadInfo}
           onDeviceSelect={this.props.updateSelectedDevicesList}/>
    );
  }
}

export default OTAScene;
