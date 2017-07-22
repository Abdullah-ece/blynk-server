import React          from 'react';
import {Tabs}         from 'antd';
import {List}         from 'immutable';
import PropTypes      from 'prop-types';
import {
  Info,
  Products
}                     from './components';
import './styles.less';

const {TabPane} = Tabs;

class Manage extends React.Component {

  static propTypes = {
    products: PropTypes.instanceOf(List),
    activeTab: PropTypes.string,

    onTabChange: PropTypes.func,
  };

  TABS = {
    INFO: 'info',
    PRODUCTS: 'products',
    ADMINS: 'admins'
  };

  render() {
    return (
      <Tabs onChange={this.props.onTabChange}
            activeKey={this.props.activeTab}>
        <TabPane tab="Info"
                 key={this.TABS.INFO}>
          <div className="organizations-manage-tab-wrapper">
            <Info />
          </div>
        </TabPane>
        <TabPane tab="Products"
                 key={this.TABS.PRODUCTS}>
          <Products products={this.props.products}/>
        </TabPane>
        <TabPane tab="Admins"
                 key={this.TABS.ADMINS}>
          Admins
        </TabPane>
      </Tabs>
    );
  }

}

export default Manage;
