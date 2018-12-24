import React from 'react';
import { Link } from 'react-router';
import { Button } from 'antd';
import { connect } from 'react-redux';
import {
  MainList,
  MainLayout
} from 'components';

import './styles.less';
import { VerifyPermission, PERMISSIONS_INDEX } from "services/Roles";

@connect((state) => ({
  permissions: state.RolesAndPermissions.currentRole.permissionGroup1
}))
export default class ProductsList extends React.Component {

  static propTypes = {
    products: React.PropTypes.array,
    permissions: React.PropTypes.number,
  };

  render() {
    return (
      <MainLayout>
        <MainLayout.Header title="Products"
                           options={VerifyPermission(this.props.permissions, PERMISSIONS_INDEX.PRODUCT_CREATE) && (
                             <div>
                               <Link to="/products/create">
                                 <Button icon="plus" type="primary">New
                                   Product</Button>
                               </Link>
                             </div>
                           ) || null}/>
        <MainLayout.Content>
          <MainList>
            {this.props.products.map((product, key) => (
              <MainList.Item key={key}
                             logoUrl={product.logoUrl}
                             noImageText="No Product Image"
                             name={product.name}
                             devicesCount={product.deviceCount}
                             link={`/product/${product.id}`}/>
            ))}
          </MainList>
        </MainLayout.Content>
      </MainLayout>
    );
  }
}
