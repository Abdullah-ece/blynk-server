import React from 'react';
import {Row, Col, Input, Select, Icon} from 'antd';
import FormItem from 'components/FormItem';
import './styles.less';
import Preview from './components/Preview';

class MetadataItem extends React.Component {
  render() {
    return (
      <div className="product-metadata-item">
        <Row gutter={8}>
          <Col span={12}>
            <FormItem offset={false}>
              <FormItem.TitleGroup>
                <FormItem.Title style={{width: '50%'}}>String</FormItem.Title>
                <FormItem.Title style={{width: '50%'}}>Value</FormItem.Title>
              </FormItem.TitleGroup>
              <FormItem.Content>
                <Input.Group compact>
                  <Input placeholder="Key" style={{width: '50%'}}/>
                  <Input placeholder="Value" style={{width: '50%'}}/>
                </Input.Group>
              </FormItem.Content>
            </FormItem>
          </Col>
          <Col span={4}>
            <FormItem offset={false}>
              <FormItem.Title>Who can edit</FormItem.Title>
              <FormItem.Content>
                <Select defaultValue="Admin" style={{width: 120}}>
                  <Select.Option value="Admin">Admin</Select.Option>
                  <Select.Option value="User">User</Select.Option>
                </Select>
              </FormItem.Content>
            </FormItem>
          </Col>
          <Col span={7} offset={1}>
            <Preview>
              <Preview.Name>Temperature:</Preview.Name>
              <Preview.Value>20˚</Preview.Value>
            </Preview>
          </Col>
        </Row>
        <div className="product-metadata-item-tools">
          <Icon type="bars"/>
          <Icon type="delete"/>
          <Icon type="copy"/>
        </div>
      </div>
    );
  }
}

export default MetadataItem;
