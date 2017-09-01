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

    this.d3 = Plotlyjs.d3;

    let WIDTH_IN_PERCENT_OF_PARENT = 100,
      HEIGHT_IN_PERCENT_OF_PARENT = 100;

    this.gd3 = this.d3.select(this.container)
      .style({
        width: WIDTH_IN_PERCENT_OF_PARENT + '%',
        'margin-left': (100 - WIDTH_IN_PERCENT_OF_PARENT) / 2 + '%',

        height: HEIGHT_IN_PERCENT_OF_PARENT + 'vh',
        'margin-top': (100 - HEIGHT_IN_PERCENT_OF_PARENT) / 2 + 'vh'
      });

    Plotlyjs.newPlot(this.gd3.node(), this.props.data || [], this.props.layout || {}, this.props.config || {});
  }

  componentDidUpdate() {

    Plotlyjs.newPlot(this.gd3.node(), this.props.data || [], this.props.layout || {}, this.props.config || {});

  }

  render() {
    return (
      <div id="fefefwefwe2323" ref={(node) => this.container = node}/>
    );
  }

}

export default Plotly;
