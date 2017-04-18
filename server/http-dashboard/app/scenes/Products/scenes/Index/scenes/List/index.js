import React from 'react';
import './styles.less';
import {Link} from 'react-router';
import {Button} from 'antd';
import ProductItem from './components/ProductItem';

export default class ProductsList extends React.Component {

  static propTypes = {
    products: React.PropTypes.array
  };

  render() {
    const items = this.props.products.map(p => <ProductItem key={p.id} item={p}/>);

    return (
      <div className="product-list">
        <div className="products-header">
          <div className="products-header-name">Products</div>
          <div className="products-header-options">
            <Link to="/products/create">
              <Button icon="plus" type="primary">Create New Product</Button>
            </Link>
          </div>
        </div>
        <div className="products-list-content">
          { items }
        </div>
      </div>
    );
  }
}
