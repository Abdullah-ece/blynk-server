import React from 'react';
import {Row, Col, Select, Icon} from 'antd';
import FormItem from 'components/FormItem';
import Preview from '../../components/Preview';

class MetadataItem extends React.Component {

  static propTypes = {
    preview: React.PropTypes.object,
    children: React.PropTypes.any,
    onDelete: React.PropTypes.func
  };

  handleDelete() {
    if (this.props.onDelete)
      this.props.onDelete();
  }

  render() {

    let preview;

    if (this.props.preview.isTouched && this.props.preview.values.name) {
      preview = (<Preview>
        <Preview.Name>{this.props.preview.values.name}</Preview.Name>
        <Preview.Value>{this.props.preview.values.value || 'Empty'}</Preview.Value>
      </Preview>);
    } else if (!this.props.preview.isTouched) {
      preview = '';
    } else {
      preview = <Preview> <Preview.Unavailable /> </Preview>;
    }

    return (
      <div className="product-metadata-item">
        <Row gutter={8}>
          <Col span={12}>
            { this.props.children }
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
            { preview }
          </Col>
        </Row>
        <div className="product-metadata-item-tools">
          <Icon type="bars"/>
          <Icon type="delete" onClick={this.handleDelete.bind(this)}/>
          <Icon type="copy"/>
        </div>
      </div>
    );
  }
}

export default MetadataItem;
