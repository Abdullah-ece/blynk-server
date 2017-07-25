import React      from 'react';
import {MainList} from 'components';
import './styles.less';

class Products extends React.Component {

  render() {
    return (
      <MainList className='organizations-create-products-list'>
        { this.props.products && this.props.products.map((product) => (
          <MainList.Item key={product.get('id')}
                         id={product.get('id')}
                         logoUrl={product.get('logoUrl')}
                         noImageText="No Product Image"
                         name={product.get('name')}
                         link={`/product/${product.get('id')}`}
                         devicesCount={product.get('devicesCount')}/>
        ))}

        { (!this.props.products || !this.props.products.size) && (
          <div>This organization doesn't have products</div>
        ) }

      </MainList>
    );
  }

}

export default Products;
