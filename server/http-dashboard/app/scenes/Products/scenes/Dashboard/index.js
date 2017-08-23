import React from 'react';
import {fromJS} from 'immutable';
import {
  Dashboard
} from '../../components/ProductManage/components';

class DashboardScene extends React.Component {

  constructor(props) {
    super(props);

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
  }

  handleWidgetAdd() {

  }

  render() {

    const widgets = fromJS([]);

    return (
      <Dashboard onWidgetAdd={this.handleWidgetAdd} widgets={widgets}/>
    );
  }

}

export default DashboardScene;
