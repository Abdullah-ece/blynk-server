import React from 'react';
import { MainList, MainLayout } from 'components';

class NoCampaigns extends React.Component {

  render() {
    return (
      <MainLayout.Content className="products-empty">
        <MainList>
          <MainList.Empty title="Ship new firmware updates Over-The-Air"
                          description="Here you can remotely update millions of your devices with new firmware and track shipment progress"
                          link="/ota/create"
                          btnText="New Shipping"/>
        </MainList>
      </MainLayout.Content>
    );
  }

}

export default NoCampaigns;
