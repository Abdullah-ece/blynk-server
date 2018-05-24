import React from 'react';
import {
  Widgets
} from 'components';
import PropTypes from 'prop-types';
import _ from 'lodash';
import ReactDOM from "react-dom";
// import {
//   fromJS
// } from 'immutable';
import {Responsive} from 'react-grid-layout';
import sizeMeProvider from 'react-sizeme';
import {WIDGETS_CONFIGS} from 'services/Widgets';
import ResizeDetector from 'react-resize-detector';
import Scroll from 'react-scroll';

// const ResponsiveGridLayout = WidthProvider(Responsive);
const ResponsiveGridLayout = sizeMeProvider()((props) => {
  return (
    <Responsive {...props} {...props.size}/>
  );
});

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

    this.state = {
      width: 0,
      height: 0,
    };

    this.handleResize = this.handleResize.bind(this);
    this.handleLayoutChange = this.handleLayoutChange.bind(this);
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

  handleResize() {
    let grid = ReactDOM.findDOMNode(this.gridRef);

    if(this.state && this.state.gridRefFound && grid) {

      if(grid && (grid.offsetWidth !== this.state.gridWidth || grid.offsetHeight !== this.state.gridHeight)) {
        this.setState({
          gridWidth: grid.offsetWidth,
          gridHeight: grid.offsetHeight,
        });
      }
    }

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

    const handleRef = (ref) => {
      this.gridRef = ref;

      let grid = ReactDOM.findDOMNode(this.gridRef);

      if((!this.state || !this.state.gridRefFound) && grid)
        this.setState({
          gridRefFound: true
        });

      if(this.state && this.state.gridRefFound && grid) {

        if(grid && (grid.offsetWidth !== this.state.gridWidth || grid.offsetHeight !== this.state.gridHeight)) {
          this.setState({
            gridWidth: grid.offsetWidth,
            gridHeight: grid.offsetHeight,
          });
        }
      }
    };

    const styles = {};

    if(this.state && this.state.gridWidth && this.state.gridHeight) {

      const LEFT_MARGIN = 8;
      const TOP_MARGIN = 8;
      const ROW_HEIGHT = 64;
      const COLUMNS = 12;

      const calcColWidth = (margin, containerWidth, size = 12) => {
        return (containerWidth - margin * (size - 1) - margin * 2) / size;
      };

      const calcColPosition = (x, y) => {

        const colWidth = calcColWidth(LEFT_MARGIN, this.state.gridWidth);

        return {
          left: Math.round((colWidth + LEFT_MARGIN) * x + LEFT_MARGIN),
          top : Math.round((ROW_HEIGHT + LEFT_MARGIN) * y + LEFT_MARGIN),
        };
      };

      const rows = Math.floor((this.state.gridHeight / (ROW_HEIGHT + TOP_MARGIN*2)));

      const WIDTH = calcColWidth(LEFT_MARGIN, this.state.gridWidth);
      const HEIGHT = ROW_HEIGHT;

      styles.width = WIDTH;
      styles.height = HEIGHT;

      styles.left = -WIDTH;
      styles.top = -HEIGHT;

      let boxShadow = [];

      for(let y = 0; y < rows; y++) {
        for(let x = 0; x < COLUMNS; x++) {
          let position = calcColPosition(x, y);
          boxShadow.push(`${position.left+WIDTH}px ${position.top+HEIGHT}px 0px #fcfcfc`);
        }
      }
      styles.boxShadow = boxShadow.join(',');
    }

    return (
      <div className="product-manage-dashboard-grid" ref={(ref) => handleRef(ref)}>
        <div className="product-manage-dashboard-grid-shadow" style={styles}/>
        <ResponsiveGridLayout
          breakpoints={Widgets.breakpoints}
          margin={[8, 8]}
          rowHeight={64}
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
        <ResizeDetector onResize={this.handleResize} handleWidth skipOnMount/>
      </div>
    );
  }

}

export default GridManage;
