import React from 'react';
import {Table, Button, Row, Col, Upload, Icon, Progress, Input} from 'antd';
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

import DeviceStatus from './components/DeviceStatus';
import OTAStatus from './components/OTAStatus';

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
  };

  constructor(props) {
    super(props);

    this.firmwareUpdateStart = this.firmwareUpdateStart.bind(this);
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
            <FormField name={'firmwareName'} placeholder={'Example: Blynk v1.0.0'}/>
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
                <Button type="danger" onClick={this.firmwareCancelModalConfirmation}>Cancel</Button>
                <Button className={"devices-ota-update-confirmation-footer-confirm-btn"}
                        disabled={!selectedDevicesIds.length}
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
                  17
                </span>
                  &nbsp; of &nbsp;
                  <span className={"devices-ota-update-confirmation-footer-upload-progress-left"}>
                  23
                </span>
                  &nbsp; devices updated
                </div>
              </div>
              <Progress percent={50} size="small" strokeWidth={4} showInfo={false}/>
            </Col>
            <Col span={12}>
              <div className="devices-ota-update-confirmation-footer-confirm-btn-group">
                {/*<Button type="danger" onClick={this.firmwareCancelModalConfirmation}>Cancel</Button>*/}
                <Button type="danger" onClick={this.props.onFirmwareUpdateCancel}>Cancel</Button>
              </div>
            </Col>
          </Row>
        </div>
      </div>
    );
  }

  firmwareCancelModalConfirmation() {
    return (
      <Modal
        title="Are you sure you want to cancel ?"
        wrapClassName="vertical-center-modal confirmation-modal-update-cancel"
        visible={false}
        onOk={() => console.log("ok")}
        onCancel={() => console.log("cancel")}
        closable={false}
        iconType={"question-circle"}
        cancelText={"Cancel"}
        okText={"Confirm"}
      >
        <div>
          If you will continue 19 devices will be not updated.
        </div>
        <div>Type in word CANCEL below to confirm</div>
        <br/>
        <Input placeholder={"Type CANCEL to confirm"}/>
      </Modal>
    );
  }

  firmwareCompleted() {
    return (
      <div className="devices-ota-update-confirmation">
        <div className="devices-ota-update-confirmation-firmware-name-success">
          Firmware Update Title update completed
        </div>
        <div className="devices-ota-update-confirmation-log">
          <div className="devices-ota-update-confirmation-log-upload-start">
            Started 1 may
          </div>
          <div className="devices-ota-update-confirmation-log-upload-end">
            Completed 3 may
          </div>
        </div>
        <div className="devices-ota-update-confirmation-file-name">
          FileName.bin
        </div>
        <div className="devices-ota-update-confirmation-fields-list">
          <div className="devices-ota-update-confirmation-fields-list-item">
            Field 1
          </div>
          <div className="devices-ota-update-confirmation-fields-list-item">
            Field 2
          </div>
          <div className="devices-ota-update-confirmation-fields-list-item">
            Field 3
          </div>
        </div>
        <div className="devices-ota-update-confirmation-footer">
          <Row>
            <Col span={24}>
              <div className="devices-ota-update-confirmation-footer-confirm-btn-group">
                <Button type="danger" onClick={this.firmwareCancelModalConfirmation}>Cancel</Button>
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
      render: (text, record) => <DeviceStatus status={record.status} disconnectTime = {record.disconnectTime} text={text}/>
    }, {
      title    : 'Firmware version',
      dataIndex: 'hardwareInfo.version',
    }, {
      title: 'FOTA Status',
      dataIndex: 'deviceOtaInfo.otaStatus',

      filters       : [{
        text : 'Online',
        value: 'ONLINE',
      }, {
        text : 'Offline',
        value: 'OFFLINE',
      }],
      filterMultiple: false,
      onFilter      : (value, record) => record.status === value,

      render: (text, record) => <OTAStatus status={null} disconnectTime={record.disconnectTime} />
    }, {
      title    : 'OTA initiated by',
      dataIndex: 'deviceOtaInfo.otaInitiatedBy', // just temporary random index
    }, {
      title    : 'Last Updated',
      dataIndex: 'deviceOtaInfo.finishedAt', // just temporary random index
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
