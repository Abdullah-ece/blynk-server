import React from 'react';
import {Row, Col, Select, Icon, Popconfirm, Button} from 'antd';
import FormItem from 'components/FormItem';
import Preview from '../../components/Preview';
import {SortableHandle} from 'react-sortable-hoc';

const DragHandler = SortableHandle(() => <Icon type="bars" className="cursor-move"/>);

class MetadataItem extends React.Component {

  static propTypes = {
    preview: React.PropTypes.object,
    children: React.PropTypes.any,
    onDelete: React.PropTypes.func,
    onClone: React.PropTypes.func,
    touched: React.PropTypes.bool
  };

  handleDelete() {
    if (this.props.onDelete)
      this.props.onDelete();
  }

  preview() {

    if (!this.props.preview.isTouched && !this.props.preview.values.name) {
      return null;
    }

    if (this.props.preview.invalid) {
      return (<Preview> <Preview.Unavailable /> </Preview>);
    }

    return (
      <Preview>
        <Preview.Name>{this.props.preview.values.name}</Preview.Name>
        <Preview.Value>{this.props.preview.values.value || 'Empty'}</Preview.Value>
      </Preview>
    );

  }

  render() {

    let deleteButton;
    if (this.props.touched) {
      deleteButton = (<Popconfirm title="Are you sure you want to delete this task?" overlayClassName="danger"
                                  onConfirm={this.handleDelete.bind(this)} okText="Yes" cancelText="No">
        <Button icon="delete" size="small" onClick={this.handleDelete.bind(this)}/>
      </Popconfirm>);
    } else {
      deleteButton = (<Button size="small" icon="delete" onClick={this.handleDelete.bind(this)}/>);
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
          <Col span={6} offset={1}>
            { this.preview() }
          </Col>
        </Row>
        <div className="product-metadata-item-tools">
          <DragHandler/>
          {deleteButton}
          <Button icon="copy" size="small" onClick={this.props.onClone.bind(this)}/>
        </div>
      </div>
    );
  }
}

export default MetadataItem;
