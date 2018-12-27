import React from 'react';
import Base from '../Base';
import { Fieldset, LinearIcon } from 'components';
import DeviceReferenceModal from './modal';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { Link } from 'react-router';
import _ from 'lodash';
import {GetDeviceByReferenceMetafield} from 'data/Devices/actions';

@connect((state) => ({
  products: state.Product.products,
  devices: state.Devices.devices,
}), (dispatch) => ({
  getDeviceByReferenceMetafield: bindActionCreators(GetDeviceByReferenceMetafield, dispatch)
}))
class DeviceReference extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    const value = _.find(this.props.devices, (device) => Number(device.id) === Number(field.selectedDeviceId));

    return (
      <Fieldset>
        <Fieldset.Legend type="dark"> <LinearIcon
          type={field.icon || 'cube'}/> {field.name}         </Fieldset.Legend>
        {value && value.name && (
          <Link to={`/devices/${value.id}`}>{value.name}</Link>
        ) || (<i>Device is not selected</i>)}
        {/*<i>No Device ={productIds}= ={value}=</i>*/}
        {/*{ !price && !units ? <i>No Value</i> : (*/}
        {/*<div>*/}
        {/*{ Number(field.perValue) === 1 ? (*/}
        {/*<p>{`${Currency[currency].abbreviation} ${price} / ${Unit[units].abbreviation}`}</p>*/}
        {/*) : (*/}
        {/*<p>{`${Currency[currency].abbreviation} ${price} / ${perValue} ${Unit[units].abbreviation}`}</p>*/}
        {/*)}*/}
        {/*</div>*/}
        {/*) }*/}
      </Fieldset>
    );
  }

  getEditableComponent() {
    const field = this.props.data;

    const productIds = Array.isArray(field.selectedProductIds) ? field.selectedProductIds : [];

    const products = productIds.map((productId) => {
      return _.find(this.props.products, (product) => Number(product.id) === Number(productId));
    }).filter((product) => !!product).map((product) => {
      return {
        id: Number(product.id),
        name: product.name,
        devices: this.props.devices.filter((device) => {
          return device.productName && product.name && device.productName === product.name;
        })
      };
    });

    const options = {};

    products.forEach((product) => {
      options[product.name] = product.devices.map((device) => ({
        key: String(device.id),
        value: String(device.name)
      }));
    });

    return (
      <div>
        <DeviceReferenceModal form={this.props.form}
                              options={options}
                              deviceId={this.props.device.id}
                              metafieldId={this.props.data.id}
                              getDeviceByReferenceMetafield={this.props.getDeviceByReferenceMetafield}/>
      </div>
    );
  }

}

export default DeviceReference;
