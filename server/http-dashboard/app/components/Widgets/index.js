import React from 'react';
import {
  Widget
} from './components';
import {Responsive, WidthProvider} from 'react-grid-layout';
import './styles.less';
import _ from 'lodash';
import PropTypes from 'prop-types';
import Scroll from 'react-scroll';

const ResponsiveGridLayout = WidthProvider(Responsive);

class Widgets extends React.Component {

  static breakpoints = {
    lg: 1200,
    md: 996,
    sm: 768,
    xs: 480,
    xxs: 0
  };

  static propTypes = {
    fetchRealData: PropTypes.bool,
    editable: PropTypes.bool,
    isPreviewOnly: PropTypes.bool,

    data: PropTypes.object,
    breakpoints: PropTypes.object,

    params: PropTypes.shape({
      id: PropTypes.number.isRequired
    }).isRequired,

    onChange: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      currentBreakpoint: 'lg',
    };

    this.onLayoutChange = this.onLayoutChange.bind(this);
    this.handleWidgetClone = this.handleWidgetClone.bind(this);
    this.onBreakpointChange = this.onBreakpointChange.bind(this);
    this.handleWidgetChange = this.handleWidgetChange.bind(this);
    this.handleWidgetDelete = this.handleWidgetDelete.bind(this);
  }

  componentDidUpdate(prevProps) {
    const difference = _.differenceBy(this.props.data[this.state.currentBreakpoint],
                                      prevProps.data[this.state.currentBreakpoint],
                                      "name");
    if(difference.length !== 0){
      const newWidget = difference[0];

      this.scrollToWidget(newWidget);
    }
  }

  scrollToWidget(newWidget) {
    //Getting all necessary widget position information
    const chartDefaultPadding = 15;

    const docScrollTop = (window.pageYOffset !== undefined) ? window.pageYOffset : (document.documentElement || document.body.parentNode || document.body).scrollTop;
    const docViewBottom = window.innerHeight + docScrollTop;

    const chartHeight = document.getElementById(newWidget.name).clientHeight;

    const rect = document.getElementById(newWidget.name).getBoundingClientRect(), bodyElt = document.body;

    const chartOffset = {
      top: rect.top + bodyElt .scrollTop,
      left: rect.left + bodyElt .scrollLeft
    };

    const chartBottom = chartOffset.top + chartHeight + chartDefaultPadding;
    // ----

    const visible = (chartBottom <= docViewBottom) && (chartOffset.top >= docScrollTop);

    if (!visible) {
      const scroll = Scroll.animateScroll;
      scroll.scrollTo( chartOffset.top+chartHeight - window.innerHeight + chartDefaultPadding + docScrollTop);
    }
  }

  cols = {lg: 12, md: 10, sm: 8, xs: 4, xxs: 2};

  onBreakpointChange(breakpoint) {
    this.setState({
      currentBreakpoint: breakpoint
    });
  }

  onLayoutChange(layout) {
    if (this.props.onChange)
      this.props.onChange(layout);
  }

  handleWidgetChange(widget) {
    this.props.onChange(
      this.props.data.lg.map((w) => {
        if (Number(w.id) === Number(widget.id))
          return widget;
        return w;
      })
    );
  }

  handleWidgetClone(widget) {

    const widgets = [...this.props.data.lg];

    const clonedWidgetId = _.random(1, 999999999);

    widget.y = widgets.reduce((acc, item) => {
      return Number(item.y) > acc ? Number(item.y) : acc;
    }, 0) + 1;

    widgets.push({
      ...widget,
      id: clonedWidgetId,
      label: `${widget.label} Copy`,
      name: widget.type + clonedWidgetId,
      x: 0
    });

    this.props.onChange(
      widgets
    );
  }

  handleWidgetDelete(id) {

    if (this.props.onChange)
      this.props.onChange(
        this.props.data.lg.filter((widget) => Number(widget.id) !== Number(id))
      );
  }

  generateDOM() {

    return _.map(this.props.data[this.state.currentBreakpoint], (widget) => {
      return (
        <Widget id={Number(widget.id)}
                key={widget.id}
                fetchRealData={this.props.fetchRealData}
                params={this.props.params}
                data={widget}
                editable={this.props.editable}
                onWidgetChange={this.handleWidgetChange}
                onWidgetDelete={this.handleWidgetDelete}
                onWidgetClone={this.handleWidgetClone}
                isPreviewOnly={this.props.isPreviewOnly}
        />
      );
    });
  }

  render() {
    return (
      <ResponsiveGridLayout
        breakpoints={this.props.breakpoints || Widgets.breakpoints}
        margin={[8, 8]}
        rowHeight={96}
        // ref={element => this.myElement = element}
        cols={this.cols}

        className={`widgets ${this.props.editable ? 'widgets-editable' : null}`}

        layouts={this.props.data}

        isDraggable={this.props.editable}
        isResizable={this.props.editable}

        onLayoutChange={this.onLayoutChange}
        onBreakpointChange={this.onBreakpointChange}

        autoSize={true}
        measureBeforeMount={true}
      >
        {this.generateDOM()}
      </ResponsiveGridLayout>
    );
  }

}

export default Widgets;
