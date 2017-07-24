import React                from 'react';
import {Link}               from 'react-router';
import {Button, message}    from 'antd';
import {List as IList}      from 'immutable';
import PropTypes            from 'prop-types';
import {
  MainList,
  MainLayout
}                           from 'components';

class List extends React.Component {

  static contextTypes = {
    router: PropTypes.object
  };

  static propTypes = {
    data: PropTypes.instanceOf(IList),

    location: PropTypes.object
  };

  componentDidMount() {
    if (this.props.location.query && this.props.location.query.success) {
      this.context.router.push('/organizations');
      message.success('Organization created successfully');
    }
  }

  getDevicesCountByProductsList(products) {
    if (products instanceof IList) {
      return products.reduce((count, product) => count + product.get('deviceCount'), 0);
    }
    return 0;
  }

  render() {
    return (
      <MainLayout>
        <MainLayout.Header title="Organizations" options={(
          <div>
            <Link to="/organizations/create">
              <Button icon="plus" type="primary">Create New Organization</Button>
            </Link>
          </div>
        )}/>
        <MainLayout.Content>
          <MainList>
            { this.props.data.map((organization, key) => (
              <MainList.Item key={key}
                             logoUrl={organization.get('logoUrl')}
                             name={organization.get('name')}
                             isActive={Boolean(organization.get('isActive'))}
                             noImageText="No Organization Image"
                             devicesCount={
                               this.getDevicesCountByProductsList(organization.get('products'))
                             }
                             link={`/organizations/${organization.get('id')}`}
              />
            ))}
          </MainList>
        </MainLayout.Content>
      </MainLayout>
    );
  }

}

export default List;
