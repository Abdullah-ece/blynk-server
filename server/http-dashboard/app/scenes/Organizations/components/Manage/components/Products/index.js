import React                from 'react';
import PropTypes            from 'prop-types';
import {List}               from 'immutable';
import {ProductsSelectList} from './components';
import './styles.less';

class Products extends React.Component {

  static propTypes = {
    products: PropTypes.instanceOf(List),

    value: PropTypes.array,

    orgName: PropTypes.string,

    changeValue: PropTypes.func,

    onSelect: PropTypes.func,
  };

  render() {
    return (
      <div className="organizations-create-products">
        <div className="organizations-create-products-title">Choose products available
          for {this.props.orgName}</div>
        <ProductsSelectList onSelect={this.props.onSelect}
                            value={this.props.value}
                            products={this.props.products}/>
      </div>
    );
  }

}

export default Products;
