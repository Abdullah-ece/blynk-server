import React from 'react';
import { Row, Col } from 'antd';
import FormItem from 'components/FormItem';
import Preview from 'scenes/Products/components/Preview';
import classnames from 'classnames';
import { connect } from 'react-redux';
import LinearIcon from "components/LinearIcon";

@connect((state) => ({
  roles: state.Organization.roles,
}))
class MetadataItemStatic extends React.Component {

  static propTypes = {
    roles: React.PropTypes.any,
    children: React.PropTypes.any,
    icon: React.PropTypes.string,
    preview: React.PropTypes.shape({
      name: React.PropTypes.string,
      value: React.PropTypes.any,
      inline: React.PropTypes.any
    }),
    role: React.PropTypes.array,
  };

  constructor(props) {
    super(props);
  }

  preview() {

    return (
      <Preview inline={this.props.preview.inline}>
        <Preview.Name>{this.props.preview.name}</Preview.Name>
        <Preview.Value>{this.props.preview.value || 'Empty'}</Preview.Value>
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
        <Row gutter={0}>
          <Col span={2} className="product-metadata-item--icon-select-section">
            <div className="product-metadata-item--icon-selected">
              <LinearIcon type={this.props.icon || 'cube'}/>
            </div>
          </Col>
          <Col span={12}>
            {this.props.children}
          </Col>
          <Col span={6} offset={1}>
            {this.preview()}
          </Col>
        </Row>
        <Row>
          <Col span={4}>
            <FormItem offset={false}>
              <FormItem.Title>Who can edit</FormItem.Title>
              <FormItem.Content>

                {
                  this.props.roles.filter((role) => (
                    (this.props.role || []).indexOf(Number(role.id)) !== -1
                  )).map((role) => (
                    <div
                      className="product-metadata-static-field product-metadata-static-field-inline"
                      key={role.id}>
                      {role.name}
                    </div>
                  ))
                }

              </FormItem.Content>
            </FormItem>
          </Col>
        </Row>
      </div>
    );
  }
}

export default MetadataItemStatic;
