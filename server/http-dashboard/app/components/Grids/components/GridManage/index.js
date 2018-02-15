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
import {WIDGETS_CONFIGS} from 'services/Widgets';
import Scroll from 'react-scroll';

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
    this.handleResize   = this.handleResize.bind(this);
    this.handleDragStop = this.handleDragStop.bind(this);
  }

  componentDidUpdate(prevProps) {

    const difference = _.differenceBy(this.props.widgets, prevProps.widgets, "key");

    if(difference.length !== 0){
      const newWidget = _.find(this.props.webDashboard.widgets, (item) => {
          return Number(item.input.value.id) === Number(difference[0].key);
        }).input.value;

      this.scrollToWidget(newWidget);
    }
  }

  scrollToWidget(newWidget) {
    //Getting all necessary widget position information

    const newWidgetName = newWidget.type + newWidget.id;

    const chartDefaultPadding = 15;

    const docScrollTop = (window.pageYOffset !== undefined) ? window.pageYOffset : (document.documentElement || document.body.parentNode || document.body).scrollTop;
    const docViewBottom = window.innerHeight + docScrollTop;

    const chartHeight = document.getElementById(newWidgetName).clientHeight;

    const rect = document.getElementById(newWidgetName).getBoundingClientRect(), bodyElt = document.body;

    const chartOffset = {
      top: rect.top + bodyElt .scrollTop,
      left: rect.left + bodyElt .scrollLeft
    };

    const chartBottom = chartOffset.top + chartHeight + chartDefaultPadding;

    const visible = (chartBottom <= docViewBottom) && (chartOffset.top >= docScrollTop);

    if (!visible) {
      const scroll = Scroll.animateScroll;
      scroll.scrollTo( chartOffset.top+chartHeight - window.innerHeight + chartDefaultPadding + docScrollTop);
    }
  }

  cols = {lg: 12, md: 10, sm: 8, xs: 4, xxs: 2};

  handleResize(layout, oldItem, newItem) {

      const item = _.find(this.props.webDashboard.widgets, (item) => {
        return Number(item.input.value.id) === Number(newItem.i);
      });

      item.input.onChange({
        ...item.input.value,
        w: newItem.w,
        h: newItem.h,
        width: newItem.w,
        height: newItem.h,
      });
  }

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
          ...WIDGETS_CONFIGS[widget.type],
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
          onResizeStop = {this.handleResize}
          layouts={layouts}
        >
          {this.props.widgets}
        </ResponsiveGridLayout>
      </div>
    );
  }

}

export default GridManage;
