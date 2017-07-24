import React              from 'react';
import {Tabs, Icon}       from 'antd';
import {List, Map}        from 'immutable';
import PropTypes          from 'prop-types';
import {
  Info,
  Products
}                         from './components';
import './styles.less';

const {TabPane} = Tabs;

class Manage extends React.Component {

  static propTypes = {
    products: PropTypes.instanceOf(List),

    formErrors: PropTypes.instanceOf(Map),

    activeTab: PropTypes.string,

    submitFailed: PropTypes.bool,

    onTabChange: PropTypes.func,
  };

  TABS = {
    INFO: 'info',
    PRODUCTS: 'products',
    ADMINS: 'admins'
  };

  validateFields(fields) {
    if (fields.filter(value => this.props.formErrors.has(value)).length)
      return true;
  }

  infoTabInvalidIcon() {

    const fields = ['name'];

    if (this.validateFields(fields) && this.props.submitFailed)
      return (<Icon type="exclamation-circle-o" className="organizations-manage-tab-invalid"/>);

    return null;
  }

  productsTabInvalidIcon() {
    const fields = [];

    if (this.validateFields(fields) && this.props.submitFailed)
      return (<Icon type="exclamation-circle-o" className="organizations-manage-tab-invalid"/>);

    return null;
  }

  adminsTabInvalidIcon() {
    const fields = [];

    if (this.validateFields(fields) && this.props.submitFailed)
      return (<Icon type="exclamation-circle-o" className="organizations-manage-tab-invalid"/>);

    return null;
  }

  render() {
    return (
      <Tabs onChange={this.props.onTabChange}
            activeKey={this.props.activeTab}>
        <TabPane tab={<span>{this.infoTabInvalidIcon()} Info</span>}
                 key={this.TABS.INFO}>
          <div className="organizations-manage-tab-wrapper">
            <Info />
          </div>
        </TabPane>
        <TabPane tab={<span>{this.productsTabInvalidIcon()} Products</span>}
                 key={this.TABS.PRODUCTS}>
          <Products products={this.props.products}/>
        </TabPane>
        <TabPane tab={<span>{this.adminsTabInvalidIcon()} Admins</span>}
                 key={this.TABS.ADMINS}>
          Admins
        </TabPane>
      </Tabs>
    );
  }

}

export default Manage;
