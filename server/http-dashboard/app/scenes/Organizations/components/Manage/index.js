import React        from 'react';
import {Tabs}        from 'antd';
import PropTypes     from 'prop-types';
// import {MainLayout} from 'components'
import './styles.less';

const {TabPane} = Tabs;

class Manage extends React.Component {

  static propTypes = {
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
            Info
          </div>
        </TabPane>
        <TabPane tab="Products"
                 key={this.TABS.PRODUCTS}>
          Products
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
