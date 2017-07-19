import React      from 'react';
import {Link}     from 'react-router';
import {Button}   from 'antd';
import {
  MainList,
  MainLayout
}                 from 'components';

class List extends React.Component {

  render() {
    return (
      <MainLayout>
        <MainLayout.Header title="Organizations" options={(
          <div>
            <Link to="/products/create">
              <Button icon="plus" type="primary">Create New Organization</Button>
            </Link>
          </div>
        )}/>
        <MainLayout.Content>
          <MainList>
            <MainList.Item logoUrl="http://lorempixel.com/400/400?1"
                           name="Ecolab"
                           devicesCount={5032}
                           link="/products"
            />
            <MainList.Item logoUrl="http://lorempixel.com/400/400?2"
                           name="Hencel"
                           devicesCount={5032}
                           link="/products"
            />
            <MainList.Item logoUrl="http://lorempixel.com/400/400?3"
                           name="Kronenburg"
                           devicesCount={5032}
                           link="/products"
            />
          </MainList>
        </MainLayout.Content>
      </MainLayout>
    );
  }

}

export default List;
