import React, { Component } from 'react';
import { Button } from "antd";
import { Table } from "antd";

class CampaignsList extends Component {
  static contextTypes = {
    router: React.PropTypes.object
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
