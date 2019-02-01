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

@connect((state) => ({
  secureUploadToken: state.Product.secureUploadToken
}), (dispatch) => ({
  secureTokenForUploadFetch: bindActionCreators(SecureTokenForUploadFetch, dispatch),
}))
class Edit extends React.Component {
  constructor(props) {
    super(props);

    this.onChange = this.onChange.bind(this);
    this.onTitleChange = this.onTitleChange.bind(this);
    this.fileUploader = this.fileUploader.bind(this);
    this.fetchToken = this.fetchToken.bind(this);
    this.firmwareUpdateStart = this.firmwareUpdateStart.bind(this);
    this.onFirmwareUpdateCancel = this.onFirmwareUpdateCancel.bind(this);

    this.fetchToken();

    const ota = this.props.OTA || {};

    this.state = {
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

  }

  onFirmwareUpdateCancel() {

  }

  componentWillMount() {
    this.fetchToken();
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
      data: {
        token: this.props.secureUploadToken
      }
    };

    const handleComponentChange = (info) => {
      const status = info.file.status;
      if (status === 'done') {
        this.fetchToken();
        onChange({ value: info.file.response, name: 'pathToFirmware' });
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
                     fileProps={fileProps}
                     iconClass={'ota-upload-drag-icon'}
                     onChange={handleComponentChange}/>
    );
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
                  No devices selected
                </div>
                <Table/>
              </Col>
            </Row>
          </EditSection>
          <EditSection title={'Firmware'}>
            <div className='upload-firmware'>
              {this.fileUploader(this.onChange, OTA.pathToFirmware)}
            </div>
          </EditSection>
          <EditSection title={'Review and start'}>
            <div>Select devices</div>
            <div>Upload Firmware file</div>
            <div className="ota-btn-group">
              <Button type="danger"
                      onClick={this.onFirmwareUpdateCancel}>Cancel</Button>
              <Button
                disabled={!OTA.deviceIds.length}
                onClick={this.firmwareUpdateStart}
                type="primary">
                Update firmware
              </Button>
            </div>
          </EditSection>
        </MainLayout.Content>
      </MainLayout>
    )
      ;
  }

}

export default Edit;
