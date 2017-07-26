import React                from 'react';
import PropTypes            from 'prop-types';
import {List, fromJS}       from 'immutable';
import {connect}            from 'react-redux';
import {bindActionCreators} from 'redux';
import {
  getFormValues,
  change
}                           from 'redux-form';
import {Manage}    from 'services/Organizations';
import {ProductsSelectList} from './components';
import './styles.less';

@connect((state) => ({
  formValues: fromJS(getFormValues(Manage.FORM_NAME)(state))
}), (dispatch) => ({
  changeValue: bindActionCreators(change, dispatch)
}))
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

    let products = this.props.formValues.get('selectedProducts');

    let index = products.indexOf(id);

    if (index === -1) {
      this.props.changeValue(Manage.FORM_NAME, 'selectedProducts', products.push(id).toJS());
    } else {
      this.props.changeValue(Manage.FORM_NAME, 'selectedProducts', products.remove(index).toJS());
    }
  }

  render() {
    return (
      <div className="organizations-create-products">
        <div className="organizations-create-products-title">Choose products available
          for {this.props.formValues.get('name')}</div>
        <ProductsSelectList onSelect={this.handleProductSelect}
                            value={this.props.formValues.get('selectedProducts').toJS()}
                            products={this.props.products}/>
      </div>
    );
  }

}

export default Products;
