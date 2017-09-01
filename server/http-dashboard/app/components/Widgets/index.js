import React from 'react';
import {
  Widget
} from './components';
import {Responsive, WidthProvider} from 'react-grid-layout';
import './styles.less';
import _ from 'lodash';

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
    fetchRealData: React.PropTypes.bool,
    editable: React.PropTypes.bool,

    data: React.PropTypes.object,
    breakpoints: React.PropTypes.object,
    params: React.PropTypes.object,

    onChange: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      currentBreakpoint: 'lg',
    };

    this.onLayoutChange = this.onLayoutChange.bind(this);
    this.onBreakpointChange = this.onBreakpointChange.bind(this);
    this.handleWidgetChange = this.handleWidgetChange.bind(this);
    this.handleWidgetDelete = this.handleWidgetDelete.bind(this);
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
