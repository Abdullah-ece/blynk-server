import React from 'react';
import {Button} from 'antd';
import {Link} from 'react-router';
import './styles.less';

class ProductsIndex extends React.Component {
  render() {
    return (
      <div className="products">

        <div className="products-no-items">
          <div className="products-no-items-title">Start by creating your first product</div>
          <div className="products-no-items-description">Product is a digital model of a physical object. It is used in
            Blynk platform as a template to be assigned to devices. {/*Lean More @todo add link when have this page*/}
          </div>
          <div className="products-no-items-action">
            <Link to="/products/create">
              <Button icon="plus" type="primary">Create New Product</Button>
            </Link>
          </div>
        </div>

      </div>
    );
  }
}

export default ProductsIndex;
