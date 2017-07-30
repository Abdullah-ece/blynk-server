import React        from 'react';
import {MainList}   from 'components';
import PropTypes    from 'prop-types';
import {List}       from 'immutable';
import './styles.less';

class Products extends React.Component {

  static propTypes = {
    products: PropTypes.instanceOf(List)
  };

  render() {
    return (
      <MainList className="organizations-create-products-list">
        { this.props.products && this.props.products.map((product) => (
          <MainList.Item key={product.get('id')}
                         id={product.get('id')}
                         logoUrl={product.get('logoUrl')}
                         noImageText="No Product Image"
                         name={product.get('name')}
                         link={`/product/${product.get('id')}`}
                         devicesCount={product.get('deviceCount')}/>
        ))}

        { (!this.props.products || !this.props.products.size) && (
          <div>This organization doesn't have products</div>
        ) }

      </MainList>
    );
  }

}

export default Products;
