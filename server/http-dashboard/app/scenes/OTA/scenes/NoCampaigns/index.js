import React from 'react';
import { MainList, MainLayout } from 'components';

class NoCampaigns extends React.Component {

  render() {
    return (
      <MainLayout.Content className="organizations-empty">
        <MainList>
          <MainList.Empty title="Ship new firmware updates Over-The-Air"/>
        </MainList>
      </MainLayout.Content>
    );
  }

}

export default NoCampaigns;
