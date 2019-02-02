import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Button, Table } from "antd";
import { MainLayout } from "components";

class CampaignsList extends Component {
  static contextTypes = {
    router: React.PropTypes.object
  };

  propTypes = {
    shippings: PropTypes.array,
  };

  constructor(props) {
    super(props);
  }

  render() {
    return (
      <MainLayout>
        <MainLayout.Header
          title={'All Shippings'}
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
            <a href="#">All Shippings ({this.props.shippings.length})</a>
          </div>
          <div>
            <Table/>
          </div>
        </MainLayout.Content>
      </MainLayout>
    );
  }
}

export default CampaignsList;
