import React                from 'react';
import PropTypes            from 'prop-types';
import {List, fromJS}       from 'immutable';
import {connect}            from 'react-redux';
import {bindActionCreators} from 'redux';
import {message}            from 'antd';
import {
  getFormValues,
  change
}                           from 'redux-form';
import {Manage}             from 'services/Organizations';
import {Products}           from 'scenes/Organizations/components/Manage/components';

import {CanDeleteProduct} from 'data/Product/api';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';

@connect((state) => ({
  formValues: fromJS(getFormValues(Manage.FORM_NAME)(state))
}), (dispatch) => ({
  changeValue: bindActionCreators(change, dispatch),
  StartLoading: bindActionCreators(StartLoading, dispatch),
  FinishLoading: bindActionCreators(FinishLoading, dispatch),
  CanDeleteProduct: bindActionCreators(CanDeleteProduct, dispatch),
}))
class ProductsEdit extends React.Component {

  static propTypes = {
    products: PropTypes.instanceOf(List),

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

  handleSelect(id) {
    id = Number(id);

    let products = this.props.formValues.get('selectedProducts');

    let index = products.indexOf(id);

    if (index === -1) {
      this.props.changeValue(Manage.FORM_NAME, 'selectedProducts', products.push(id).toJS());
    } else {

      this.props.StartLoading();

      this.props.CanDeleteProduct({
        id: id
      }).then(() => {
        this.props.FinishLoading();
        this.props.changeValue(Manage.FORM_NAME, 'selectedProducts', products.remove(index).toJS());
      }).catch(() => {
        message.warning('You cannot unselect this product');
        this.props.FinishLoading();
      });

    }
  }

  render() {
    return (
      <Products orgName={this.props.formValues.get('name')}
                onSelect={this.handleSelect}
                value={this.props.formValues.get('selectedProducts').toJS()}
                products={this.props.products}/>
    );
  }

}

export default ProductsEdit;
