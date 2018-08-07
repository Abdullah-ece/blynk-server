import React from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';

import * as API from './data/actions';

@connect(() => {
  return {};
}, (dispatch) => {
  return {
    Logout: bindActionCreators(API.Logout, dispatch)
  };
})
export default class Logout extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    Logout: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.props.Logout().catch(() => {
      this.context.router.push('/login');
    }).then(() => {
      this.context.router.push('/login');
    });
  }

  componentWillMount() {
    this.props.Logout().catch(() => {
      this.context.router.push('/login');
    }).then(() => {
      this.context.router.push('/login');
    });
  }

  componentDidUpdate() {
    this.context.router.push('/login');
  }

  render() {
    return <div/>;
  }
}
