import React from 'react';
import {
  Widgets
} from 'components';
import PropTypes from 'prop-types';
import {Responsive, WidthProvider} from 'react-grid-layout';
const ResponsiveGridLayout = WidthProvider(Responsive);

class GridStatic extends React.Component {

  static propTypes = {
    deviceId: PropTypes.number,

    webDashboard: PropTypes.object,

    widgets: PropTypes.arrayOf(PropTypes.element),
  };


  constructor(props) {
    super(props);
  }

  cols = {lg: 12, md: 10, sm: 8, xs: 4, xxs: 2};

  render() {

    let widgets = this.props.webDashboard && this.props.webDashboard.widgets || [];

    const layouts = {
      lg: widgets.map((widget) => {

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
      <div className="product-details-dashboard-grid">
        <ResponsiveGridLayout
          breakpoints={Widgets.breakpoints}
          margin={[8, 6]}
          rowHeight={96}
          cols={this.cols}
          className={`widgets`}
          isDraggable={false}
          isResizable={false}
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

export default GridStatic;
