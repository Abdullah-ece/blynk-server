import React from 'react';
import {Button, Tabs, message} from 'antd';
import './styles.less';
import Info from './scenes/Info';
import Metadata from './scenes/Metadata';
import * as API from 'data/Product/api';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import _ from 'lodash';
import DeleteModal from './components/Delete';

@connect((state) => ({
  Product: state.Product.products
}), (dispatch) => ({
  Fetch: bindActionCreators(API.ProductsFetch, dispatch),
  Delete: bindActionCreators(API.ProductDelete, dispatch)
}))
class ProductDetails extends React.Component {

  static propTypes = {
    Fetch: React.PropTypes.func,
    Delete: React.PropTypes.func,

    params: React.PropTypes.object,
    location: React.PropTypes.object,

    Product: React.PropTypes.array,
  };

  static contextTypes = {
    router: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    this.state = {
      product: null,
      activeTab: props && props.params.tab || this.TABS.INFO,
      showDeleteModal: false
    };
  }

  componentDidMount() {
    if (this.props.location.query && this.props.location.query.save) {
      message.success('Product saved successfully');
      console.log('on init', this.props.params.tab);
      if (this.props.params.tab) {
        this.context.router.push(`/product/${this.props.params.id}/${this.props.params.tab}`);
      } else {
        this.context.router.push(`/product/${this.props.params.id}`);
      }
    }
    this.props.Fetch({
      id: this.props.params.id
    }).then(() => {
      const product = _.find(this.props.Product, {
        id: Number(this.props.params.id)
      });

      this.setState({
        product: product,
        showDeleteModal: false
      });
    });
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
  }

  handleDeleteSubmit() {
    return this.props.Delete(this.props.params.id).then(() => {
      this.toggleDelete();
      this.context.router.push('/products?deleted=true');
    }).catch((err) => {
      message.error(err.message || 'Cannot delete product');
    });
  }

  toggleDelete() {
    this.setState({
      showDeleteModal: !this.state.showDeleteModal
    });
  }

  handleEdit() {
    console.log(this.state);
    if (this.state.activeTab) {
      this.context.router.push(`/products/edit/${this.props.params.id}/${this.state.activeTab}`);
    } else {
      this.context.router.push(`/products/edit/${this.props.params.id}`);
    }
  }

  render() {

    if (!this.state.product) {
      return (<div />);
    }

    return (
      <div className="products-create">
        <div className="products-header">
          <div className="products-header-name">{this.state.product.name}</div>
          <div className="products-header-options">
            <Button type="danger" onClick={this.toggleDelete.bind(this)}>Delete</Button>
            <Button type="primary" onClick={this.handleEdit.bind(this)}>Edit</Button>
          </div>
        </div>
        <div className="products-content">
          <Tabs className="products-tabs"
                defaultActiveKey={this.TABS.INFO}
                activeKey={this.state.activeTab}
                onChange={this.handleTabChange.bind(this)}>
            <Tabs.TabPane tab="Info" key={this.TABS.INFO}>
              <Info product={this.state.product}/>
            </Tabs.TabPane>
            <Tabs.TabPane tab="Metadata" key={this.TABS.METADATA}>
              <Metadata product={this.state.product}/>
            </Tabs.TabPane>
          </Tabs>
          <DeleteModal deviceCount={this.state.product.deviceCount} onCancel={this.toggleDelete.bind(this)}
                       visible={this.state.showDeleteModal} handleSubmit={this.handleDeleteSubmit.bind(this)}
                       productName={this.state.product.name}/>
        </div>
      </div>
    );
  }
}

export default ProductDetails;
