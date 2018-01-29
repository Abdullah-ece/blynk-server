import React from 'react';
// import PropTypes from 'prop-types';
// import {
//   List,
// } from 'immutable';
// import {
//   AddWidgetTools,
//   Grid
// } from './components';
import './styles.less';

class Dashboard extends React.Component {

  static propTypes = {

  };

  constructor(props) {
    super(props);

    // this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
    // this.handleWidgetsChange = this.handleWidgetsChange.bind(this);
  }

  // handleWidgetAdd(widget) {
  //   this.props.onWidgetAdd(widget);
  // }
  //
  // handleWidgetsChange(widgets) {
  //   this.props.onWidgetsChange(widgets);
  // }

  render() {

    // const widgets = this.props.widgets;

    return (
      <div className="products-manage-dashboard">

        {/*<AddWidgetTools onWidgetAdd={this.handleWidgetAdd}*/}
        {/*/>*/}

        {/*<Grid widgets={widgets}*/}
              {/*params={this.props.params}*/}
              {/*onChange={this.handleWidgetsChange}*/}
        {/*/>*/}

      </div>
    );
  }

}

export default Dashboard;
