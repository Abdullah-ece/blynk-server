import React from 'react';
import {Table, Button, Row, Col, Upload, Icon, Progress} from 'antd';
import PropTypes from 'prop-types';
import {reduxForm} from 'redux-form';
import {
  Item
} from "components/UI";
import {
  Field as FormField
} from 'components/Form';

import './styles.less';

import DeviceStatus from './components/DeviceStatus';

@reduxForm({
  form: 'OTA'
})
class OTA extends React.Component{

  static propTypes = {
    devices: PropTypes.arrayOf(PropTypes.shape({
      id: PropTypes.number,
      name: PropTypes.string,
      status: PropTypes.oneOf(['ONLINE', 'OFFLINE']), // use this for column "status" and display like a green / gray dot
      disconnectTime: PropTypes.number, // display "Was online N days ago" when user do mouseover the gray dot (idea is to display last time when device was online if it's offline right now)
      hardwareInfo: PropTypes.shape({
        version: PropTypes.string
      })
    }))
  };

  uploadFirmware() {
    return (
      <div className="devices-ota-update-upload">
        <h4>Firmware Update:</h4>
        <Upload>
          <Button type="dashed"><Icon type="plus" />Upload new firmware</Button>
        </Upload>
      </div>
    );
  }

  updateConfirmation(selectedUsers = false) {
    return (
      <div className="devices-ota-update-confirmation">
        <div className="devices-ota-update-confirmation-name">
          <Item label="Firmware Name" offset="medium">
            <FormField name={'firmwareName'} placeholder={'Example: Blynk v1.0.0'}/>
          </Item>
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
            <Col span={12}>
              <div className="devices-ota-update-confirmation-footer-selected-devices-count">
                Select devices to update firmware
              </div>
            </Col>
            <Col span={12}>
              <div className="devices-ota-update-confirmation-footer-confirm-btn-group">
                <Button type="danger">Cancel</Button>
                <Button className={"devices-ota-update-confirmation-footer-confirm-btn"} disabled={!selectedUsers} type="danger">Update firmware</Button>
              </div>
            </Col>
          </Row>
        </div>
      </div>
    );
  }

  firmwareProcessing() {
    return (
      <div className="devices-ota-update-confirmation">
        <div className="devices-ota-update-confirmation-firmware-name">
          Firmware Update Title
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
              <Progress percent={50} size="small"  strokeWidth={4}  showInfo={false}/>
            </Col>
            <Col span={12}>
              <div className="devices-ota-update-confirmation-footer-confirm-btn-group">
                <Button type="danger">Cancel</Button>
              </div>
            </Col>
          </Row>
        </div>
      </div>
    );
  }

  firmwareCancelModalConfirmation() {
    return (
      null
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
                <Button type="danger">Cancel</Button>
              </div>
            </Col>
          </Row>
        </div>
      </div>
    );
  }

  updateColumns() {
    return [{
      title: 'Name',
      dataIndex: 'name',
    }, {
      title: 'Status',
      dataIndex: 'status',

      filters: [{
        text: 'Online',
        value: 'ONLINE',
      }, {
        text: 'Offline',
        value: 'OFFLINE',
      }],
      filterMultiple: false,
      onFilter: (value, record) => record.status === value,

      render: (text, record) => <DeviceStatus status={record.status} disconnectTime = {record.disconnectTime}/>
    }, {
      title: 'Firmware version',
      dataIndex: 'hardwareInfo.version',
    }];
  }
  getDataSource(){

    return this.props.devices;
  }
  render(){

    const rowSelection = {
      onChange: () => {

      },
      getCheckboxProps: record => ({
        name: record.name,
      }),
    };
    const dataSource = this.getDataSource();
    const columns = this.updateColumns();
    return (
      <div className="users-profile--organization-settings--organization-users">

        { this.uploadFirmware() }

        { this.updateConfirmation() }

        { this.updateConfirmation(true) }

        { this.firmwareProcessing() }

        { this.firmwareCompleted() }

        { this.firmwareCancelModalConfirmation() }

        <Table
          rowKey={(record) => record.name}
          rowSelection={rowSelection} columns={columns} dataSource={dataSource}
               pagination={false}/>
      </div>
    );
  }
}

export default OTA;
