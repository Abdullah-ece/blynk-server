import React                from 'react';
import PropTypes            from 'prop-types';
import {List, Map, fromJS}  from 'immutable';
import {connect}            from 'react-redux';
import {bindActionCreators} from 'redux';
import {message}            from 'antd';
import {
  getFormValues,
  getFormInitialValues,
  change
}                           from 'redux-form';
import {Manage}             from 'services/Organizations';
import {Products}           from 'scenes/Organizations/components/Manage/components';

import {CanDeleteProduct} from 'data/Product/api';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';

@connect((state) => ({
  formValues: fromJS(getFormValues(Manage.FORM_NAME)(state)),
  initialFormValues: fromJS(getFormInitialValues(Manage.FORM_NAME)(state))
}), (dispatch) => ({
  changeValue: bindActionCreators(change, dispatch),
  StartLoading: bindActionCreators(StartLoading, dispatch),
  FinishLoading: bindActionCreators(FinishLoading, dispatch),
  CanDeleteProduct: bindActionCreators(CanDeleteProduct, dispatch),
}))
class ProductsEdit extends React.Component {

  static propTypes = {
    products: PropTypes.instanceOf(List),
    initialFormValues: PropTypes.instanceOf(Map),

    formValues: PropTypes.object,

    changeValue: PropTypes.func,
    StartLoading: PropTypes.func,
    FinishLoading: PropTypes.func,
    CanDeleteProduct: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleSelect = this.handleSelect.bind(this);
  }

  handleSelect(product) {

    const id = product.get('id');

    let products = this.props.formValues.get('selectedProducts');

    let index = products.indexOf(id);

    if (index === -1) {
      this.props.changeValue(Manage.FORM_NAME, 'selectedProducts', products.push(id).toJS());
    } else {

      // do canDeleteProduct only for products already was on org
      if (this.props.initialFormValues.get('selectedProducts').indexOf(id) !== -1) {
        this.props.StartLoading();

        this.props.CanDeleteProduct({
          id: product.get('cloneId')
        }).then(() => {
          this.props.FinishLoading();
          this.props.changeValue(Manage.FORM_NAME, 'selectedProducts', products.remove(index).toJS());
        }).catch(() => {
          message.warning('You cannot unselect this product');
          this.props.FinishLoading();
        });
      } else {
        this.props.changeValue(Manage.FORM_NAME, 'selectedProducts', products.remove(index).toJS());
      }

    }
  }

  render() {

    // we should display organization products (clones from original products) and original products
    // we able to clone to this org.

    // this code get products list and remove original products if we have already attached clone of this product

    // replace clone id to parent id to save product with original products ids


    let products = fromJS([]);
    let name = "";
    let selectedProducts = [];

    if (this.props.formValues) {
      name = this.props.formValues.get('name');
      selectedProducts = this.props.formValues.get('selectedProducts').toJS();
      const clonedProducts = this.props.formValues.get('products').map((product) =>
        product
          .set('isClone', true)
          .set('cloneId', product.get('id'))
          .set('id', product.get('parentId'))
      );

      const notClonedOriginalProducts = fromJS(this.props.products).filter(((originalProduct) =>
          clonedProducts.every((clonedProduct) => originalProduct.get('id') !== clonedProduct.get('parentId'))
      ));

      products = clonedProducts.concat(notClonedOriginalProducts);

    }

    return (
      <Products orgName={name}
                onSelect={this.handleSelect}
                value={selectedProducts}
                products={products}/>
    );
  }

}

export default ProductsEdit;
