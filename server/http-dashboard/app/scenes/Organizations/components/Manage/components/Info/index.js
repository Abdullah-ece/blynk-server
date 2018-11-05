import React      from 'react';
import {Form}     from './components';
import PropTypes  from 'prop-types';
import './styles.less';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {SecureTokenForUploadFetch} from "data/Product/api";

@connect((state) => ({
  secureUploadToken: state.Product.secureUploadToken
}), (dispatch) => ({
  secureTokenForUploadFetch: bindActionCreators(SecureTokenForUploadFetch, dispatch),
}))
class Info extends React.Component {

  static propTypes = {
    secureTokenForUploadFetch: PropTypes.func,

    organizationName: PropTypes.string,
    secureUploadToken: PropTypes.string,

    canCreateOrgs: PropTypes.bool
  };

  constructor(props) {
    super(props);

    this.props.secureTokenForUploadFetch();
  }

  componentWillMount() {
    this.props.secureTokenForUploadFetch();
  }

  render() {
    return (
      <Form secureUploadToken={this.props.secureUploadToken} organizationName={this.props.organizationName} canCreateOrgs={this.props.canCreateOrgs}/>
    );
  }

}

export default Info;
