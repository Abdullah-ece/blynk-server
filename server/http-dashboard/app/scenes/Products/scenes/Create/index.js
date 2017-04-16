import React from 'react';
import './styles.less';
import {Button, Tabs, Input, Upload, Icon, message, Col, Row, Select, Popover} from 'antd';
import {HARDWARE_DEFAULT, HARDWARES, CONNECTIONS_TYPES, CONNECTIONS_TYPES_DEFAULT} from 'services/Devices';
import {SelectSimpleMatch} from 'services/Filters';
import FormItem from 'components/FormItem';
import MetadataIntroductionMessage from "./components/MetadataIntroductionMessage/index";
import AddNewMetadataField from "./components/AddNewMetadataField/index";

class ProductCreate extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      activeTab: 0,
      isMetadataInfoRead: false,
      metadataIntroVisible: null
    };
  }

  InfoFileProps = {
    name: 'file',
    multiple: true,
    showUploadList: false,
    action: '//somepoint',
    onChange: this.handleInfoFileChenge
  };

  handleInfoFileChenge(info) {
    const status = info.file.status;
    if (status !== 'uploading') {
      message.info(`status ${info.file.name}`);
    }
    if (status === 'done') {
      message.success(`${info.file.name} file uploaded successfully.`);
    } else if (status === 'error') {
      message.error(`${info.file.name} file upload failed.`);
    }
  }

  getHardwareOptions() {
    let options = [];
    HARDWARES.forEach((item) => {
      options.push(
        <Select.Option key={item}>{item}</Select.Option>
      );
    });
    return options;
  }

  getConnectionOptions() {
    let options = [];
    CONNECTIONS_TYPES.forEach((item) => {
      options.push(
        <Select.Option key={item}>{item}</Select.Option>
      );
    });
    return options;
  }

  handleTabChange(key) {
    this.setState({
      activeTab: Number(key)
    });
  }

  isMetadataIntroductionMessageVisible() {
    if (this.state.metadataIntroVisible === null) return !this.state.isMetadataInfoRead;

    return !!this.state.metadataIntroVisible;
  }

  toggleMetadataIntroductionMessage() {
    this.setState({
      metadataIntroVisible: !this.state.metadataIntroVisible,
      isMetadataInfoRead: true
    });
  }

  render() {

    const hardwareOptions = this.getHardwareOptions();
    const connectionsOptions = this.getConnectionOptions();

    return (
      <div className="products-create">
        <div className="products-header">
          <div className="products-header-name">New Product</div>
          <div className="products-header-options">
            <Button type="default">Cancel</Button>
            <Button type="primary">Save</Button>
          </div>
        </div>
        <div className="products-content" style={{position: 'relative'}}>
          { this.state.activeTab === 2 && <Popover
            placement="bottomRight"
            content={<MetadataIntroductionMessage onGotItClick={this.toggleMetadataIntroductionMessage.bind(this)}/>}
            visible={this.isMetadataIntroductionMessageVisible()}
            overlayClassName="products-metadata-introduction-message-popover"
            trigger="click">

            <Icon type="info-circle" className="products-metadata-info"
                  onClick={this.toggleMetadataIntroductionMessage.bind(this)}/>
          </Popover>}
          <Tabs defaultActiveKey="1" onChange={this.handleTabChange.bind(this)}>
            <Tabs.TabPane tab="Info" key="1">

              <Row gutter={24} className="products-create-tabs-inner-content">
                <Col span={15}>
                  <FormItem>
                    <FormItem.Title>Name</FormItem.Title>
                    <FormItem.Content>
                      <Input/>
                    </FormItem.Content>
                  </FormItem>

                  <FormItem>
                    <FormItem.TitleGroup>
                      <FormItem.Title style={{width: '50%'}}>hardware</FormItem.Title>
                      <FormItem.Title style={{width: '50%'}}>connection type</FormItem.Title>
                    </FormItem.TitleGroup>
                    <FormItem.Content>
                      <Input.Group compact>
                        <Select showSearch
                                defaultValue={HARDWARE_DEFAULT}
                                style={{width: '50%'}}
                                filterOption={SelectSimpleMatch}>
                          {hardwareOptions}
                        </Select>
                        <Select showSearch
                                style={{width: '50%'}}
                                defaultValue={CONNECTIONS_TYPES_DEFAULT}
                                filterOption={SelectSimpleMatch}>
                          {connectionsOptions}
                        </Select>
                      </Input.Group>
                    </FormItem.Content>
                  </FormItem>

                  <FormItem>
                    <FormItem.Title>description</FormItem.Title>
                    <FormItem.Content>
                      <Input type="textarea" rows="4"/>
                    </FormItem.Content>
                  </FormItem>
                </Col>
                <Col span={9}>
                  <div className="products-create-drag-and-drop">
                    <Upload.Dragger {...this.InfoFileProps}>
                      <p className="ant-upload-drag-icon">
                        <Icon type="cloud-upload-o"/>
                      </p>
                      <p className="ant-upload-text">Add image</p>
                      <p className="ant-upload-hint">
                        Upload from computer or drag-n-drop<br/>
                        .png or .jpg, min 500x500px
                      </p>
                    </Upload.Dragger>
                  </div>
                </Col>
              </Row>


            </Tabs.TabPane>
            <Tabs.TabPane tab="Metadata" key="2">
              <AddNewMetadataField/>
            </Tabs.TabPane>
            <Tabs.TabPane tab="Data Streams" key="3">Content of Data Streams</Tabs.TabPane>
            <Tabs.TabPane tab="Events" key="4">Content of Events</Tabs.TabPane>
          </Tabs>
        </div>
      </div>
    );
  }
}

export default ProductCreate;
