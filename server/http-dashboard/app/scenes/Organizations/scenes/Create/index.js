import React                  from 'react';
import {connect}              from 'react-redux';
import {bindActionCreators}   from 'redux';
import {initialize}           from 'redux-form';
import {Map}                  from 'immutable';
import PropTypes              from 'prop-types';

import {
  OrganizationsManageSetActiveTab,
  OrganizationsManageUpdate
}                             from 'data/Organizations/actions';

import {
  Create as OrganizationCreate
}                             from 'scenes/Organizations/components';

import './styles.less';

@connect((state) => ({
  manage: state.Organizations.get('manage'),
  activeTab: state.Organizations.getIn(['manage', 'activeTab'])
}), (dispatch) => ({
  updateManage: bindActionCreators(OrganizationsManageUpdate, dispatch),
  setTab: bindActionCreators(OrganizationsManageSetActiveTab, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch)
}))
class Create extends React.Component {

  static propTypes = {
    setTab: PropTypes.func,
    updateManage: PropTypes.func,
    initializeForm: PropTypes.func,

    manage: PropTypes.instanceOf(Map),
    activeTab: PropTypes.string
  };

  constructor(props) {
    super(props);

    this.handleTabChange = this.handleTabChange.bind(this);
  }

  componentWillMount() {
    this.props.updateManage(
      this.props.manage.updateIn(['info', 'form'], () => 'organizations-create-info')
    );

    this.props.initializeForm('organizations-create-info', {
      name: 'New Organization'
    });
  }

  TABS = {
    INFO: 'Info',
    PRODUCTS: 'Products',
    ADMINS: 'Admins'
  };

  handleTabChange(tab) {
    this.props.setTab(tab);
  }

  render() {
    return (
      <OrganizationCreate onTabChange={this.handleTabChange} activeTab={this.props.activeTab}/>
    );
  }

}

export default Create;
