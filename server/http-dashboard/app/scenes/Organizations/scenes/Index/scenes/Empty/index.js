import React          from 'react';
import {MainList, MainLayout}     from 'components';
import './styles.less';

class Empty extends React.Component {

  render() {
    return (
      <MainLayout.Content className="organizations-empty">
        <MainList>
          <MainList.Empty title="Manage organizations easily"
                          description="Start with adding a new Organization that will get access to the Products and devices they own."
                          link="/organizations/create"
                          btnText="Create New Organization"/>
        </MainList>
      </MainLayout.Content>
    );
  }

}

export default Empty;
