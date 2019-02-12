/* eslint-disable no-console */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Button, Table, Menu, Dropdown, Badge } from "antd";
import { MainLayout, LinearIcon } from "components";
import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import {
  OTAStart,
  OTAStop,
  GetOrgShipments,
} from "data/Product/actions";

@connect((state) => ({
  orgId: state.Account.selectedOrgId
}), (dispatch) => ({
  otaStart: bindActionCreators(OTAStart, dispatch),
  otaStop: bindActionCreators(OTAStop, dispatch),
  getOrgShipments: bindActionCreators(GetOrgShipments, dispatch),
}))
class CampaignsList extends Component {
  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    shipments: PropTypes.array,
    otaDelete: PropTypes.func,
    otaStart: PropTypes.func,
    otaStop: PropTypes.func,
    getOrgShipments: PropTypes.func,
    orgId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  };

  constructor(props) {
    super(props);

    this.buildColumns = this.buildColumns.bind(this);
    this.buildDataSource = this.buildDataSource.bind(this);
    this.resumeOTA = this.resumeOTA.bind(this);
    this.pauseOTA = this.pauseOTA.bind(this);
    this.deleteOTA = this.deleteOTA.bind(this);
    this.cancelOTA = this.cancelOTA.bind(this);
    this.buildBadge = this.buildBadge.bind(this);

    this.state = {
      shipments: props.shipments
    };
  }

  cancelOTA(id) {
    console.log(id);
  }

  resumeOTA(shipment) {
    this.props.otaStart({ otaDTO: shipment }).then(
      () => {
        this.props.getOrgShipments({ orgId: this.props.orgId }).then(
          shipments => {
            this.setState({ shipments: shipments.payload.data });
          });
      }).catch(err => console.error(err));
  }

  pauseOTA(shipment) {
    this.props.otaStop({ otaDTO: shipment }).then(
      () => {
        this.props.getOrgShipments({ orgId: this.props.orgId }).then(
          shipments => {
            this.setState({ shipments: shipments.payload.data });
          });
      }).catch(err => console.error(err));
  }

  deleteOTA(id) {
    this.props.otaDelete(id);
  }

  buildDataSource() {
    return this.state.shipments.map(shipment => {
      return {
        ...shipment,
        shipmentAndFirmware: {
          title: shipment.title,
          firmwareOriginalFileName: shipment.firmwareOriginalFileName
        },
        options: {
          status: shipment.status,
          shipment
        }
      };
    });
  }

  buildBadge(value) {
    let text = value;
    let status = 'success';

    if (value === 'PAUSE') {
      status = 'warning';
    } else if (value === 'FAILED', value === 'CANCELLED') {
      status = 'error';
    } else if (value === 'DRAFT') {
      status = 'default';
    }

    if (value === 'PAUSE') {
      text = 'Paused';
    } else if (value === 'RUN') {
      text = 'Live';
    } else if (value === 'FAILED') {
      text = 'Failed';
    } else if (value === 'DRAFT') {
      text = 'Draft';
    } else if (value === 'CANCEL') {
      text = 'Cancelled';
    } else if (value === 'FINISH') {
      text = 'Finished';
    }

    return (<Badge status={status} text={text}/>);
  }

  buildColumns() {
    const columns = [{
      title: "Shipment",
      dataIndex: "shipmentAndFirmware",
      render: (value) => {
        return (<div>
          <div className="ota-shipment-table-name">{value.title}</div>
          <div
            className="ota-shipment-table-name-details">{value.firmwareOriginalFileName}</div>
        </div>);
      }
    }, {
      title: "Status",
      dataIndex: "status",
      render: (value) => {
        return this.buildBadge(value);
      }
    }/*, {
      title: "Progress",
      dataIndex: "hardwareInfo.version",
    }*/, {
      title: "Target Size",
      dataIndex: "deviceIds.length",
    }, {
      title: "Firmware Version",
      dataIndex: "firmwareInfo.version",
    }, {
      title: "Started Date",
      dataIndex: "startedAt",
    }, {
      title: "",
      dataIndex: "options",
      render: (value) => {
        const menu = (
          <Menu>
            {/*<Menu.Item disabled>*/}
            {/*<span onClick={() => this.cancelOTA(value.shipment.id)}>*/}
            {/*<LinearIcon type="cancel" className="ota-shipment-table-icon"/> Cancel*/}
            {/*</span>*/}
            {/*</Menu.Item>*/}
            {value.status === 'RUN' && (<Menu.Item>
              <span onClick={() => this.pauseOTA(value.shipment)}>
                <LinearIcon type="pause" className="ota-shipment-table-icon"/> Pause
              </span>
            </Menu.Item>)}
            {value.status === 'PAUSE' && (<Menu.Item>
              <span onClick={() => this.resumeOTA(value.shipment)}>
                <LinearIcon type="play" className="ota-shipment-table-icon"/> Resume
              </span>
            </Menu.Item>)}
            <Menu.Item>
               <span onClick={() => this.deleteOTA(value.shipment.id)}>
                <LinearIcon type="trash" className="ota-shipment-table-icon"/> Delete
               </span>
            </Menu.Item>
          </Menu>
        );

        return (
          <Dropdown overlay={menu}>
            <div className="ota-shipment-table-options">...</div>
          </Dropdown>
        );
      }
    },];

    return columns;
  }

  render() {
    return (
      <MainLayout>
        <MainLayout.Header
          title={'Blynk.Air'}
          options={(
            <div>
              <Button type="primary"
                      onClick={() => this.context.router.push('/ota/create')}>
                New Shipping
              </Button>
            </div>
          )}/>
        <MainLayout.Content className="organizations-create-content">
          <div>
            <a href="#">All Shippings ({this.state.shipments.length})</a>
          </div>
          <div className="ota-shipments-table">
            <Table columns={this.buildColumns()}
                   dataSource={this.buildDataSource()}
                   pagination={false}/>
          </div>
        </MainLayout.Content>
      </MainLayout>
    );
  }
}

export default CampaignsList;
