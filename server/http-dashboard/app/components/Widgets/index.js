import React from 'react';
import {
  Widget,
} from './components';
import {Responsive, WidthProvider} from 'react-grid-layout';
import './styles.less';
import _ from 'lodash';
import PropTypes from 'prop-types';
import Scroll from 'react-scroll';
import {Field, Fields} from 'redux-form';

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

    deviceId: PropTypes.number,

    onWidgetDelete: PropTypes.func,
    onWidgetClone: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      currentBreakpoint: 'lg',
    };

    this.widget = this.widget.bind(this);
    this.handleWidgetClone = this.handleWidgetClone.bind(this);
    this.onBreakpointChange = this.onBreakpointChange.bind(this);
    this.handleWidgetDelete = this.handleWidgetDelete.bind(this);
    this.responsiveGridLayout = this.responsiveGridLayout.bind(this);
  }

  // TEMPORARILY comment this to prevent error

  componentDidUpdate(prevProps) {
    const difference = _.differenceBy(this.props.data[this.state.currentBreakpoint],
                                      prevProps.data[this.state.currentBreakpoint],
                                      "id");

    if(difference.length !== 0){
      const newWidget = difference[0];

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

  onBreakpointChange(breakpoint) {
    this.setState({
      currentBreakpoint: breakpoint
    });
  }

  handleWidgetDelete(id) {
    this.props.onWidgetDelete(id);
  }

  handleWidgetClone(id) {
    this.props.onWidgetClone(id, this.state.currentBreakpoint);
  }

  widget(props) {

    const onChange = (widget) => {
      props.input.onChange(widget);
    };

    const widget = props.input.value;

    return (
      <Widget id={Number(widget.id)}
              key={widget.id}
              style={props.style}
              onMouseDown={props.onMouseDown}
              onMouseUp={props.onMouseUp}
              onTouchStart={props.onTouchStart}
              onTouchEnd={props.onTouchEnd}
              fetchRealData={this.props.fetchRealData}
              deviceId={this.props.deviceId}
              data={widget}
              editable={this.props.editable}
              onWidgetChange={onChange}
              onWidgetDelete={this.handleWidgetDelete}
              onWidgetClone={this.handleWidgetClone}
              isPreviewOnly={this.props.isPreviewOnly}
      />
    );
  }

  generateDOM() {

    return _.map(this.props.data[this.state.currentBreakpoint], (widget) => {
      return (
        <Field name={`${widget.fieldName}`} key={widget.id} component={this.widget}/>
      );
    });
  }

  responsiveGridLayout(props) {
    const onDragStop = (layout, oldItem, newItem) => {

      const item = _.find(props.webDashboard.widgets, (item) => {
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
    };

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

        onDragStop={onDragStop}
        onBreakpointChange={this.onBreakpointChange}

        autoSize={true}
        measureBeforeMount={true}
      >
        {this.generateDOM()}
      </ResponsiveGridLayout>
    );
  }

  render() {

    const names = _.map(this.props.data[this.state.currentBreakpoint], (widget) => {
      return (widget.fieldName);
    });

    return (
      <Fields names={names} component={this.responsiveGridLayout} deviceId={this.props.deviceId} />
    );
  }

}

export WidgetEditable from './components/WidgetEditable';
export LinearWidget from './components/LinearWidget';
export BarChartWidget from './components/BarChartWidget';
export LabelWidget from './components/LabelWidget';
export SwitchWidget from './components/SwitchWidget';

export default Widgets;
