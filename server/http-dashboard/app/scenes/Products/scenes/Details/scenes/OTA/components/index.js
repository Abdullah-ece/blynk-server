import React from 'react';
import {Table, Button, Row, Col, Upload, Icon, Progress} from 'antd';
import Modal from 'components/Modal';
import PropTypes from 'prop-types';
import {reduxForm} from 'redux-form';
import {
  Item
} from "components/UI";
import {
  Field as FormField
} from 'components/Form';

import {OTA_STEPS} from 'services/Products';

import './styles.less';
import Validation from "services/Validation";

import DeviceStatus from './components/DeviceStatus';
import OTAStatus from './components/OTAStatus';
import {getCalendarFormatDate} from "services/Date";

@reduxForm({
  form: 'OTA'
})
class OTA extends React.Component {

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
      firmwareFileName: PropTypes.string,
      productId: PropTypes.number,
      status: PropTypes.number
    }),

    updatingProgress: PropTypes.number,
    devicesUpdated: PropTypes.array,

    onDeviceSelect: PropTypes.func,

    devicesLoading: PropTypes.bool,

    fileUploadOptions    : PropTypes.shape({
      name          : PropTypes.string,
      showUploadList: PropTypes.bool,
      accept        : PropTypes.string,
      onChange      : PropTypes.func,
    }),
    firmwareUploadInfo   : PropTypes.shape({
      uploadPercent: PropTypes.number,
      status       : PropTypes.oneOf([-1, 0, 1, 2]),
      link         : PropTypes.string,
    }),
    firmwareUpdate: PropTypes.shape({
      status: PropTypes.any,
      loading: PropTypes.bool,
      cancelLoading: PropTypes.bool,
    }),
    firmwareFetchInfo    : PropTypes.shape({
      loading: PropTypes.bool,
      data   : PropTypes.any,
    }),
    selectedDevicesIds   : PropTypes.arrayOf(PropTypes.number),
    step                 : PropTypes.oneOf([
      OTA_STEPS.START_UPDATE,
      OTA_STEPS.SUCCESS,
      OTA_STEPS.UPDATING,
      OTA_STEPS.UPLOAD_FIRMWARE,
    ]),
    onFirmwareUpdateStart: PropTypes.func,
    onFirmwareUpdateCancel: PropTypes.func,
    onFirmwareUpdateProcessCancel: PropTypes.func,
    onModalClose: PropTypes.func,
    onModalOpen: PropTypes.func,

    dateStarted: PropTypes.any,
    dateFinished: PropTypes.any,

    modalVisible: PropTypes.bool,
    invalid: PropTypes.bool,
  };

  constructor(props) {
    super(props);

    this.firmwareUpdateStart = this.firmwareUpdateStart.bind(this);
    this.handleCancelModalOk = this.handleCancelModalOk.bind(this);
    this.handleCancelModalCancel = this.handleCancelModalCancel.bind(this);
  }

  uploadFirmware() {
    const {firmwareUploadInfo} = this.props;
    return (
      <div className="devices-ota-update-upload">
        <h4>Firmware update:</h4>
        <Upload {...this.props.fileUploadOptions}>
          <Button type="dashed"><Icon type="plus"/>Upload new firmware</Button>
        </Upload>
        {firmwareUploadInfo.uploadPercent > 0 ? (
          <div className="devices-ota-update-upload-progress">
            <Progress percent={firmwareUploadInfo.uploadPercent} strokeWidth={5}/>
          </div>
        ) : (
          null
        )}
      </div>
    );
  }

  firmwareUpdateStart() {
    if (typeof this.props.onFirmwareUpdateStart === 'function') {
      this.props.onFirmwareUpdateStart();
    }
  }

  updateConfirmation() {

    const {firmwareUploadInfo, firmwareFetchInfo, selectedDevicesIds} = this.props;

    const fields = Object.keys(firmwareFetchInfo.data).map((key) => {
      return {
        key  : key,
        value: firmwareFetchInfo.data[key],
      };
    });

    return (
      <div className="devices-ota-update-confirmation">
        <div className="devices-ota-update-confirmation-name">
          <Item label="Firmware Name" offset="medium">
            <FormField name={'firmwareName'} placeholder={'Example: Blynk v1.0.0'} validate={[Validation.Rules.required]}/>
          </Item>
        </div>
        <div className="devices-ota-update-confirmation-file-name">
          {firmwareUploadInfo.name}
        </div>
        {firmwareFetchInfo.loading ? (
          <div className="devices-ota-update-confirmation-fields-list">
            <Icon type="loading"/>
          </div>
        ) : fields.length ?
          (<div className="devices-ota-update-confirmation-fields-list">
            {fields.map((field, key) => (
              <div className="devices-ota-update-confirmation-fields-list-item" key={key}>
                {field.key}: {field.value}
              </div>
            ))}
          </div>)
          :
          (null)
        }
        <div className="devices-ota-update-confirmation-footer">
          <Row>
            <Col span={12}>
              <div className="devices-ota-update-confirmation-footer-selected-devices-count">
                {selectedDevicesIds && selectedDevicesIds.length ? `${selectedDevicesIds.length} Device${selectedDevicesIds.length === 1 ? '' : 's'} Selected` : `Select devices to update firmware`}
              </div>
            </Col>
            <Col span={12}>
              <div className="devices-ota-update-confirmation-footer-confirm-btn-group">
                <Button type="danger" onClick={this.props.onFirmwareUpdateCancel}>Cancel</Button>
                <Button className={"devices-ota-update-confirmation-footer-confirm-btn"}
                        disabled={!selectedDevicesIds.length || this.props.invalid}
                        onClick={this.firmwareUpdateStart}
                        loading={this.props.firmwareUpdate.loading}
                        type="danger">
                  Update firmware
                </Button>
              </div>
            </Col>
          </Row>
        </div>
      </div>
    );
  }

  firmwareProcessing() {

    const {OTAUpdate} = this.props;

    const fields = Object.keys(OTAUpdate.firmwareFields).map((key) => {
      return {
        key  : key,
        value: OTAUpdate.firmwareFields[key],
      };
    });

    return (
      <div className="devices-ota-update-confirmation">
        <div className="devices-ota-update-confirmation-firmware-name">
          { this.props.OTAUpdate.title }
        </div>
        <div className="devices-ota-update-confirmation-file-name">
          { this.props.OTAUpdate.firmwareFileName }
        </div>
        { fields.length ?
          (<div className="devices-ota-update-confirmation-fields-list">
            {fields.map((field, key) => (
              <div className="devices-ota-update-confirmation-fields-list-item" key={key}>
                {field.key}: {field.value}
              </div>
            ))}
          </div>)
          :
          (null)
        }
        <div className="devices-ota-update-confirmation-footer">
          <Row>
            <Col span={12}>
              <div className="devices-ota-update-confirmation-footer-upload-progress">
                <div>
                  <span className={"devices-ota-update-confirmation-footer-upload-progress-done"}>
                    {this.props.devicesUpdated.length}
                </span>
                  &nbsp; of &nbsp;
                  <span className={"devices-ota-update-confirmation-footer-upload-progress-left"}>
                    {this.props.OTAUpdate.selectedDevicesIds.length}
                </span>
                  &nbsp; devices updated
                </div>
              </div>
              <Progress className="devices-ota-update-progress" percent={this.props.updatingProgress} size="small" strokeWidth={4} status="active" showInfo={false}/>
            </Col>
            <Col span={12}>
              <div className="devices-ota-update-confirmation-footer-confirm-btn-group">
                <Button type="danger" onClick={this.props.onModalOpen}>Cancel</Button>
              </div>
            </Col>
          </Row>
        </div>
      </div>
    );
  }

  handleCancelModalOk() {
    this.props.onFirmwareUpdateProcessCancel();
  }

  handleCancelModalCancel() {
    this.props.onModalClose();
  }

  firmwareCancelModalConfirmation() {
    return (
      <Modal
        title="Are you sure you want to cancel ?"
        wrapClassName="vertical-center-modal confirmation-modal-update-cancel"
        visible={this.props.modalVisible}
        confirmLoading={this.props.firmwareUpdate.cancelLoading}
        onOk={this.handleCancelModalOk}
        onCancel={this.handleCancelModalCancel}
        closable={false}
        iconType={"question-circle"}
        cancelText={"Cancel"}
        okText={"Confirm"}
      >
        <div>
          If you continue {Number(this.props.OTAUpdate.selectedDevicesIds.length) - Number(this.props.devicesUpdated.length)} device(s) won't be updated<br/>
          { this.props.devicesUpdated.length > 0 ? `and you won\'t revert firmware for ${this.props.devicesUpdated.length} already updated device(s).` : null}
        </div>
        {/*<div>Type in word CANCEL below to confirm</div>*/}
        {/*<br/>*/}
        {/*<Input placeholder={"Type CANCEL to confirm"}/>*/}
      </Modal>
    );
  }

  firmwareCompleted() {

    const {OTAUpdate} = this.props;

    const fields = Object.keys(OTAUpdate.firmwareFields).map((key) => {
      return {
        key  : key,
        value: OTAUpdate.firmwareFields[key],
      };
    });

    return (
      <div className="devices-ota-update-confirmation">
        <div className="devices-ota-update-confirmation-firmware-name-success">
          { this.props.OTAUpdate.title } update completed
        </div>
        <div className="devices-ota-update-confirmation-log">
          <div className="devices-ota-update-confirmation-log-upload-start">
            Started: { this.props.dateStarted }
          </div>
          <div className="devices-ota-update-confirmation-log-upload-end">
            Completed: { this.props.dateFinished }
          </div>
        </div>
        <div className="devices-ota-update-confirmation-file-name">
          <span className={"devices-ota-update-confirmation-footer-upload-progress-left"}>
                    {this.props.OTAUpdate.selectedDevicesIds.length}
                </span> Devices were successfully updated
        </div>
        <div className="devices-ota-update-confirmation-file-name">
          { this.props.OTAUpdate.firmwareFileName }
        </div>
        { fields.length ?
          (<div className="devices-ota-update-confirmation-fields-list">
            {fields.map((field, key) => (
              <div className="devices-ota-update-confirmation-fields-list-item" key={key}>
                {field.key}: {field.value}
              </div>
            ))}
          </div>)
          :
          (null)
        }
        <div className="devices-ota-update-confirmation-footer">
          <Row>
            <Col span={24}>
              <div className="devices-ota-update-confirmation-footer-confirm-btn-group">
                <Button type="danger" onClick={this.onFirmwareUpdateCancel}>Cancel</Button>
              </div>
            </Col>
          </Row>
        </div>
      </div>
    );
  }

  updateColumns() {
    return [{
      title    : 'Device Name',
      dataIndex: 'name',
      filters       : [{
        text : 'Online',
        value: 'ONLINE',
      }, {
        text : 'Offline',
        value: 'OFFLINE',
      }],
      filterMultiple: false,
      onFilter      : (value, record) => record.status === value,

      render: (text, record) => <DeviceStatus status={record.status} disconnectTime = {record.disconnectTime} text={text}/>
    }, {
      title    : 'Firmware version',
      dataIndex: 'hardwareInfo.version',
    }, {
      title: 'FOTA Status',
      dataIndex: 'deviceOtaInfo.otaStatus',

      render: (text, record) => <OTAStatus status={record && record.deviceOtaInfo && record.deviceOtaInfo.otaStatus || null} disconnectTime={record.disconnectTime} />
    }, {
      title    : 'OTA initiated',
      dataIndex: 'deviceOtaInfo.otaInitiatedBy',
      render: (text, record) => <div>{text} {record.deviceOtaInfo && record.deviceOtaInfo.otaInitiatedAt && getCalendarFormatDate(record.deviceOtaInfo.otaInitiatedAt)}</div>
    }, {
      title    : 'Last Updated',
      dataIndex: 'deviceOtaInfo.finishedAt',
    },];
  }

  handleDeviceSelect(selectedRowKeys) {
    this.props.onDeviceSelect(selectedRowKeys);
  }

  getDataSource() {

    return this.props.devices;
  }

  render() {

    const {step} = this.props;

    const rowSelection = {
      onChange        : (selectedRowKeys, selectedRows) => {
        this.handleDeviceSelect(selectedRowKeys, selectedRows);
      },
      getCheckboxProps: record => ({
        name: record.name,
      }),
      selectedRowKeys: this.props.selectedDevicesIds,
    };
    const dataSource = this.getDataSource();
    const columns = this.updateColumns();
    return (
      <div className="users-profile--organization-settings--organization-users">

        {step === OTA_STEPS.UPLOAD_FIRMWARE ? this.uploadFirmware() : null}

        {step === OTA_STEPS.START_UPDATE ? this.updateConfirmation() : null}

        {step === OTA_STEPS.UPDATING ? this.firmwareProcessing() : null}

        {step === OTA_STEPS.SUCCESS ? this.firmwareCompleted() : null}

        {this.firmwareCancelModalConfirmation()}

        <Table
          loading={this.props.devicesLoading}
          rowKey={(record) => record.id}
          rowSelection={rowSelection} columns={columns} dataSource={dataSource}
          pagination={false}/>
      </div>
    );
  }
}

export default OTA;
