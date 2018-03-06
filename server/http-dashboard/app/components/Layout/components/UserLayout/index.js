import React from 'react';
import Header from '../Header';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as AccountActions from '../../../../data/Account/actions';
import {blynkWsConnect, blynkWsLogin} from 'store/blynk-websocket-middleware/actions';

import './styles.less';

@connect((state) => ({
  Account: state.Account
}), (dispatch) => ({
  fetchAccount  : bindActionCreators(AccountActions.Account, dispatch),
  blynkWsConnect: bindActionCreators(blynkWsConnect, dispatch),
  blynkWsLogin  : bindActionCreators(blynkWsLogin, dispatch),
}))
class UserLayout extends React.Component {

  static propTypes = {
    Account       : React.PropTypes.object,
    children      : React.PropTypes.object,
    location      : React.PropTypes.object,
    fetchAccount  : React.PropTypes.func,
    blynkWsConnect: React.PropTypes.func,
    blynkWsLogin  : React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    props.fetchAccount();

  }

  componentWillMount() {
    this.props.blynkWsConnect().then(() => {
      this.props.blynkWsLogin({
        username: this.props.Account.credentials.username,
        hash    : this.props.Account.credentials.password
      });
    });
  }

  render() {

    return (
      <div className="user-layout">
        <Header location={this.props.location}/>
        {this.props.children}
      </div>
    );
  }

}

export default UserLayout;
