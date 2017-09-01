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

    let d3 = Plotlyjs.d3;

    let WIDTH_IN_PERCENT_OF_PARENT = 100,
      HEIGHT_IN_PERCENT_OF_PARENT = 100;

    let gd3 = d3.select(this.container)
      .style({
        width: WIDTH_IN_PERCENT_OF_PARENT + '%',
        'margin-left': (100 - WIDTH_IN_PERCENT_OF_PARENT) / 2 + '%',

        height: HEIGHT_IN_PERCENT_OF_PARENT + 'vh',
        'margin-top': (100 - HEIGHT_IN_PERCENT_OF_PARENT) / 2 + 'vh'
      });

    Plotlyjs.plot(gd3.node(), this.props.data || [], this.props.layout || {}, this.props.config || {});
  }

  // componentDidUpdate() {
  // }

  render() {
    return (
      <div id="fefefwefwe2323" ref={(node) => this.container = node}/>
    );
  }

}

export default Plotly;
