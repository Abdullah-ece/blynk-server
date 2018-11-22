import React from 'react';
import PropTypes from 'prop-types';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import {OrganizationPreloadFetch} from "data/Organization/actions";
import {ProductsPreloadFetch} from "data/Product/api";

import FullSizeLoading from 'scenes/FullSizeLoading';

@connect((state) => ({
  orgId: state.Account.selectedOrgId,
  productsLoading: state.Product.productsPreloadLoading,
  organizationsLoading: state.Organization.organizationPreloadLoading,
  Organization: state.Organization,
}), (dispatch) => ({
  fetchOrganization: bindActionCreators(OrganizationPreloadFetch, dispatch),
  fetchProduct     : bindActionCreators(ProductsPreloadFetch, dispatch),
}))
class ProductsPreload extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    children: PropTypes.any,
    orgId: PropTypes.number,
    fetchOrganization: PropTypes.func,
    fetchProduct: PropTypes.func,
    productsLoading: PropTypes.bool,
    organizationsLoading: PropTypes.bool,
    Organization: PropTypes.object,
  };

  componentWillMount() {
    this.props.fetchOrganization({
      id: this.props.orgId
    });
    this.props.fetchProduct();
  }

  render() {

    if(this.props.Organization && this.props.Organization.parentId !== -1) {
      this.context.router.push('/devices');
      return null;
    }

    if(this.props.productsLoading || this.props.organizationsLoading)
      return <FullSizeLoading />;

    return this.props.children;
  }

}

export default ProductsPreload;
