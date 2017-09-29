import React from 'react';
import PropTypes from 'prop-types';
import Plotlyjs from 'plotly';
import './styles.less';

class Plotly extends React.Component {

  static propTypes = {
    data: PropTypes.array,
    layout: PropTypes.object,
    config: PropTypes.object,
    handleHover: PropTypes.func,
    handleUnHover: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleHover = this.handleHover.bind(this);
    this.handleUnHover = this.handleUnHover.bind(this);
  }

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

    this.container.on('plotly_hover', this.handleHover).on('plotly_unhover', this.handleUnHover);
  }

  componentDidUpdate() {
    Plotlyjs.newPlot(this.gd3.node(), this.props.data || [], this.props.layout || {}, this.props.config || {});
    this.container.on('plotly_hover', this.handleHover).on('plotly_unhover', this.handleUnHover);
  }

  handleHover(data) {

    if(typeof this.props.handleHover === 'function')
      this.props.handleHover(data, this.container, {
        restyle: Plotlyjs.restyle,
        animate: Plotlyjs.animate,
      });
  }

  handleUnHover(data) {
    if(typeof this.props.handleUnHover === 'function')
      this.props.handleUnHover(data, this.container, {
        restyle: Plotlyjs.restyle,
        animate: Plotlyjs.animate,
      });
  }

  render() {
    return (
      <div id="div" ref={(node) => this.container = node}/>
    );
  }

}

export default Plotly;
