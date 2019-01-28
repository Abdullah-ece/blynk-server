import React from 'react';
import {
  Button, Tabs, message
} from 'antd';
import Info from './scenes/Info';
import Metadata from './scenes/Metadata';
import DataStreams from './scenes/DataStreams';
import Events from './scenes/Events';
import Dashboard from './scenes/Dashboard';
// import OTA from './scenes/OTA';
import * as API from 'data/Product/api';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import _ from 'lodash';
import { TABS } from 'services/Products';
import { displayError } from 'services/ErrorHandling';
import './styles.less';

import { MainLayout } from 'components';
import { VerifyPermission, PERMISSIONS_INDEX } from "services/Roles";

@connect((state) => ({
  account: state.Account,
  Product: state.Product.products,
  permissions: state.RolesAndPermissions.currentRole.permissionGroup1,
}), (dispatch) => ({
  Fetch: bindActionCreators(API.ProductFetch, dispatch),
  FetchAll: bindActionCreators(API.ProductsFetch, dispatch),
}))
class ProductDetails extends React.Component {

  static propTypes = {
    Fetch: React.PropTypes.func,
    FetchAll: React.PropTypes.func,

    params: React.PropTypes.object,
    location: React.PropTypes.object,

    account: React.PropTypes.object,

    Product: React.PropTypes.array,

    permissions: React.PropTypes.number,
  };

  static contextTypes = {
    router: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    this.state = {
      product: null,
      activeTab: props && props.params.tab || TABS.INFO,
    };

    this.handleClone = this.handleClone.bind(this);
    this.handleEdit = this.handleEdit.bind(this);
    this.handleTabChange = this.handleTabChange.bind(this);

  }

  componentDidMount() {
    if (this.props.location.query && this.props.location.query.save) {
      message.success('Product saved successfully');
      if (this.props.params.tab) {
        this.context.router.push(`/product/${this.props.params.id}/${this.props.params.tab}`);
      } else {
        this.context.router.push(`/product/${this.props.params.id}`);
      }
    }

    this.props.FetchAll({
      orgId: this.props.account.selectedOrgId
    }).catch((err) => {
      displayError(err, message.error);
    });

    this.props.Fetch({
      orgId: this.props.account.selectedOrgId,
      id: this.props.params.id
    }).then(() => {
      const product = _.find(this.props.Product, {
        id: Number(this.props.params.id)
      });

      this.setState({
        product: product,
        showDeleteModal: false,
        enteringEditMode: false,
      });
    }).catch((err) => {
      displayError(err, message.error);
    });
  }

  componentWillUnmount() {
    this.setState({ "enteringEditMode": false });
  }

  TABS = {
    INFO: 'info',
    METADATA: 'metadata',
    // DATA_STREAMS: 'datastreams',
    // EVENTS: 'events'
  };

  handleTabChange(key) {
    this.setState({
      activeTab: key
    });

    this.context.router.push(`/product/${this.props.params.id}/${key}`);
  }

  enteringEditMode = false;

  handleEdit() {
    this.setState({ "enteringEditMode": true });

    if (this.state.activeTab) {
      this.context.router.push(`/products/edit/${this.props.params.id}/${this.state.activeTab}`);
    } else {
      this.context.router.push(`/products/edit/${this.props.params.id}`);
    }
  }

  handleClone() {
    this.context.router.push(`/products/clone/${this.props.params.id}`);
  }

  render() {

    if (!this.state.product) {
      return (<div/>);
    }

    return (
      <MainLayout>
        <MainLayout.Header title={this.state.product.name}
                           options={VerifyPermission(this.props.permissions, PERMISSIONS_INDEX.PRODUCT_EDIT) && (
                             <div>
                               <Button type="default"
                                       onClick={this.handleClone}>
                                 Clone
                               </Button>
                               <Button type="primary"
                                       loading={this.state.enteringEditMode}
                                       onClick={this.handleEdit}>
                                 Edit
                               </Button>
                             </div>
                           ) || (null)}/>
        <MainLayout.Content className="product-details-content">
          <Tabs className="products-tabs"
                defaultActiveKey={TABS.INFO}
                activeKey={this.state.activeTab}
                onChange={this.handleTabChange}>
            <Tabs.TabPane tab="Info" key={TABS.INFO}>
              <Info product={this.state.product}/>
            </Tabs.TabPane>
            <Tabs.TabPane tab="Metadata" key={TABS.METADATA}>
              <Metadata product={this.state.product}/>
            </Tabs.TabPane>
            <Tabs.TabPane tab="Data Streams" key={TABS.DATA_STREAMS}>
              <DataStreams product={this.state.product}/>
            </Tabs.TabPane>
            <Tabs.TabPane tab="Events" key={TABS.EVENTS}>
              <Events fields={this.state.product.events}/>
            </Tabs.TabPane>
            <Tabs.TabPane tab="Dashboard" key={TABS.DASHBOARD}>
              <Dashboard webDashboard={this.state.product.webDashboard}/>
            </Tabs.TabPane>
            {/*{VerifyPermission(this.props.permissions, PERMISSIONS_INDEX.OTA_VIEW) && (*/}
              {/*<Tabs.TabPane tab="OTA" key={TABS.OTA}>*/}
                {/*<OTA params={this.props.params}/>*/}
              {/*</Tabs.TabPane>)}*/}
          </Tabs>
        </MainLayout.Content>
      </MainLayout>
    );
  }
}

export default ProductDetails;
