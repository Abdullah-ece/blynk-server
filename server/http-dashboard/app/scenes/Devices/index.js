import React from 'react';
import PageLayout from 'components/PageLayout';
import {DevicesSearch} from './components';

class Devices extends React.Component {

  render() {
    return (
      <PageLayout>
        <PageLayout.Navigation>
          <DevicesSearch />
        </PageLayout.Navigation>
        <PageLayout.Content>
          Content
        </PageLayout.Content>
      </PageLayout>
    );
  }

}

export default Devices;
