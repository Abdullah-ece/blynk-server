import React, {Component} from 'react';
import {Button, message} from "antd";
import PropTypes from "prop-types";
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {OrganizationSendInvite} from "data/Organization/actions";
import {displayError} from "services/ErrorHandling";

@connect(() => ({
}), (dispatch) => ({
  orgSendInvite: bindActionCreators(OrganizationSendInvite, dispatch),
}))
class ResendInvite extends Component {

  static propTypes = {
    orgSendInvite: PropTypes.func,
    user: PropTypes.shape({
      orgId: PropTypes.number.isRequired,
      email: PropTypes.string.isRequired,
      name: PropTypes.string.isRequired,
      roleId: PropTypes.number.isRequired,
    }).isRequired
  };

  constructor(props) {
    super(props);

    this.state = {
      loading: false
    };

    this.handleSendInvite = this.handleSendInvite.bind(this);
  }

  handleSendInvite() {
    this.setState({
      loading: true
    });
    this.props.orgSendInvite({
      id: this.props.user.orgId,
      email: this.props.user.email,
      name: this.props.user.name,
      roleId: this.props.user.roleId,
    }).catch((err) => {
      this.setState({
        loading: false
      });

      displayError(err, message.error);
    }).then(() => {
      this.setState({
        loading: false
      });

      message.success("Invite has been sent to email");
    });
  }

  render() {
    return (
      <Button type="primary" onClick={this.handleSendInvite} loading={this.state.loading} disabled={this.state.loading}>
        Resend invite
      </Button>
    );
  }
}

export default ResendInvite;
