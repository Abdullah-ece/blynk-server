import React        from 'react';
import {Link}       from 'react-router';
import {Button}     from 'antd';
import {
  MainList,
  MainLayout
}                   from 'components';

import './styles.less';

export default class ProductsList extends React.Component {

  static propTypes = {
    products: React.PropTypes.array
  };

  render() {
    return (
      <MainLayout>
        <MainLayout.Header title="Products" options={(
          <div>
            <Link to="/products/create">
              <Button icon="plus" type="primary">Create New Product</Button>
            </Link>
          </div>
        )}/>
        <MainLayout.Content>
          <MainList>
            { this.props.products.map((product, key) => (
              <MainList.Item key={key}
                             logoUrl={product.logoUrl}
                             name={product.name}
                             devicesCount={product.devicesCount}
                             link={`/product/${product.id}`}/>
            ))}
          </MainList>
        </MainLayout.Content>
      </MainLayout>
    );
  }
}
