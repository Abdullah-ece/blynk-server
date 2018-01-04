import React        from 'react';
import {MainList}   from 'components';
import PropTypes    from 'prop-types';
import {List}       from 'immutable';
import './styles.less';

class Products extends React.Component {

  static propTypes = {
    products: PropTypes.instanceOf(List),
    companyName: PropTypes.string
  };

  render() {
    return (
      <MainList className="organizations-create-products-list non-clickable">
        { this.props.products && this.props.products.map((product) => (
          <MainList.Item key={product.get('id')}
                         id={product.get('id')}
                         logoUrl={product.get('logoUrl')}
                         noImageText="No Product Image"
                         name={product.get('name')}
                         devicesCount={product.get('deviceCount')}/>
        ))}

        { (!this.props.products || !this.props.products.size) && (
          <div>{this.props.companyName} doesn't have assigned Products yet.<br/>Click Edit button to assign products to this Organization</div>
        ) }

      </MainList>
    );
  }

}

export default Products;
