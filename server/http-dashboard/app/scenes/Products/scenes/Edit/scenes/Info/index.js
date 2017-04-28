import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {ProductInfoUpdateValues} from 'data/Product/actions';
import InfoForm from './components/InfoForm';

@connect((state) => ({
  product: state.Product.edit
}), (dispatch) => ({
  updateValues: bindActionCreators(ProductInfoUpdateValues, dispatch)
}))
class Info extends React.Component {

  static propTypes = {
    updateValues: React.PropTypes.func,

    product: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    this.invalid = false;
  }

  onChange(values) {
    this.props.updateValues(values);
  }

  getInitialValues() {
    const values = {
      boardType: this.props.product.info.values.boardType,
      connectionType: this.props.product.info.values.connectionType,
      name: this.props.product.info.values.name,
      description: this.props.product.info.values.description,
      logoUrl: this.props.product.info.values.logoUrl
    };
    return values;
  }

  render() {

    if (!this.props.product.info.values.name)
      return null;

    return (
      <InfoForm onChange={this.onChange.bind(this)} initialValues={this.getInitialValues()}/>
    );
  }
}

export default Info;
