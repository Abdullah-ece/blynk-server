import React from 'react';
import FormItem from 'components/FormItem';
import {MetadataField as MetadataFormField, MetadataSelect as MetadataFormSelect} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import Static from './static';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {fromJS} from 'immutable';
import _ from 'lodash';

@connect((state) => ({
  products: (state.Product.products || [])
}))
class DeviceReferenceField extends BaseField {

  static propTypes = {
    products: PropTypes.array,
  };

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);

  }

  getPreviewValues() {


    let name = this.props.field.get('name');
    let products = fromJS(this.props.field.get('selectedProductIds') || []).toJS();

    let value = products.map((productId) => {
      let index = _.findIndex(this.props.products, (product) => Number(product.id) === Number(productId));

      if(index >= 0) {
        return this.props.products[index] && this.props.products[index].name || 'No Product Name';
      }

      return null;
    });

    if(value.length > 1) {
      value = 'Multiple';
    } else {
      value = value.length ? value[0] : null;
    }

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' ? value.trim() : null
    };
  }

  component() {

    const products = this.props.products.map((product) => ({
      key: String(product.id),
      value: product.name,
    }));

    return (
      <FormItem offset={false}>
        <FormItem.Title>Device Reference</FormItem.Title>
        <FormItem.Content>
            <MetadataFormField className={`metadata-name-field-${this.props.field.get('id')}`}
                               onFocus={this.onFocus} onBlur={this.onBlur}
                               validateOnBlur={true} name={`metaFields.${this.props.metaFieldKey}.name`} type="text" placeholder="Field Name" validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
        </FormItem.Content>
        <FormItem.Title>Product(s)</FormItem.Title>
        <FormItem.Content>
          <MetadataFormSelect mode="multiple" onFocus={this.onFocus} onBlur={this.onBlur}
                              defaultValue={[]}
                              name={`metaFields.${this.props.metaFieldKey}.selectedProductIds`} type="text" placeholder="Choose Product(s)"
                              dropdownClassName="product-metadata-item-unit-dropdown" values={products}/>
        </FormItem.Content>
      </FormItem>
    );
  }

}

DeviceReferenceField.Static = Static;
export default DeviceReferenceField;
