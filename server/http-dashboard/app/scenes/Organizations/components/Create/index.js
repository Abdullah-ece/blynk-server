import React          from 'react';
import {MainLayout}   from 'components';
import Manage         from './../Manage';
import {Button}       from 'antd';
import PropTypes      from 'prop-types';
import {List}         from 'immutable';

import './styles.less';

class Create extends React.Component {

  static propTypes = {
    activeTab: PropTypes.string,
    products: PropTypes.instanceOf(List),

    onTabChange: PropTypes.func,
    handleCancel: PropTypes.func,
    handleSubmit: PropTypes.func,
  };

  render() {
    return (
      <MainLayout>
        <MainLayout.Header title="New Organization"
                           options={(
                             <div>
                               <Button type="default"
                                 // onClick={this.props.handleCancel.bind(this)}>
                                       onClick={() => {
                                       }}>
                                 Cancel
                               </Button>
                               <Button type="primary"
                                 // onClick={this.props.handleSubmit.bind(this)}>
                                       onClick={() => {
                                       }}>
                                 Create
                               </Button>
                             </div>
                           )}/>
        {/*@todo rename product- to layout*/}
        <MainLayout.Content className="product-create-content">
          <Manage
            onTabChange={this.props.onTabChange}
            activeTab={this.props.activeTab}
            products={this.props.products}/>
        </MainLayout.Content>
      </MainLayout>
    );
  }

}

export default Create;
