import React from 'react';
import PropTypes from 'prop-types';
import {Widgets} from 'components';

class Dashboard extends React.Component {

  static propTypes = {
    widgets: PropTypes.array
  };

  render() {

    if (!this.props.widgets)
      return null;

    let widgets = {
      lg: this.props.widgets.map((item) => ({
        i: String(item.id),
        id: String(item.id),
        w: item.width,
        h: item.height,
        x: item.x,
        y: item.y
      }))
    };

    return (
      <Widgets editable={false} data={widgets}/>
    );
  }

}

export default Dashboard;
