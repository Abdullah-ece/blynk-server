import React      from 'react';
import {Form}     from './components';
import PropTypes  from 'prop-types';
import './styles.less';

class Info extends React.Component {

  static propTypes = {
    organizationName: PropTypes.string
  };

  render() {
    return (
      <Form organizationName={this.props.organizationName}/>
    );
  }

}

export default Info;
