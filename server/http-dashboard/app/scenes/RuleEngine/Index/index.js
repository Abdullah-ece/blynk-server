import React from 'react';
import { MainList, MainLayout } from 'components';

class Index extends React.Component {

  render() {
    return (
      <MainLayout.Content className="organizations-empty">
        <MainList>
          <MainList.Empty title="Business analytics at your fingertips"/>
        </MainList>
      </MainLayout.Content>
    );
  }

}

export default Index;
