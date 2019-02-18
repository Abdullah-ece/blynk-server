/* eslint-disable no-console */
import React, { Component } from 'react';
import NoCampaigns from '../NoCampaigns';
import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import {
  GetOrgShipments,
  ShipmentDelete,
} from "data/Product/actions";
import CampaignsList from "../CampaignsList";
import PropTypes from "prop-types";

@connect((state) => ({
  orgId: state.Account.selectedOrgId
}), (dispatch) => ({
  getOrgShipments: bindActionCreators(GetOrgShipments, dispatch),
  otaDelete: bindActionCreators(ShipmentDelete, dispatch),
}))
class Index extends Component {
  static propTypes = {
    getOrgShipments: PropTypes.func,
    orgId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    otaDelete: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      shipments: []
    };

    this.deleteOTA = this.deleteOTA.bind(this);
  }

  componentWillMount() {
    this.props.getOrgShipments({ orgId: this.props.orgId }).then(shipments => {
      this.setState({ shipments: shipments.payload.data });
    }).catch(err => console.error(err));
  }

  deleteOTA(id) {
    this.props.otaDelete({ shipmentId: id }).then(
      () => {
        this.props.getOrgShipments({ orgId: this.props.orgId }).then(
          shipments => {
            this.setState({ shipments: shipments.payload.data });
          });
      }).catch(err => console.error(err));
  }

  render() {
    const { shipments } = this.state;
    if (shipments.length > 0) {
      return (
        <CampaignsList shipments={shipments} otaDelete={this.deleteOTA}/>
      );
    } else {
      return (
        <NoCampaigns/>
      );
    }
  }
}

export default Index;
