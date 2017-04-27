import React from 'react';
import {Button, Tabs} from 'antd';
import './styles.less';
import Info from './scenes/Info';
import Metadata from './scenes/Metadata';

class ProductDetails extends React.Component {

  TABS = {
    INFO: 'info',
    METADATA: 'metadata',
    // DATA_STREAMS: 'datastreams',
    // EVENTS: 'events'
  };

  render() {
    return (
      <div className="products-create">
        <div className="products-header">
          <div className="products-header-name">Product name</div>
          <div className="products-header-options">
            <Button type="danger">Delete</Button>
            <Button type="primary">Edit</Button>
          </div>
        </div>
        <div className="products-content">
          <Tabs className="products-tabs">
            <Tabs.TabPane tab="Info" key={this.TABS.INFO}>
              <Info/>
            </Tabs.TabPane>
            <Tabs.TabPane tab="Metadata" key={this.TABS.METADATA}>
              <Metadata/>
            </Tabs.TabPane>
          </Tabs>
        </div>
      </div>
    );
  }
}

export default ProductDetails;
