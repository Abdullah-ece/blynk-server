import React from 'react';

import {WIDGET_TYPES} from 'services/Widgets';

import PropTypes from 'prop-types';

import {
  LinearWidget,
  BarChartWidget,
  LabelWidget,
  SwitchWidget
} from 'components/Widgets';

import _ from 'lodash';

class WidgetStatic extends React.Component {

  static propTypes = {

    loading: PropTypes.bool,

    widget : PropTypes.object,
    history: PropTypes.object,
    style  : PropTypes.object,


    children: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.element),
      PropTypes.element
    ]),

    deviceId: PropTypes.number,

    onWriteToVirtualPin: PropTypes.func,
  };

  constructor(props) {
    super(props);
  }

  shouldComponentUpdate(nextProps) {
    return (
      !_.isEqual(nextProps.widget, this.props.widget) ||
      !_.isEqual(nextProps.loading, this.props.loading) ||
      !_.isEqual(nextProps.history, this.props.history) ||
      !_.isEqual(nextProps.style, this.props.style) ||
      !_.isEqual(nextProps.deviceId, this.props.deviceId)
    );
  }

  render() {
    const widget = this.props.widget;

    const attributes = {
      key                : widget.id,
      deviceId           : this.props.deviceId,
      data               : widget,
      name               : widget.type + widget.id,
      history            : this.props.history,
      loading            : this.props.loading,
      onWriteToVirtualPin: this.props.onWriteToVirtualPin,
    };

    attributes.parentElementProps = {
      id         : widget.type + widget.id,
      style      : this.props.style,
    };

    if (widget.type === WIDGET_TYPES.LINEAR)
      return (
        <LinearWidget {...attributes}/>
      );

    if (widget.type === WIDGET_TYPES.BAR)
      return (
        <BarChartWidget {...attributes}/>
      );

    if (widget.type === WIDGET_TYPES.LABEL)
      return (
        <LabelWidget {...attributes}/>
      );

    if (widget.type === WIDGET_TYPES.SWITCH)
      return (
        <SwitchWidget {...attributes}/>
      );

  }

}

export default WidgetStatic;
