import React from 'react';
import './styles.less';
import {Button} from 'antd';

class ProductCreate extends React.Component {
  render() {
    return (
      <div className="products-create">
        <div className="products-header">
          <div className="products-header-name">New Product</div>
          <div className="products-header-options">
            <Button type="default">Cancel</Button>
            <Button type="primary">Save</Button>
          </div>
        </div>
        <div className="products-content">

        </div>
      </div>
    );
  }
}

export default ProductCreate;
