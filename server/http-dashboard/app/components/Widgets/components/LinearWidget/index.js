import React from 'react';
// import {
//   Plotly
// } from 'components';
// import Widget from '../Widget';
import PropTypes from 'prop-types';
import LinearWidgetSettings from './settings';
import './styles.less';

class LinearWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,

    editable: PropTypes.bool,

    onWidgetDelete: PropTypes.func,
  };

  render() {

    // const data = [];
    // const config = [];
    // const layout = [];

    return (
      <div className="grid-linear-widget">
        Linear chart is comming there
        {/*<Plotly />*/}
      </div>
    );
  }

}

LinearWidget.Settings = LinearWidgetSettings;

export default LinearWidget;
