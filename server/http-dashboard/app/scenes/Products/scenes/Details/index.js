import React from 'react';
import {Button, Tabs} from 'antd';
import './styles.less';
import Info from './scenes/Info';
import Metadata from './scenes/Metadata';
import * as API from 'data/Product/api';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import _ from 'lodash';

@connect((state) => ({
  Product: state.Product.products
}), (dispatch) => ({
  Fetch: bindActionCreators(API.ProductsFetch, dispatch)
}))
class ProductDetails extends React.Component {

  static propTypes = {
    params: React.PropTypes.object,
    fetch: React.PropTypes.func,
    Product: React.PropTypes.array
  };

  constructor(props) {
    super(props);

    this.state = {
      product: null
    };
  }

  componentDidMount() {
    this.props.Fetch({
      id: this.props.params.id
    }).then(() => {
      const product = _.find(this.props.Product, {
        id: Number(this.props.params.id)
      });

      this.setState({
        product: product
      });
    });
  }

  TABS = {
    INFO: 'info',
    METADATA: 'metadata',
    // DATA_STREAMS: 'datastreams',
    // EVENTS: 'events'
  };

  render() {

    if (!this.state.product) {
      return (<div />);
    }

    return (
      <div className="products-create">
        <div className="products-header">
          <div className="products-header-name">{this.state.product.name}</div>
          <div className="products-header-options">
            <Button type="danger">Delete</Button>
            <Button type="primary">Edit</Button>
          </div>
        </div>
        <div className="products-content">
          <Tabs className="products-tabs">
            <Tabs.TabPane tab="Info" key={this.TABS.INFO}>
              <Info product={this.state.product}/>
            </Tabs.TabPane>
            <Tabs.TabPane tab="Metadata" key={this.TABS.METADATA}>
              <Metadata product={this.state.product}/>
            </Tabs.TabPane>
          </Tabs>
        </div>
      </div>
    );
  }
}

export default ProductDetails;
