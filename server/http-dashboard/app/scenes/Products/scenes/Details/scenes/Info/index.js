import React from 'react';
import {Row, Col} from 'antd';
import FormItem from 'components/FormItem';
import {CONNECTIONS_TYPES, HARDWARES} from 'services/Devices';
import _ from 'lodash';

class Info extends React.Component {

  static propTypes = {
    product: React.PropTypes.object
  };

  render() {

    const connectionType = _.find(CONNECTIONS_TYPES, {key: this.props.product.connectionType}).value;
    const boardType = _.find(HARDWARES, {key: this.props.product.boardType}).value;

    return (
      <div className="products-create-tabs-inner-content">
        <Row gutter={24}>
          <Col span={15}>
            <div className="product-details-row">
              <Row gutter={24}>
                <Col span={12}>
                  <FormItem>
                    <FormItem.Title>hardware</FormItem.Title>
                    <FormItem.Content>
                      { boardType }
                    </FormItem.Content>
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem>
                    <FormItem.Title>connection type</FormItem.Title>
                    <FormItem.Content>
                      { connectionType }
                    </FormItem.Content>
                  </FormItem>
                </Col>
              </Row>
            </div>
            <div className="product-details-row">
              <Row gutter={32} className="row">
                <Col span={24}>
                  { this.props.product.description && (
                    <FormItem>
                      <FormItem.Title>Description</FormItem.Title>
                      <FormItem.Content>
                        { this.props.product.description }
                      </FormItem.Content>
                    </FormItem>
                  )}
                </Col>
              </Row>
            </div>
          </Col>
          <Col span={9}>
            <div className="product-details-row product-details-image">
              <img
                src={this.props.product.logoUrl}
                alt=""/>
            </div>
          </Col>
        </Row>
      </div>
    );
  }
}

export default Info;
