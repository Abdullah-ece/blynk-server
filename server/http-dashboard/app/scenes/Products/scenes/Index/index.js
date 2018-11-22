import React from 'react';
import './styles.less';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as ProductsAPI from 'data/Product/api';
import {message} from 'antd';

import ProductsList from './scenes/List';
import NoProducts from './scenes/NoProducts';

@connect((state) => ({
  account: state.Account,
  Product: state.Product,
  Organization: state.Organization
}), (dispatch) => {
  return {
    ProductsFetch: bindActionCreators(ProductsAPI.ProductsFetch, dispatch),
  };
})
class ProductsIndex extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    account: React.PropTypes.object,
    location: React.PropTypes.object,
    ProductsFetch: React.PropTypes.func,
    Product: React.PropTypes.object,
    Organization: React.PropTypes.object,
  };

  constructor(props) {
    super(props);
  }

  componentWillMount() {
    this.fetchData();
    // if (this.props.Organization && !this.props.Organization.canCreateOrgs) {
    //   this.context.router.push('/dashboard');
    // }
    if (this.props.location.query && this.props.location.query.success) {
      message.success('Product created successfully');
      this.context.router.push('/products');
    }
    if (this.props.location.query && this.props.location.query.deleted) {
      message.success('Product deleted successfully');
      this.context.router.push('/products');
    }
    if (this.props.location.query && this.props.location.query.cloned) {
      message.success('Product cloned successfully');
      this.context.router.push('/products');
    }
  }

  componentDidUpdate(prevProps) {
    if(prevProps.account.selectedOrgId !== this.props.account.selectedOrgId) {
      this.fetchData();
    }
  }

  fetchData() {
    this.props.ProductsFetch({
      orgId: this.props.account.selectedOrgId,
    });
  }

  render() {
    const items = this.props.Product.products;

    if (items && items.length)
      return (
        <ProductsList products={items}/>
      );

    return (
      <NoProducts organization={this.props.Organization}/>
    );
  }
}

export default ProductsIndex;
