import React from 'react';
import PropTypes from 'prop-types';
import {Grids} from 'components';
import {WidgetStatic} from "components/Widgets";

class Dashboard extends React.Component {

  static propTypes = {
    webDashboard: PropTypes.object,
  };

  render() {

    if (!this.props.webDashboard || !this.props.webDashboard.widgets || !this.props.webDashboard.widgets.length)
      return (<div className="product-no-fields">No Dashboard widgets</div>);


    let widgets = this.props.webDashboard.widgets.map((widget) => (
      <WidgetStatic widget={widget} key={widget.id}/>
    ));

    return (
      <Grids.GridStatic widgets={widgets} webDashboard={this.props.webDashboard}/>
    );
  }

}

export default Dashboard;
