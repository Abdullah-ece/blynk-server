import React from 'react';
import './styles.less';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as ProductsAPI from './data/actions';

import ProductsList from './scenes/List';
import NoProducts from './scenes/NoProducts';

@connect((state) => ({
  Product: state.Product
}), (dispatch) => {
  return {
    ProductsFetch: bindActionCreators(ProductsAPI.ProductsFetch, dispatch),
  };
})
class ProductsIndex extends React.Component {

  static propTypes = {
    ProductsFetch: React.PropTypes.func,
    Product: React.PropTypes.obj
  };

  constructor(props) {
    super(props);

    this.props.ProductsFetch();
  }

  render() {
    const items = this.props.Product.products;

    return (
      <div className="product-container">

        { items && items.length ? <ProductsList products={items}/> : <NoProducts/> }

      </div>
    );
  }
}

export default ProductsIndex;
