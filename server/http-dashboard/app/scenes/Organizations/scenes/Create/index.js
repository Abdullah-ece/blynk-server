import React        from 'react';
import {connect}        from 'react-redux';
import {bindActionCreators}        from 'redux';
import PropTypes        from 'prop-types';

import {
  OrganizationsManageSetActiveTab
}                   from 'data/Organizations/actions';

import {
  Create as OrganizationCreate
}                   from 'scenes/Organizations/components';

import './styles.less';

@connect((state) => ({
  activeTab: state.Organizations.getIn(['manage', 'activeTab'])
}), (dispatch) => ({
  setTab: bindActionCreators(OrganizationsManageSetActiveTab, dispatch)
}))
class Create extends React.Component {

  static propTypes = {
    setTab: PropTypes.func,

    activeTab: PropTypes.string
  };

  constructor(props) {
    super(props);

    this.handleTabChange = this.handleTabChange.bind(this);
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
