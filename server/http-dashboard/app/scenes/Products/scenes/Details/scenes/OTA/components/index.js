import React from 'react';
import {Table, Button, Row, Col, Upload, Icon, Progress} from 'antd';
import Modal from 'components/Modal';
import PropTypes from 'prop-types';
import {reduxForm, Fields} from 'redux-form';
import {amountBasedWord} from 'services/Spelling';
import {
  Item
} from "components/UI";
import {
  Field as FormField
} from 'components/Form';

import {OTA_STATUSES, OTA_STEPS} from 'services/Products';

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
      pathToFirmware: PropTypes.string,
      firmwareOriginalFileName: PropTypes.string,
      startedAt: PropTypes.number,
      deviceIds: PropTypes.arrayOf(PropTypes.number),
      firmwareInfo: PropTypes.any,
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
    onProductDeleteProcess: PropTypes.func,
    onModalClose: PropTypes.func,
    onModalOpen: PropTypes.func,

    dateStarted: PropTypes.any,
    dateFinished: PropTypes.any,

    modalVisible: PropTypes.bool,
    invalid: PropTypes.bool,
  };

  constructor(props) {
    super(props);

    this.devicesTable = this.devicesTable.bind(this);
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

    const fields = Object.keys(OTAUpdate.firmwareInfo).map((key) => {
      return {
        key  : key,
        value: OTAUpdate.firmwareInfo[key],
      };
    });

    return (
      <div className="devices-ota-update-confirmation">
        <div className="devices-ota-update-confirmation-firmware-name">
          { this.props.OTAUpdate.title }
        </div>
        <div className="devices-ota-update-confirmation-file-name">
          { this.props.OTAUpdate.firmwareOriginalFileName }
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
                    {this.props.OTAUpdate.deviceIds.length}
                </span>
                  &nbsp; {amountBasedWord(this.props.OTAUpdate.deviceIds.length, 'device', 'devices')} updated
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

    let notUpdatedDevices = Number(this.props.OTAUpdate && this.props.OTAUpdate.deviceIds && this.props.OTAUpdate.deviceIds.length) - Number(this.props.devicesUpdated.length);

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
          If you continue {amountBasedWord(notUpdatedDevices, 'device', 'devices')} won't be updated<br/>
          { this.props.devicesUpdated.length > 0 ? `and you won\'t revert firmware for ${this.props.devicesUpdated.length} already updated ${amountBasedWord(this.props.devicesUpdated.length, 'device', 'devices')}.` : null}
        </div>
        {/*<div>Type in word CANCEL below to confirm</div>*/}
        {/*<br/>*/}
        {/*<Input placeholder={"Type CANCEL to confirm"}/>*/}
      </Modal>
    );
  }

  firmwareCompleted() {

    const {OTAUpdate} = this.props;

    const fields = Object.keys(OTAUpdate.firmwareInfo).map((key) => {
      return {
        key  : key,
        value: OTAUpdate.firmwareInfo[key],
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
                    {this.props.OTAUpdate.deviceIds.length}
                </span> {amountBasedWord(this.props.OTAUpdate.deviceIds.length, 'Device was', 'Devices were')} successfully updated
        </div>
        <div className="devices-ota-update-confirmation-file-name">
          { this.props.OTAUpdate.firmwareOriginalFileName }
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
                <Button type="danger" onClick={this.props.onProductDeleteProcess}>Cancel</Button>
              </div>
            </Col>
          </Row>
        </div>
      </div>
    );
  }

  updateColumns(values) {
    return [{
      title    : 'Device Name',
      dataIndex: 'name',
      filters       : [{
        text : 'Pending',
        value: 'PENDING',
      }, {
        text : 'Success',
        value: 'SUCCESS',
      }, {
        text : 'Failure',
        value: 'FAILURE',
      }, {
        text : 'Never Updated',
        value: 'NEVER_UPDATED',
      }],
      filterMultiple: false,
      onFilter      : (value, record) => {
        if(value === 'PENDING') {
          return [OTA_STATUSES.STARTED, OTA_STATUSES.REQUEST_SENT, OTA_STATUSES.FIRMWARE_UPLOADED].indexOf(record && record.deviceOtaInfo && record.deviceOtaInfo.otaStatus || null) >= 0;
        }

        if(value === 'SUCCESS') {
          return OTA_STATUSES.SUCCESS === record && record.deviceOtaInfo && record.deviceOtaInfo.otaStatus || false;
        }

        if(value === 'FAILURE') {
          return OTA_STATUSES.FAILURE === record && record.deviceOtaInfo && record.deviceOtaInfo.otaStatus || false;
        }

        if(value === 'NEVER_UPDATED') {
          return !record || !record.deviceOtaInfo || !record.deviceOtaInfo.otaStatus;
        }
      },

      sorter: (a, b) => a.name > b.name ? -1 : 1,
      sortOrder: values.sort.columnKey === 'name' && values.sort.order,

      render: (text, record) => <DeviceStatus status={record.status} disconnectTime = {record.disconnectTime} text={text}/>
    }, {
      title    : 'Firmware version',
      dataIndex: 'hardwareInfo.version',
      render: (text) => <div>{text || "-"}</div>,
      sorter: (a, b) => {
        if(a.hardwareInfo && b.hardwareInfo) {
          return a.hardwareInfo.version > b.hardwareInfo.version ? -1 : 1;
        }

        return -1;
      },
      sortOrder: values.sort.columnKey === 'hardwareInfo.version' && values.sort.order,

    }, {
      title: 'FOTA Status',
      dataIndex: 'deviceOtaInfo.otaStatus',

      render: (text, record) => <OTAStatus deviceOtaInfo={record && record.deviceOtaInfo} status={record && record.deviceOtaInfo && record.deviceOtaInfo.otaStatus || null} disconnectTime={record.disconnectTime} />
    }, {
      title    : 'OTA initiated',
      dataIndex: 'deviceOtaInfo.otaInitiatedBy',
      render: (text, record) => <div>{text || "-"} {record.deviceOtaInfo && record.deviceOtaInfo.otaInitiatedAt && getCalendarFormatDate(record.deviceOtaInfo.otaInitiatedAt)}</div>
    }, {
      title    : 'Last Updated',
      dataIndex: 'deviceOtaInfo.finishedAt',
      render: (value) => <div>{(!value || value < 0 ? "-" : getCalendarFormatDate(value)) || "-"}</div>
    },];
  }

  handleDeviceSelect(selectedRowKeys) {
    this.props.onDeviceSelect(selectedRowKeys);
  }

  getDataSource() {

    return this.props.devices;
  }

  devicesTable(props) {

    const onChange = (pagination, filters, sorter) => {
      props.sort.input.onChange(sorter);
    };

    const values = {
      filter: props.filter.input.value,
      sort: props.sort.input.value,
    };

    const columns = this.updateColumns(values);

    return (
      <Table
        className="ota-table"
        loading={this.props.devicesLoading}
        rowKey={(record) => record.id}
        rowSelection={props.rowSelection} columns={columns} dataSource={props.dataSource}
        onChange={onChange}
        pagination={false}/>
    );
  }

  render() {
    const {step} = this.props;

    const dataSource = this.getDataSource();

    const rowSelection = {
      onChange        : (selectedRowKeys, selectedRows) => {
        this.handleDeviceSelect(selectedRowKeys, selectedRows);
      },

      getCheckboxProps: record => ({
        name: record.name,
      }),

      selectedRowKeys: this.props.selectedDevicesIds,
    };
    return (
      <div className="users-profile--organization-settings--organization-users">

        {step === OTA_STEPS.UPLOAD_FIRMWARE ? this.uploadFirmware() : null}

        {step === OTA_STEPS.START_UPDATE ? this.updateConfirmation() : null}

        {step === OTA_STEPS.UPDATING ? this.firmwareProcessing() : null}

        {step === OTA_STEPS.SUCCESS ? this.firmwareCompleted() : null}

        {this.firmwareCancelModalConfirmation()}

        <Fields names={['sort', 'filter']}
                component={this.devicesTable}
                dataSource={dataSource}
                rowSelection={rowSelection}
                rerenderOnEveryChange={true}
        />
      </div>
    );
  }
}

export default OTA;
