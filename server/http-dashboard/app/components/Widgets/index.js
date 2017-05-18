import React from 'react';
import {Responsive, WidthProvider} from 'react-grid-layout';
import './styles.less';
import _ from 'lodash';

const ResponsiveGridLayout = WidthProvider(Responsive);

class Widgets extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      layouts: {
        lg: [
          {i: '0', x: 0, y: 0, w: 5, h: 3},
          {i: '1', x: 5, y: 0, w: 3, h: 3},
          {i: '2', x: 0, y: 3, w: 1, h: 1},
          {i: '3', x: 1, y: 3, w: 2, h: 1},
          {i: '4', x: 3, y: 3, w: 3, h: 1},
          {i: '5', x: 6, y: 3, w: 2, h: 1},
          {i: '6', x: 0, y: 4, w: 3, h: 3},
          {i: '7', x: 3, y: 4, w: 5, h: 3},
        ]
      },
      currentBreakpoint: 'lg',
    };
  }

  cols = {lg: 12, md: 10, sm: 8, xs: 4, xxs: 2};

  onBreakpointChange(breakpoint) {
    this.setState({
      currentBreakpoint: breakpoint
    });
  }

  onLayoutChange(layout, layouts) {
    this.setState({
      layouts: layouts
    });
  }

  generateDOM() {

    return _.map(this.state.layouts[this.state.currentBreakpoint], function (l, i) {
      return (<div key={i} className="widget"/>);
    });
  }

  render() {
    return (
      <ResponsiveGridLayout
        margin={[5, 5]}
        cols={this.cols}
        className="widgets"
        layouts={this.state.layouts}
        onBreakpointChange={this.onBreakpointChange.bind(this)}
        onLayoutChange={this.onLayoutChange.bind(this)}
        measureBeforeMount={true}
        rowHeight={96}
      >
        {this.generateDOM()}
      </ResponsiveGridLayout>
    );
  }

}

export default Widgets;
