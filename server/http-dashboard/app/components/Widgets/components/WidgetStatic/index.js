import React from 'react';

import {WIDGET_TYPES} from 'services/Widgets';

import PropTypes from 'prop-types';

import {
  LinearWidget,
  BarChartWidget,
  LabelWidget,
  SwitchWidget
} from 'components/Widgets';

import {Label as LabelDataWrapper} from 'scenes/Devices/scenes/WidgetDataWrapper/index';

import _ from 'lodash';

class WidgetStatic extends React.Component {

  static propTypes = {

    loading: PropTypes.bool,
    isLive: PropTypes.bool,

    widget : PropTypes.object,
    history: PropTypes.oneOfType([
      PropTypes.array,
      PropTypes.object
    ]),
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

    let pin = null;

    if(widget && widget.sources && widget.sources[0] && widget.sources[0].dataStream && widget.sources[0].dataStream.pin) {
      pin = widget.sources[0].dataStream.pin;
    }

    const dataWrapperAttributes = {
      type    : widget.type,
      isLive  : this.props.isLive,
      deviceId: this.props.deviceId,
      widgetId: widget.id,
      pin     : pin
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
        <LabelDataWrapper {...dataWrapperAttributes}>
          <LabelWidget {...attributes}/>
        </LabelDataWrapper>
      );

    if (widget.type === WIDGET_TYPES.SWITCH)
      return (
        <SwitchWidget {...attributes}/>
      );

  }

}

export default WidgetStatic;
