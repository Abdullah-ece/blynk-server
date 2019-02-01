import React from 'react';
import { MainLayout, ContentEditableInput } from 'components';
import {
  Col,
  Row,
  Button,
  Table,
  Input,
  Select,
  message
} from 'antd';
import FormItem from 'components/FormItem';
import ImageUploader from 'components/ImageUploader';

import EditSection from "../../components/EditSection";
import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { SecureTokenForUploadFetch } from "data/Product/api";
import Pluralize from 'pluralize';

import {
  DevicesFetch
} from 'data/Devices/api';
import { OTAGetFirmwareInfo } from "data/Product/actions";

import { FILE_UPLOAD_URL } from 'services/API';

@connect((state) => ({
  orgId: state.Account.selectedOrgId,
  products: state.Product.products,
  devices: state.Devices.devices,
  secureUploadToken: state.Product.secureUploadToken
}), (dispatch) => ({
  secureTokenForUploadFetch: bindActionCreators(SecureTokenForUploadFetch, dispatch),
  fetchDevices: bindActionCreators(DevicesFetch, dispatch),
  getFirmwareInfo: bindActionCreators(OTAGetFirmwareInfo, dispatch),
}))
class Edit extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    this.onChange = this.onChange.bind(this);
    this.onTitleChange = this.onTitleChange.bind(this);
    this.fileUploader = this.fileUploader.bind(this);
    this.fetchToken = this.fetchToken.bind(this);
    this.firmwareUpdateStart = this.firmwareUpdateStart.bind(this);
    this.onFirmwareUpdateCancel = this.onFirmwareUpdateCancel.bind(this);
    this.getSelectedDevicesTitle = this.getSelectedDevicesTitle.bind(this);

    this.fetchToken();

    const ota = this.props.OTA || {};

    this.state = {
      selectedRowKeys: [],
      OTA: {
        orgId: this.props.orgId,
        productId: undefined,
        pathToFirmware: '',
        firmwareOriginalFileName: '',
        deviceIds: [],
        title: ota.title || 'New Shipping',
        checkBoardType: false,
        firmwareInfo: {
          version: '',
          boardType: '',
          buildDate: '',
          md5Hash: '',
        },
        attemptsLimit: 0,
        isSecure: false
      }
    };
  }

  firmwareUpdateStart() {
    this.context.router.push('/ota');
  }

  onFirmwareUpdateCancel() {
    this.context.router.push('/ota');
  }

  componentWillMount() {
    this.fetchToken();

    this.props.fetchDevices({
      orgId: this.props.orgId
    });
  }

  fetchToken() {
    this.props.secureTokenForUploadFetch();
  }

  onTitleChange(value) {
    const { OTA } = this.state;
    OTA.title = value;
    this.setState({ OTA });
  }

  onChange(target) {
    const { OTA } = this.state;
    OTA[target.name] = target.value;
    this.setState({ OTA });
  }

  fileUploader(onChange, value, error) {
    const fileProps = {
      name: 'file',
      action: FILE_UPLOAD_URL,
      showUploadList: false,
      accept: '.bin',
      data: {
        token: this.props.secureUploadToken
      }
    };

    const handleComponentChange = (info) => {
      const status = info.file.status;
      if (status === 'done') {
        this.fetchToken();
        onChange({ value: info.file.response, name: 'pathToFirmware' });
        this.props.getFirmwareInfo({ path_to_firmware: info.file.response }).then(
          result => {
            const { OTA } = this.state;
            OTA.firmwareInfo = result.payload.data;
            this.setState({ OTA });
          });
      } else if (status === 'error') {
        this.fetchToken();
        message.error(`${info.file.name} file upload failed.`);
      }
    };

    return (
      <ImageUploader text={() => (
        <span>Upload firmware file (.bin)<br/><br/></span>)}
                     logo={value}
                     error={error}
                     iconClass={'ota-upload-drag-icon'}
                     onChange={handleComponentChange}
                     fileProps={fileProps}/>
    );
  }

  getSelectedDevicesTitle() {
    const { deviceIds } = this.state.OTA;
    let firstpart = '';
    if (!deviceIds.length) {
      firstpart = 'No devices';
    } else {
      firstpart = Pluralize('device', deviceIds.length, true);
    }

    return firstpart + ' selected';
  }

  createTable() {
    const { productId } = this.state.OTA;
    const devices = productId ? this.props.devices.filter((device) => device.productId === productId) : this.props.devices;
    const columns = [{
      title: 'Device Name',
      dataIndex: 'name',
    }, {
      title: 'Product Name',
      dataIndex: 'productName',
    },];
    const selectedRowKeys = [];

    if (this.state.OTA.deviceIds.length) {
      for (let i = 0; i < devices.length; i++) {
        if (this.state.OTA.deviceIds.indexOf(devices[i].id) >= 0) {
          selectedRowKeys.push(i);
        }
      }
    }

    const rowSelection = {
      selectedRowKeys,
      onChange: (selectedRowKeys, selectedRows) => {
        const { OTA } = this.state;
        OTA.deviceIds = selectedRows.map(device => device.id);
        this.setState({ OTA });
      },
      getCheckboxProps: record => ({
        name: record.name,
      }),
    };


    return (
      <Table rowSelection={rowSelection} columns={columns}
             dataSource={devices} pagination={false}/>);
  }

  render() {
    const { OTA } = this.state;
    const products = this.props.products || [{ id: 1, name: 'blaj' }];

    let productsList = products.map((product) => ({
      key: product.id,
      value: product.name,
    }));

    return (
      <MainLayout>
        <MainLayout.Header
          title={<ContentEditableInput maxLength={40}
                                       value={OTA.title}
                                       onChange={this.onTitleChange}/>
          }>
        </MainLayout.Header>

        <MainLayout.Content className="organizations-create-content">
          <EditSection>
            <Row gutter={24}>
              <Col span={7}>
                <div className="edit-section-sub-title ">
                  Target selection
                </div>
                <div className="product-details-row">
                  <FormItem>
                    <FormItem.Title>Product</FormItem.Title>
                    <FormItem.Content>
                      <Select className="edit-section-content-input"
                              value={OTA.productId}
                              disabled={true}
                              onChange={value => this.onChange({
                                value,
                                name: 'productId'
                              })}
                              placeholder={`Select product to filter devices`}>
                        {productsList.map((product) => (
                          <Select.Option key={`${product.key}`}
                                         value={`${product.key}`}>{product.value}</Select.Option>
                        ))}
                      </Select>
                    </FormItem.Content>
                  </FormItem>
                </div>
                <div className="product-details-row">
                  <FormItem className='edit-section-content'>
                    <FormItem.Title>Product</FormItem.Title>
                    <FormItem.Content>
                      <Input value={''}
                             onChange={this.onChange}
                             name={''}
                             placeholder={'Search devices'}
                             disabled={true}/>
                    </FormItem.Content>
                  </FormItem>
                </div>
              </Col>
              <Col span={17}>
                <div className="edit-section-sub-title ">
                  {this.getSelectedDevicesTitle()}
                </div>
                <div className="product-details-row">
                  {this.createTable()}
                </div>
              </Col>
            </Row>
          </EditSection>
          <EditSection title={'Firmware'}>
            <Row>
              <Col span={7}>
                <div className='upload-firmware'>
                  {this.fileUploader(this.onChange, OTA.pathToFirmware)}
                </div>
              </Col>
              <Col span={17}>
                {OTA.firmwareInfo.buildDate && (<div>
                  <div>Build: {OTA.firmwareInfo.buildDate}</div>
                  <div>Version: {OTA.firmwareInfo.version}</div>
                  <div>Hardware: {OTA.firmwareInfo.boardType}</div>
                  <div>MD5 Checksum: {OTA.firmwareInfo.md5Hash}</div>
                </div>)}
              </Col>
            </Row>
          </EditSection>
          <EditSection title={'Review and start'}>
            <div>Select devices</div>
            <div>Upload Firmware file</div>
            <div className="ota-btn-group">
              <Button type="danger"
                      onClick={this.onFirmwareUpdateCancel}>Cancel</Button>
              <Button
                disabled={!OTA.deviceIds.length || !OTA.pathToFirmware}
                onClick={this.firmwareUpdateStart}
                type="primary">
                Start Shipping
              </Button>
            </div>
          </EditSection>
        </MainLayout.Content>
      </MainLayout>
    );
  }

}

export default Edit;
