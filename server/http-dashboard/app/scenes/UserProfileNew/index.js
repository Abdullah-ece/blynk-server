import React, {Component} from 'react';
import UserProfileComponent from './components/UserProfile';
import {TABS} from 'services/UserProfile';
import {connect} from 'react-redux';
import {
  tabChange
} from 'data/UserProfile/actions';
import {bindActionCreators} from 'redux';

@connect((state)=>{
  return {
    activeTab: state.UserProfile.activeTab
  };
},(dispatch) => {
  return {
    onTabChange: bindActionCreators(tabChange, dispatch)
  };
})
class UserProfile extends Component {

  static propTypes = {
    params: React.PropTypes.object,
    activeTab: React.PropTypes.string,
    onTabChange: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleTabChange = this.handleTabChange.bind(this);

  }

  componentWillMount() {

    if(this.props.params.tab) {
      this.handleTabChange(this.props.params.tab);
    }
  }
  handleTabChange(tab) {
    this.props.onTabChange(tab);
  }

  render() {
    const params = {
      activeTab: this.props.activeTab || TABS.ACCOUNT_SETTINGS.key
    };

    return(
      <UserProfileComponent params={params}
                            onTabChange={this.handleTabChange}/>
    );
  }
}

export default UserProfile;
