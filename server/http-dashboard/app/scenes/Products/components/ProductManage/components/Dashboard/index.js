import React      from 'react';
import PropTypes  from 'prop-types';
import {List}     from 'immutable';
import {
  AddWidgetTools,
  Grid
}                 from './components';
import './styles.less';

class Dashboard extends React.Component {

  static propTypes = {
    onWidgetAdd: PropTypes.func,

    widgets: PropTypes.instanceOf(List)
  };

  constructor(props) {
    super(props);

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
  }

  handleWidgetAdd() {
    this.props.onWidgetAdd();
  }

  render() {
    return (
      <div className="products-manage-dashboard">
        <AddWidgetTools onWidgetAdd={this.handleWidgetAdd}/>
        <Grid widgets={this.props.widgets}/>
      </div>
    );
  }

}

export default Dashboard;
