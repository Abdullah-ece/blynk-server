import React                from 'react';
import PropTypes            from 'prop-types';
import {List, fromJS}       from 'immutable';
import {connect}            from 'react-redux';
import {bindActionCreators} from 'redux';
import {
  getFormValues,
  change
}                           from 'redux-form';
import {Manage}             from 'services/Organizations';
import {Products}           from 'scenes/Organizations/components/Manage/components';

@connect((state) => ({
  formValues: fromJS(getFormValues(Manage.FORM_NAME)(state))
}), (dispatch) => ({
  changeValue: bindActionCreators(change, dispatch)
}))
class ProductsCreate extends React.Component {

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
      <Products orgName={this.props.formValues.get('name')}
                onSelect={this.handleProductSelect}
                value={this.props.formValues.get('selectedProducts').toJS()}
                products={this.props.products}/>
    );
  }

}

export default ProductsCreate;
