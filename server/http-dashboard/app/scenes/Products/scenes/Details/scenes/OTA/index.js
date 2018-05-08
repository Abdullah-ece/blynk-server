import React from 'react';
import OTA from './components';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {FILE_UPLOAD_URL} from 'services/API';
import {getFormValues} from 'redux-form';
import {
  ProductInfoOTAFirmwareUploadUpdate,
  ProductInfoOTADevicesSelectedDevicesUpdate
} from 'data/Product/actions';
import {OTA_STEPS} from 'services/Products';
import {
  ProductInfoDevicesOTAFetch,
  ProductInfoDevicesOTAFirmwareInfoFetch,
  ProductInfoDevicesOTAStart,
} from 'data/Product/api';
import {
  StorageOTADevicesSessionStart,
  StorageOTADevicesSessionStop,
} from 'data/Storage/actions';
import {message} from 'antd';

@connect((state) => ({
  orgId: state.Account.orgId,
  devices: state.Product.OTADevices.data,
  devicesLoading: state.Product.OTADevices.loading,
  selectedDevicesIds: state.Product.OTADevices.selectedDevicesIds,
  firmwareUploadInfo: state.Product.OTADevices.firmwareUploadInfo,
  firmwareFetchInfo: state.Product.OTADevices.firmwareFetchInfo,
  formValues: getFormValues('OTA')(state) || {},
  firmwareUpdate: state.Product.OTADevices.firmwareUpdate,
  OTAUpdate: state.Storage.OTAUpdate,
}), (dispatch) => ({
  fetchDevices: bindActionCreators(ProductInfoDevicesOTAFetch, dispatch),
  updateSelectedDevicesList: bindActionCreators(ProductInfoOTADevicesSelectedDevicesUpdate, dispatch),
  firmwareUploadChange: bindActionCreators(ProductInfoOTAFirmwareUploadUpdate, dispatch),
  firmwareInfoFetch: bindActionCreators(ProductInfoDevicesOTAFirmwareInfoFetch, dispatch),
  firmwareUpdateStart: bindActionCreators(ProductInfoDevicesOTAStart, dispatch),
  storageOTADevicesSessionStart: bindActionCreators(StorageOTADevicesSessionStart, dispatch),
  storageOTADevicesSessionStop: bindActionCreators(StorageOTADevicesSessionStop, dispatch),
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

    OTAUpdate: PropTypes.shape({
      title: PropTypes.string,
      selectedDevicesIds: PropTypes.arrayOf(PropTypes.number),
      pathToFirmware: PropTypes.string,
      productId: PropTypes.number,
      status: PropTypes.number
    }),

    updateSelectedDevicesList: PropTypes.func,

    firmwareUploadInfo: PropTypes.shape({
      name: PropTypes.string,
      uploadPercent: PropTypes.number,
      status: PropTypes.oneOf([-1,0,1,2]),
      link: PropTypes.string,
    }),

    firmwareUpdate: PropTypes.shape({
      status: PropTypes.any,
      loading: PropTypes.bool,
    }),

    params: PropTypes.shape({
      id: PropTypes.any,
    }),

    firmwareFetchInfo: PropTypes.shape({
      loading: PropTypes.bool,
      data: PropTypes.any,
    }),

    formValues: PropTypes.shape({
      firmwareName: PropTypes.any,
    }),

    selectedDevicesIds: PropTypes.arrayOf(PropTypes.number),

    orgId: PropTypes.number,

    devicesLoading: PropTypes.bool,

    fetchDevices: PropTypes.func,
    firmwareUploadChange: PropTypes.func,
    firmwareInfoFetch: PropTypes.func,
    firmwareUpdateStart: PropTypes.func,
    storageOTADevicesSessionStart: PropTypes.func,
    storageOTADevicesSessionStop: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleFileUploadChange = this.handleFileUploadChange.bind(this);
    this.handleUpdateFirmwareStart = this.handleUpdateFirmwareStart.bind(this);
    this.handleUpdateFirmwareCancel = this.handleUpdateFirmwareCancel.bind(this);
  }

  componentWillMount() {
    if(!isNaN(Number(this.props.orgId))) {
      console.log('mount');
      this.fetchDevices();
    }
  }

  componentWillReceiveProps(nextProps) {

    if(isNaN(Number(this.props.orgId)) && !isNaN(Number(nextProps.orgId))) {
      this.fetchDevices();
    }

    if(!this.props.firmwareUploadInfo.link && nextProps.firmwareUploadInfo.link) {
      this.props.firmwareInfoFetch({
        firmwareUploadUrl: nextProps.firmwareUploadInfo.link,
      });
    }

    if(this.props.firmwareUpdate.loading === true && nextProps.firmwareUpdate.loading === false && nextProps.firmwareUpdate.status === true) {
      this.fetchDevices();
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

  handleUpdateFirmwareStart() {
    this.props.firmwareUpdateStart({
      title: this.props.formValues.firmwareName,
      pathToFirmware: this.props.firmwareUploadInfo.link,
      productId: Number(this.props.params.id),
      deviceIds: this.props.selectedDevicesIds,
    }).then(() => {
      this.props.updateSelectedDevicesList([]);

      this.props.storageOTADevicesSessionStart({
        title: this.props.formValues.firmwareName,
        selectedDevicesIds: this.props.selectedDevicesIds,
        pathToFirmware: this.props.firmwareUploadInfo.link,
        firmwareFields: this.props.firmwareFetchInfo.data,
        firmwareFileName: this.props.firmwareUploadInfo.name,
        productId: Number(this.props.params.id),
      });
    });
  }

  handleUpdateFirmwareCancel() {
    this.props.storageOTADevicesSessionStop();
  }

  handleCloseSuccessUpdateFirmware() {

  }

  render() {

    const {devices, devicesLoading, firmwareUploadInfo, firmwareFetchInfo, selectedDevicesIds, firmwareUpdate, OTAUpdate} = this.props;

    let step = OTA_STEPS.UPLOAD_FIRMWARE;

    if(firmwareUploadInfo.status === 1) {
      step = OTA_STEPS.START_UPDATE;
    }

    if(this.props.OTAUpdate.status === 1) {
      step = OTA_STEPS.UPDATING;
    }

    return (
      <OTA step={step}
           OTAUpdate={OTAUpdate}
           firmwareUpdate={firmwareUpdate}
           selectedDevicesIds={selectedDevicesIds}
           devices={devices}
           devicesLoading={devicesLoading}
           fileUploadOptions={this.fileUploadOptions}
           firmwareFetchInfo={firmwareFetchInfo}
           firmwareUploadInfo={firmwareUploadInfo}
           onDeviceSelect={this.props.updateSelectedDevicesList}
           onFirmwareUpdateStart={this.handleUpdateFirmwareStart}
           onFirmwareUpdateCancel={this.handleUpdateFirmwareCancel}
      />
    );
  }
}

export default OTAScene;
