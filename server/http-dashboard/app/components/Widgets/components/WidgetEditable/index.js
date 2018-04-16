import React from 'react';

import {WIDGET_TYPES} from 'services/Widgets';

import PropTypes from 'prop-types';

import {
  LinearWidget,
  BarChartWidget,
  LabelWidget,
  SwitchWidget
} from 'components/Widgets';

import {
  Popconfirm,
  Button
} from 'antd';

import _ from 'lodash';

class WidgetEditable extends React.Component {

  static propTypes = {
    input: PropTypes.shape({
      value   : PropTypes.object,
      onChange: PropTypes.func
    }),

    loading: PropTypes.bool,

    history: PropTypes.object,

    style: PropTypes.object,

    children: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.element),
      PropTypes.element
    ]),

    deviceId: PropTypes.number,

    onWidgetDelete: PropTypes.func,
    onWidgetClone : PropTypes.func,
    onMouseUp     : PropTypes.func,
    onTouchEnd    : PropTypes.func,
    onMouseDown   : PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      isSettingsModalVisible: false
    };

    this.handleWidgetClone = this.handleWidgetClone.bind(this);
    this.handleWidgetDelete = this.handleWidgetDelete.bind(this);
    this.handleWidgetChange = this.handleWidgetChange.bind(this);
    this.toggleSettingsModalVisibility = this.toggleSettingsModalVisibility.bind(this);
  }

  shouldComponentUpdate(nextProps, nextState) {
    return (
      nextState.isSettingsModalVisible !== this.state.isSettingsModalVisible ||
      nextProps.deviceId !== this.props.deviceId ||
      !_.isEqual(nextProps.input.value, this.props.input.value) ||
      !_.isEqual(nextProps.loading, this.props.loading) ||
      !_.isEqual(nextProps.history, this.props.history) ||
      !_.isEqual(nextProps.style, this.props.style)
    );
  }

  handleWidgetChange(widgetValues) {
    this.props.input.onChange(widgetValues);

    this.toggleSettingsModalVisibility();
  }

  handleWidgetDelete() {
    const widget = this.props.input.value;

    this.props.onWidgetDelete(widget.id);
  }

  handleWidgetClone() {
    const widget = this.props.input.value;

    this.props.onWidgetClone(widget.id);
  }

  toggleSettingsModalVisibility() {
    this.setState({
      isSettingsModalVisible: !this.state.isSettingsModalVisible
    });
  }

  getSettingsElement() {

    const widget = this.props.input.value;

    if (widget.level && !widget.level.min && !widget.level.max) {
      widget.level.min = 0;
      widget.level.max = 100;
    }

    const attributes = {
      form         : `widget-settings-${widget.id}`,
      initialValues: widget,
      deviceId     : this.props.deviceId,
      onSubmit     : this.handleWidgetChange,
      onClose      : this.toggleSettingsModalVisibility,
      visible      : this.state.isSettingsModalVisible,
      history      : this.props.history,
      loading      : this.props.loading,
    };

    /*
    *
    * @TODO
    * 1) Put history/loading to Settings and Preview inside Settings
    * 2) Fix getting data by sourceIndex. Use PIN except sourceIndex
    * 3) Finish WidgetStatic
    * 4) Apply new Grid to Device Dashboard
    * */


    if (widget.type === WIDGET_TYPES.LINEAR)
      return (
        <LinearWidget.Settings {...attributes}/>
      );

    if (widget.type === WIDGET_TYPES.BAR)
      return (
        <BarChartWidget.Settings {...attributes}/>
      );

    if (widget.type === WIDGET_TYPES.LABEL)
      return (
        <LabelWidget.Settings {...attributes}/>
      );

    if (widget.type === WIDGET_TYPES.SWITCH)
      return (
        <SwitchWidget.Settings {...attributes}/>
      );

  }

  getToolsElements() {

    const preventDragNDrop = (e) => {
      e.preventDefault();
      e.stopPropagation();
    };

    return (
      <div className="widgets--widget-tools"
           onMouseDown={preventDragNDrop}
           onMouseUp={preventDragNDrop}>

        <Popconfirm title="Are you sure you want to delete this widget"
                    okText="Yes"
                    cancelText="No"
                    onConfirm={this.handleWidgetDelete}
                    overlayClassName="danger">
          <Button icon="delete" size="small"/>
        </Popconfirm>

        <Button icon="copy" size="small" onClick={this.handleWidgetClone}/>

        <Button icon="setting" size="small" onClick={this.toggleSettingsModalVisibility}/>

      </div>
    );
  }

  render() {
    const widget = this.props.input.value;

    const attributes = {
      key          : widget.id,
      deviceId     : this.props.deviceId,
      data         : widget,
      name         : widget.type + widget.id,
      resizeHandler: this.props.children, // resize handler from react-grid-layout
      value        : widget.type === WIDGET_TYPES.LINEAR ? [] : null,
      loading      : false,
    };

    attributes.parentElementProps = {
      id         : widget.type + widget.id,
      onMouseUp  : this.props.onMouseUp,
      onTouchEnd : this.props.onTouchEnd,
      onMouseDown: this.props.onMouseDown,
      style      : this.props.style,
    };

    attributes.settingsModal = this.getSettingsElement(); // modal for widget Settings

    attributes.tools =  this.getToolsElements(); // tools for Clone, Delete, Edit widget


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

export default WidgetEditable;
