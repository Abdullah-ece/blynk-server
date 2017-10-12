import React from 'react';

import PropTypes from 'prop-types';

import BarChartSettings from './settings';

class BarChartWidget extends React.Component {

  static propTypes = {
    fetchRealData: PropTypes.bool
  };

  renderFakeData() {
    return (
      <div>The chart is fake</div>
    );
  }

  renderRealData() {
    return (
      <div>The chart is real</div>
    );
  }

  render() {

    if(!this.props.fetchRealData)
      return this.renderFakeData();

    return this.renderRealData();

  }

}

BarChartWidget.Settings = BarChartSettings;

export default BarChartWidget;
