import React from 'react';
import {Row, Col} from 'antd';
import FormItem from 'components/FormItem';
import Preview from '../../components/Preview';
import classnames from 'classnames';

class MetadataItemStatic extends React.Component {

  constructor(props) {
    super(props);
  }

  preview() {

    return (
      <Preview>
        <Preview.Name>Name:</Preview.Name>
        <Preview.Value>Empty</Preview.Value>
      </Preview>
    );

  }

  render() {

    const itemClasses = classnames({
      'product-metadata-item': true,
      'product-metadata-item-static': true
    });

    return (
      <div className={itemClasses}>
        <Row gutter={4}>
          <Col span={12}>
            { this.props.children }
          </Col>
          <Col span={4}>
            <FormItem offset={false}>
              <FormItem.Title>Who can edit</FormItem.Title>
              <FormItem.Content>
                <div className="product-metadata-static-field">
                  Admin
                </div>
              </FormItem.Content>
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            { this.preview() }
          </Col>
        </Row>
      </div>
    );
  }
}

export default MetadataItemStatic;
