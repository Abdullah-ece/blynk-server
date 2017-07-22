import React        from 'react';
import {MainList}   from 'components';
import {List}       from 'immutable';
import classnames   from 'classnames';
import PropTypes    from 'prop-types';
import './styles.less';

class ProductSelectList extends React.Component {

  static propTypes = {
    products: PropTypes.instanceOf(List),
    value: PropTypes.array,
    onSelect: PropTypes.func
  };

  constructor(props) {
    super(props);

    this.handleClick = this.handleClick.bind(this);
  }

  isChecked(id) {
    return this.props.value.indexOf(id) !== -1;
  }

  isAnyChecked() {
    return !!this.props.value.length;
  }

  handleClick(id) {
    this.props.onSelect(id);
  }

  render() {

    const className = classnames({
      'organizations-create-products-list': true,
      'organizations-create-products-list--active': !!this.props.value.length
    });

    return (
      <MainList className={className}>
        { this.props.products.map((product) => (
          <MainList.Item key={product.get('id')}
                         id={product.get('id')}
                         onItemClick={this.handleClick}
                         logoUrl={product.get('logoUrl')}
                         isChecked={this.isChecked(product.get('id'))}
                         lightOverlay={this.isAnyChecked()}
                         noImageText="No Product Image"
                         name={product.get('name')}
                         devicesCount={product.get('devicesCount')}/>
        ))}
      </MainList>
    );
  }

}

export default ProductSelectList;
