import React                from 'react';
import PropTypes            from 'prop-types';
import {List, fromJS}       from 'immutable';
import {connect}            from 'react-redux';
import {bindActionCreators} from 'redux';
import {
  reduxForm,
  getFormValues,
  change
}                           from 'redux-form';
import {ProductsSelectList} from './components';
import './styles.less';

@connect((state) => ({
  formValues: fromJS(getFormValues('organizations-create-products')(state))
}), (dispatch) => ({
  changeValue: bindActionCreators(change, dispatch)
}))
@reduxForm({
  form: 'organizations-create-products'
})
class Products extends React.Component {

  static propTypes = {
    products: PropTypes.instanceOf(List),

    formValues: PropTypes.object,

    changeValue: PropTypes.func
  };

  constructor(props) {
    super(props);

    this.value = [];

    this.handleProductSelect = this.handleProductSelect.bind(this);
  }

  handleProductSelect(id) {

    id = Number(id);

    let products = this.props.formValues.get('products');

    let index = products.indexOf(id);

    if (index === -1) {
      this.props.changeValue('organizations-create-products', 'products', products.push(id).toJS());
    } else {
      this.props.changeValue('organizations-create-products', 'products', products.remove(index).toJS());
    }
  }

  render() {
    return (
      <div className="organizations-create-products">
        <div className="organizations-create-products-title">Choose products available for Organization</div>
        <ProductsSelectList onSelect={this.handleProductSelect}
                            value={this.props.formValues.get('products').toJS()}
                            products={this.props.products}/>
      </div>
    );
  }

}

export default Products;
