import React              from 'react';
import {Tabs, Icon}       from 'antd';
import {List, Map}        from 'immutable';
import PropTypes          from 'prop-types';
import {
  Info
}                         from './components';
import './styles.less';

const {TabPane} = Tabs;

class Manage extends React.Component {

  static propTypes = {
    products: PropTypes.instanceOf(List),

    formValues: PropTypes.instanceOf(Map),
    formErrors: PropTypes.instanceOf(Map),
    formAsyncErrors: PropTypes.instanceOf(Map),
    formSubmitErrors: PropTypes.instanceOf(Map),

    activeTab: PropTypes.string,

    submitFailed: PropTypes.bool,

    onTabChange: PropTypes.func,

    productsComponent: PropTypes.element,
    adminsComponent: PropTypes.element,
  };

  TABS = {
    INFO: 'info',
    PRODUCTS: 'products',
    ADMINS: 'admins'
  };

  validateFields(fields) {
    if (fields.filter(value => this.props.formErrors.has(value)).length)
      return true;

    if (fields.filter(value => this.props.formAsyncErrors.has(value)).length)
      return true;

    if (fields.filter(value => this.props.formSubmitErrors.has(value)).length)
      return true;
  }

  infoTabInvalidIcon() {

    const fields = ['name'];

    if (this.validateFields(fields) && this.props.submitFailed)
      return (<Icon type="exclamation-circle-o" className="organizations-manage-tab-invalid-icon"/>);

    return null;
  }

  productsTabInvalidIcon() {
    const fields = [];

    if (this.validateFields(fields) && this.props.submitFailed)
      return (<Icon type="exclamation-circle-o" className="organizations-manage-tab-invalid-icon"/>);

    return null;
  }

  adminsTabInvalidIcon() {
    const fields = ['admins'];

    if (this.validateFields(fields) && this.props.submitFailed)
      return (<Icon type="exclamation-circle-o" className="organizations-manage-tab-invalid-icon"/>);

    return null;
  }

  render() {
    return (
      <Tabs onChange={this.props.onTabChange}
            activeKey={this.props.activeTab}>
        <TabPane tab={<span>{this.infoTabInvalidIcon()} Info</span>}
                 key={this.TABS.INFO}>
          <div className="organizations-manage-tab-wrapper">
            <Info organizationName={this.props.formValues.get('name')}
                  canCreateOrgs={this.props.formValues.get('canCreateOrgs')}/>
          </div>
        </TabPane>
        <TabPane tab={<span>{this.productsTabInvalidIcon()} Products</span>}
                 key={this.TABS.PRODUCTS}>
          <div className="organizations-manage-tab-wrapper">
            { this.props.productsComponent }
          </div>
        </TabPane>
        <TabPane tab={<span>{this.adminsTabInvalidIcon()} Admins</span>}
                 key={this.TABS.ADMINS}>
          <div className="organizations-manage-tab-wrapper">
            { this.props.adminsComponent }
          </div>
        </TabPane>
      </Tabs>
    );
  }

}

export default Manage;
