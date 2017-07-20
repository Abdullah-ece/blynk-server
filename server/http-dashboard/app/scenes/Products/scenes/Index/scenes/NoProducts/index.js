import React  from 'react';
import {
  MainList,
  MainLayout
}             from 'components';
import './styles.less';

export default class NoProducts extends React.Component {
  render() {
    return (
      <MainLayout.Content className="products-empty">
        <MainList>
          <MainList.Empty title="Start by creating your first product"
                          description="Product is a digital model of a physical object. It is used in Blynk platform as a template to be assigned to devices."
                          link="/products/create"
                          btnText="Create New Product"/>
        </MainList>
      </MainLayout.Content>
    );
  }
}
