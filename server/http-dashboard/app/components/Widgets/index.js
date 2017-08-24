import React from 'react';
// import {Button} from 'antd';
import {Responsive, WidthProvider} from 'react-grid-layout';
import './styles.less';
import _ from 'lodash';

const ResponsiveGridLayout = WidthProvider(Responsive);

class Widgets extends React.Component {

  static propTypes = {
    editable: React.PropTypes.bool
  };

  constructor(props) {
    super(props);

    // this.state = {
    //   layouts: {
    //     lg: [
    //       {i: '0', x: 0, y: 0, w: 7, h: 3},
    //       {i: '1', x: 7, y: 0, w: 5, h: 3},
    //       {i: '2', x: 0, y: 3, w: 2, h: 1},
    //       {i: '3', x: 2, y: 3, w: 4, h: 1},
    //       {i: '4', x: 6, y: 3, w: 5, h: 1},
    //       {i: '5', x: 11, y: 3, w: 1, h: 1},
    //       {i: '6', x: 0, y: 4, w: 5, h: 3},
    //       {i: '7', x: 5, y: 4, w: 7, h: 3},
    //     ],
    //     md: [
    //       {i: '0', x: 0, y: 0, w: 6, h: 3},
    //       {i: '1', x: 6, y: 0, w: 4, h: 3},
    //       {i: '2', x: 0, y: 3, w: 2, h: 1},
    //       {i: '3', x: 2, y: 3, w: 3, h: 1},
    //       {i: '4', x: 5, y: 3, w: 3, h: 1},
    //       {i: '5', x: 8, y: 3, w: 2, h: 1},
    //       {i: '6', x: 0, y: 4, w: 4, h: 3},
    //       {i: '7', x: 4, y: 4, w: 6, h: 3},
    //     ],
    //     sm: [
    //       {i: '0', x: 0, y: 0, w: 5, h: 3},
    //       {i: '1', x: 5, y: 0, w: 3, h: 3},
    //       {i: '2', x: 0, y: 3, w: 1, h: 1},
    //       {i: '3', x: 1, y: 3, w: 3, h: 1},
    //       {i: '4', x: 4, y: 3, w: 3, h: 1},
    //       {i: '5', x: 7, y: 3, w: 1, h: 1},
    //       {i: '6', x: 0, y: 4, w: 3, h: 3},
    //       {i: '7', x: 3, y: 4, w: 5, h: 3},
    //     ],
    //     xs: [
    //       {i: '0', x: 0, y: 0, w: 4, h: 2},
    //       {i: '1', x: 0, y: 2, w: 4, h: 2},
    //       {i: '2', x: 0, y: 4, w: 3, h: 1},
    //       {i: '3', x: 3, y: 4, w: 1, h: 1},
    //       {i: '4', x: 0, y: 5, w: 1, h: 1},
    //       {i: '5', x: 1, y: 5, w: 3, h: 1},
    //       {i: '6', x: 0, y: 6, w: 4, h: 2},
    //       {i: '7', x: 0, y: 8, w: 4, h: 2},
    //     ],
    //     xxs: [
    //       {i: '0', x: 0, y: 0, w: 7, h: 3},
    //       {i: '1', x: 7, y: 0, w: 5, h: 3},
    //       {i: '2', x: 0, y: 3, w: 2, h: 1},
    //       {i: '3', x: 1, y: 3, w: 3, h: 1},
    //       {i: '4', x: 3, y: 3, w: 4, h: 1},
    //       {i: '5', x: 6, y: 3, w: 3, h: 1},
    //       {i: '6', x: 0, y: 4, w: 3, h: 3},
    //       {i: '7', x: 3, y: 4, w: 5, h: 3},
    //     ]
    //   },
    //   currentBreakpoint: 'lg',
    // };

    this.state = {
      layouts: {
        lg: [
          {i: '0', x: 0, y: 0, w: 1, h: 1},
        ],
        md: [
          {i: '0', x: 0, y: 0, w: 1, h: 1},
        ],
        sm: [
          {i: '0', x: 0, y: 0, w: 1, h: 1},
        ],
        xs: [
          {i: '0', x: 0, y: 0, w: 1, h: 1},
        ],
        xxs: [
          {i: '0', x: 0, y: 0, w: 1, h: 1},
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

  // recountIndexesForLayout(layout = []) {
  //
  //   layout = [...layout];
  //
  //   let k = 0;
  //
  //   return layout.map((value) => {
  //     return {
  //       ...value,
  //       i: (k++).toString()
  //     }
  //   });
  // }

  // recountIndexesForLayouts(layouts = {}) {
  //   layouts = {...layouts};
  //
  //   _.forEach(layouts, (widgets, breakpoint) => {
  //     let k = 0;
  //     layouts[breakpoint] = this.recountIndexesForLayout(widgets);
  //   });
  //
  //   return layouts;
  // }

  // deleteWidget(breakpoint, i) {
  //
  //   const layouts = this.state.layouts[breakpoint].slice();
  //
  //   const key = _.findIndex(layouts, {i: i});
  //
  //   layouts.splice(key, 1);
  //
  //   this.state.layouts = {
  //     ...this.state.layouts,
  //     [breakpoint]: this.recountIndexesForLayout(layouts)
  //   };
  //
  //   this.setState({
  //     layouts: this.state.layouts
  //   });
  // }

  generateDOM() {

    return _.map(this.state.layouts[this.state.currentBreakpoint], (l, i) => {
      return (<div key={i} className="widget"/>);
    });
  }

  render() {
    return (
      <ResponsiveGridLayout
        margin={[8, 8]}
        cols={this.cols}
        className={`widgets ${this.props.editable ? 'editable' : null}`}
        layouts={this.state.layouts}
        isDraggable={this.props.editable}
        isResizable={this.props.editable}
        onBreakpointChange={this.onBreakpointChange.bind(this)}
        onLayoutChange={this.onLayoutChange.bind(this)}
        measureBeforeMount={true}
        verticalCompact={false}
        rowHeight={96}
        autoSize={false}
      >
        {this.generateDOM()}
      </ResponsiveGridLayout>
    );
  }

}

export default Widgets;
