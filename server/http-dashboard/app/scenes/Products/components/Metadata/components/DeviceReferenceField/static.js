import React from 'react';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';
import FieldStub from 'scenes/Products/components/FieldStub';
import {connect} from 'react-redux';
import _ from 'lodash';

@connect((state) => ({
  products: (state.Product.products || [])
}))
class DeviceReference extends BaseField.Static {

  DEFAULT_VALUE = 'No Value';

  static propTypes = {
    name: React.PropTypes.string,
    selectedProductIds: React.PropTypes.any,
    products: React.PropTypes.array,
  };

  getPreviewValues() {
    const name = this.props.name;
    const value = this.props.selectedProductIds;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' ? value.trim() : null
    };
  }

  getProductsArray() {
    return (Array.isArray(this.props.selectedProductIds) ? this.props.selectedProductIds : []).map((productId) => (
      _.find(this.props.products, (product) => Number(product.id) === Number(productId))
    )).filter((product) => !!product);
  }

  component() {

    const products = this.getProductsArray();

    return (
      <FormItem offset={false}>
        <FormItem.Title>Device Reference</FormItem.Title>
        <FormItem.Content input>
          <FieldStub>
            {this.props.name}
          </FieldStub>
        </FormItem.Content>
        <br/>
        <FormItem.Title>Selected Product(s)</FormItem.Title>
        <FormItem.Content input>
          {products.map((product) => (
            <FieldStub inline={true} key={product.name}>
              {product.name}
            </FieldStub>
          ))}
          { !products.length && (
            <FieldStub inline={true} noValueMessage={`No Product(s) selected`}/>
          )}
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default DeviceReference;
