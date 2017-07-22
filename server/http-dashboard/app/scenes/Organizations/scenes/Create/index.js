import React                  from 'react';
import {connect}              from 'react-redux';
import {bindActionCreators}   from 'redux';
import {initialize, destroy}  from 'redux-form';
import {Map, List, fromJS}    from 'immutable';
import PropTypes              from 'prop-types';
import {ProductsFetch}        from 'data/Product/api';
import {Manage}               from 'services/Organizations';

import {
  OrganizationsManageSetActiveTab,
  OrganizationsManageUpdate
}                             from 'data/Organizations/actions';

import {
  Create as OrganizationCreate
}                             from 'scenes/Organizations/components';

import './styles.less';

@connect((state) => ({
  products: fromJS(state.Product.products),
  manage: state.Organizations.get('manage'),
  activeTab: state.Organizations.getIn(['manage', 'activeTab'])
}), (dispatch) => ({
  updateManage: bindActionCreators(OrganizationsManageUpdate, dispatch),
  setTab: bindActionCreators(OrganizationsManageSetActiveTab, dispatch),
  fetchProducts: bindActionCreators(ProductsFetch, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  destroyForm: bindActionCreators(destroy, dispatch),
}))
class Create extends React.Component {

  static propTypes = {
    setTab: PropTypes.func,
    destroyForm: PropTypes.func,
    updateManage: PropTypes.func,
    initializeForm: PropTypes.func,
    fetchProducts: PropTypes.func,

    products: PropTypes.instanceOf(List),
    manage: PropTypes.instanceOf(Map),
    activeTab: PropTypes.string
  };

  constructor(props) {
    super(props);

    this.handleTabChange = this.handleTabChange.bind(this);
  }

  componentWillMount() {
    this.props.updateManage(
      this.props.manage
        .updateIn(['info', 'form'], () => Manage.INFO_FORM_NAME)
        .updateIn(['products', 'form'], () => Manage.PRODUCTS_FORM_NAME)
    );

    this.props.fetchProducts();

    this.props.initializeForm(Manage.INFO_FORM_NAME, {
      name: 'New Organization'
    });

    this.props.initializeForm(Manage.PRODUCTS_FORM_NAME, {
      products: []
    });
  }

  componentWillUnmount() {
    this.props.updateManage(
      this.props.manage.set('activeTab', Manage.DEFAULT_TAB)
    );

    this.props.destroyForm(this.props.manage.getIn(['info', 'form']));
    this.props.destroyForm(this.props.manage.getIn(['products', 'form']));
  }

  TABS = {
    INFO: 'Info',
    PRODUCTS: 'Products',
    ADMINS: 'Admins'
  };

  handleTabChange(tab) {
    this.props.setTab(tab);
  }

  render() {
    return (
      <OrganizationCreate
        products={this.props.products}
        onTabChange={this.handleTabChange}
        activeTab={this.props.activeTab}
      />
    );
  }

}

export default Create;
