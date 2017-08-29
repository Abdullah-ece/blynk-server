import React from 'react';
import PropTypes from 'prop-types';
import Plotlyjs from 'plotly';

class Plotly extends React.Component {

  static propTypes = {
    data: PropTypes.array,
    layout: PropTypes.object,
    config: PropTypes.object,
  };

  componentDidMount() {
    Plotlyjs.newPlot(this.container, this.props.data || [], this.props.layout || {}, this.props.config || {});
  }

  componentDidUpdate() {
    Plotlyjs.newPlot(this.container, this.props.data || [], this.props.layout || {}, this.props.config || {});
  }

  render() {
    return (
      <div ref={(node) => this.container = node}/>
    );
  }

}

export default Plotly;
