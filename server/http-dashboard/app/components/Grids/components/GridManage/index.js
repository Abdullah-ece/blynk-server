import React from 'react';
import {
  Widgets
} from 'components';
import PropTypes from 'prop-types';
import _ from 'lodash';
// import {
//   fromJS
// } from 'immutable';
import {Responsive, WidthProvider} from 'react-grid-layout';
const ResponsiveGridLayout = WidthProvider(Responsive);

class GridManage extends React.Component {

  static propTypes = {
    webDashboard: PropTypes.object,

    deviceId: PropTypes.number,

    onWidgetDelete: PropTypes.func,
    onWidgetClone: PropTypes.func,

    widgets: PropTypes.arrayOf(PropTypes.element),

  };


  constructor(props) {
    super(props);
    this.handleDragStop = this.handleDragStop.bind(this);
  }

  cols = {lg: 12, md: 10, sm: 8, xs: 4, xxs: 2};

  handleDragStop(layout, oldItem, newItem) {

    const item = _.find(this.props.webDashboard.widgets, (item) => {
      return Number(item.input.value.id) === Number(newItem.i);
    });

    item.input.onChange({
      ...item.input.value,
      x: newItem.x,
      y: newItem.y,
      w: newItem.w,
      h: newItem.h,
      width: newItem.w,
      height: newItem.h,
    });

  }

  render() {

    let widgets = this.props.webDashboard && this.props.webDashboard.widgets || [];

    const layouts = {
      lg: widgets.map((item) => {
        const widget = item.input.value;

        return {
          i   : String(widget.id),
          w   : widget.width,
          h   : widget.height,
          x   : widget.x,
          y   : widget.y,
          minW: widget.minW,
          minH: widget.minH,
          maxW: widget.maxW,
          maxH: widget.maxH,
        };

      })
    };

    return (
      <div className="product-manage-dashboard-grid">
        <ResponsiveGridLayout
          breakpoints={Widgets.breakpoints}
          margin={[8, 8]}
          rowHeight={96}
          cols={this.cols}
          className={`widgets widgets-editable`}
          isDraggable={true}
          isResizable={true}
          autoSize={true}
          measureBeforeMount={true}
          onDragStop={this.handleDragStop}
          layouts={layouts}
        >
          {this.props.widgets}
        </ResponsiveGridLayout>
      </div>
    );
  }

}

export default GridManage;
