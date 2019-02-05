/* eslint-disable no-console */
import React, { Component } from 'react';
import NoCampaigns from '../NoCampaigns';
import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import {
  GetOrgShipments
} from "data/Product/actions";
import CampaignsList from "../CampaignsList";
import PropTypes from "prop-types";

@connect((state) => ({
  orgId: state.Account.selectedOrgId
}), (dispatch) => ({
  getOrgShipments: bindActionCreators(GetOrgShipments, dispatch),
}))
class Index extends Component {
  static propTypes = {
    getOrgShipments: PropTypes.func,
    orgId: this.props.orgId,
  };

  constructor(props) {
    super(props);

    this.state = {
      shipments: []
    };
  }

  componentWillMount() {
    this.props.getOrgShipments({ orgId: this.props.orgId }).then(shipments => {
      this.setState({ shipments: shipments.payload.data });
    }).catch(err => console.error(err));
  }

  render() {
    const { shipments } = this.state;
    if (shipments.length > 0) {
      return (
        <CampaignsList shipments={shipments}/>
      );
    } else {
      return (
        <NoCampaigns/>
      );
    }
  }
}

export default Index;
