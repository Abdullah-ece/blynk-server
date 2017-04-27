import React from 'react';
import {Row, Col} from 'antd';
import FormItem from 'components/FormItem';
import Preview from '../../components/Preview';
import classnames from 'classnames';
import {Roles} from 'services/Roles';

class MetadataItemStatic extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
    preview: React.PropTypes.shape({
      name: React.PropTypes.string,
      value: React.PropTypes.any
    }),
    role: React.PropTypes.string
  };

  constructor(props) {
    super(props);
  }

  preview() {

    return (
      <Preview>
        <Preview.Name>{this.props.preview.name}</Preview.Name> <Preview.Value>{this.props.preview.value}</Preview.Value>
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
                  {Roles[this.props.role].title}
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
