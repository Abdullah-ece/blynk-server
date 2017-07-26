import React      from 'react';
import {Form}     from './components';
import PropTypes  from 'prop-types';
import './styles.less';

class Info extends React.Component {

  static propTypes = {
    organizationName: PropTypes.string,

    canCreateOrgs: PropTypes.bool
  };

  render() {
    return (
      <Form organizationName={this.props.organizationName} canCreateOrgs={this.props.canCreateOrgs}/>
    );
  }

}

export default Info;
