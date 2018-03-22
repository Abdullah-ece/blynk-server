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
    this.handleLayoutChange   = this.handleLayoutChange.bind(this);
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

  handleLayoutChange(layout) {

    layout.forEach((newItem) => {
      let originalItem = _.find(this.props.webDashboard.widgets, (item) => Number(item.input.value.id) === Number(newItem.i));

      let x1 = Number(originalItem.input.value.x);
      let y1 = Number(originalItem.input.value.y);
      let w1 = Number(originalItem.input.value.w);
      let h1 = Number(originalItem.input.value.h);

      let x2 = Number(newItem.x);
      let y2 = Number(newItem.y);
      let w2 = Number(newItem.w);
      let h2 = Number(newItem.h);

      if(x1 !== x2 || y1 !== y2 || w1 !== w2 || h1 !== h2) {
        originalItem.input.onChange({
          ...originalItem.input.value,
          x: x2,
          y: y2,
          width: w2,
          height: h2,
        });
      }
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
          onDragStop={this.handleLayoutChange}
          onResizeStop = {this.handleLayoutChange}
          layouts={layouts}
        >
          {this.props.widgets}
        </ResponsiveGridLayout>
      </div>
    );
  }

}

export default GridManage;
