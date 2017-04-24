import React from 'react';
import FormItem from 'components/FormItem';
import {Input, Upload, Icon, Col, Row, message} from 'antd';
import {HARDWARES, CONNECTIONS_TYPES} from 'services/Devices';
import {Field, Select} from 'components/Form';
import {reduxForm, Field as FormField} from 'redux-form';

@reduxForm({
  form: 'product-creation-info',
  initialValues: {
    hardware: HARDWARES['Particle Electron'].key,
    connectionType: CONNECTIONS_TYPES.GSM.key
  }
})
class Info extends React.Component {

  hardware = [
    HARDWARES['Arduino 101'],
    HARDWARES['Arduino Due'],
    HARDWARES['Arduino Leonardo'],
    HARDWARES['Arduino Mega'],
    HARDWARES['Arduino Micro'],
    HARDWARES['Arduino Mini'],
    HARDWARES['Arduino MKR1000'],
    HARDWARES['Arduino Nano'],
    HARDWARES['Arduino Pro Micro'],
    HARDWARES['Arduino Pro Mini'],
    HARDWARES['Arduino UNO'],
    HARDWARES['Arduino Yun'],
    HARDWARES['Arduino Zero'],
    HARDWARES['ESP8266'],
    HARDWARES['Generic Board'],
    HARDWARES['Intel Edison'],
    HARDWARES['Intel Galileo'],
    HARDWARES['LinkIt ONE'],
    HARDWARES['Microduino Core+'],
    HARDWARES['Microduino Core'],
    HARDWARES['Microduino CoreRF'],
    HARDWARES['Microduino CoreUSB'],
    HARDWARES['NodeMCU'],
    HARDWARES['Particle Core'],
    HARDWARES['Particle Electron'],
    HARDWARES['Particle Photon'],
    HARDWARES['Raspberry Pi 3 B'],
    HARDWARES['Raspberry Pi 2/A+/B+'],
    HARDWARES['Raspberry Pi B (Rev1)'],
    HARDWARES['Raspberry Pi A/B (Rev2)'],
    HARDWARES['RedBearLab CC3200/Mini'],
    HARDWARES['Seeed Wio Link'],
    HARDWARES['SparkFun Blynk Board'],
    HARDWARES['SparkFun ESP8266 Thing'],
    HARDWARES['SparkFun Photon RedBoard'],
    HARDWARES['TI CC3200-LaunchXL'],
    HARDWARES['TI Tiva C Connected'],
    HARDWARES['TinyDuino'],
    HARDWARES['WeMos D1'],
    HARDWARES['WeMos D1 mini'],
    HARDWARES['Wildfire v2'],
    HARDWARES['Wildfire v3'],
    HARDWARES['Wildfire v4'],
    HARDWARES['WiPy']
  ];

  connectionTypes = [
    CONNECTIONS_TYPES.ETHERNET,
    CONNECTIONS_TYPES.WIFI,
    CONNECTIONS_TYPES.USB,
    CONNECTIONS_TYPES.BLUETOOTH,
    CONNECTIONS_TYPES.BLE,
    CONNECTIONS_TYPES.GSM,
  ];

  handleInfoFileChange(valueChanger, info) {
    const status = info.file.status;
    if (status !== 'uploading') {
      message.info(`status ${info.file.name}`);
    }
    if (status === 'done') {
      message.success(`${info.file.name} file uploaded successfully.`);
      valueChanger(info.file.response);
    } else if (status === 'error') {
      message.error(`${info.file.name} file upload failed.`);
    }
  }

  InfoFileProps = {
    name: 'file',
    action: '/dashboard/upload',
    showUploadList: false,
    accept: 'image/*'
  };

  render() {

    return (

      <Row gutter={24} className="products-create-tabs-inner-content">
        <Col span={15}>
          <FormItem>
            <FormItem.Title>Name</FormItem.Title>
            <FormItem.Content>
              <Field name="name" placeholder="Name"/>
            </FormItem.Content>
          </FormItem>

          <FormItem>
            <FormItem.TitleGroup>
              <FormItem.Title style={{width: '50%'}}>hardware</FormItem.Title>
              <FormItem.Title style={{width: '50%'}}>connection type</FormItem.Title>
            </FormItem.TitleGroup>
            <FormItem.Content>
              <Input.Group compact>
                <Select name="hardware" values={this.hardware}/>
                <Select name="connectionType" values={this.connectionTypes}/>
              </Input.Group>
            </FormItem.Content>
          </FormItem>

          <FormItem>
            <FormItem.Title>description</FormItem.Title>
            <FormItem.Content>
              <Field name="description" type="textarea" placeholder="Description (optional)"/>
            </FormItem.Content>
          </FormItem>
        </Col>
        <Col span={9}>
          <div className="products-create-drag-and-drop">
            <FormField name="file" component={({input}) => (
              <Upload.Dragger {...this.InfoFileProps} onChange={this.handleInfoFileChange.bind(this, input.onChange)}>
                <p className="ant-upload-drag-icon">
                  <Icon type="cloud-upload-o"/>
                </p>
                <p className="ant-upload-text">Add image</p>
                <p className="ant-upload-hint">
                  Upload from computer or drag-n-drop<br/>
                  .png or .jpg, min 500x500px
                </p>
              </Upload.Dragger>
            )}/>
          </div>
        </Col>
      </Row>
    );
  }
}

export default Info;
