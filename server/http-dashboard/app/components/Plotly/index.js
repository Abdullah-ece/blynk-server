import React from 'react';
import PropTypes from 'prop-types';

class Plotly extends React.Component {

  static propTypes = {
    data: PropTypes.array,
    layout: PropTypes.object,
    config: PropTypes.object
  };

  render() {
    return (
      <div ref={(node) => this.container = node}/>
    );
  }

}

export default Plotly;
