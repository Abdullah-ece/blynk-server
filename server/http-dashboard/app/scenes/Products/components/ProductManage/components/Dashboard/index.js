import React from 'react';
import PropTypes from 'prop-types';
import {
  List,
} from 'immutable';
import {
  AddWidgetTools,
  Grid
} from './components';
import './styles.less';

class Dashboard extends React.Component {

  static propTypes = {
    onWidgetAdd: PropTypes.func,
    onWidgetsChange: PropTypes.func,

    widgets: PropTypes.instanceOf(List)
  };

  constructor(props) {
    super(props);

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
    this.handleWidgetsChange = this.handleWidgetsChange.bind(this);
  }

  handleWidgetAdd(widget) {
    this.props.onWidgetAdd(widget);
  }

  handleWidgetsChange(widgets) {
    this.props.onWidgetsChange(widgets);
  }

  render() {

    const widgets = this.props.widgets;

    return (
      <div className="products-manage-dashboard">

        <AddWidgetTools onWidgetAdd={this.handleWidgetAdd}
        />

        <Grid widgets={widgets}
              onChange={this.handleWidgetsChange}
        />

      </div>
    );
  }

}

export default Dashboard;
