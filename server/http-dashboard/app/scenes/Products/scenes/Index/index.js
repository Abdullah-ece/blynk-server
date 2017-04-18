import React from 'react';
import './styles.less';

import ProductsList from './scenes/List';
import NoProducts from './scenes/NoProducts';

class ProductsIndex extends React.Component {

  render() {
    const products = [];

    return (
      <div className="product-container">

        { products.length ? <ProductsList products={products}/> : <NoProducts/> }

      </div>
    );
  }
}

export default ProductsIndex;
