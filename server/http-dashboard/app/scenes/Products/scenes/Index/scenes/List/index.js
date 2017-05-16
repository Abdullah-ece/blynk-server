import React from 'react';
import './styles.less';
import {Link} from 'react-router';
import {Button} from 'antd';
import ProductItem from './components/ProductItem';
import ProductHeader from 'scenes/Products/components/ProductHeader';

export default class ProductsList extends React.Component {

  static propTypes = {
    products: React.PropTypes.array
  };

  render() {
    const items = this.props.products.map(p => <ProductItem key={p.id} item={p}/>);

    return (
      <div className="product-list">
        <ProductHeader title="Products"
                       options={(
                         <div>
                           <Link to="/products/create">
                             <Button icon="plus" type="primary">Create New Product</Button>
                           </Link>
                         </div>
                       )}/>
        <div className="products-list-content">
          { items }
        </div>
      </div>
    );
  }
}
