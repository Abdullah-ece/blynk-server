import React from 'react';
import {
  Button,
  Popconfirm,
} from 'antd';
import Dotdotdot from 'react-dotdotdot';
import PropTypes from 'prop-types';
import classnames from 'classnames';
import {
  WIDGET_TYPES
} from 'services/Widgets';
import {
  LinearWidget,
  BarChartWidget,
  LabelWidget
} from 'components/Widgets/components';
import './styles.less';
class Widget extends React.Component {

  static propTypes = {
    children: PropTypes.oneOfType([
      PropTypes.element,
      PropTypes.array,
    ]),
    style: PropTypes.object,
    data: PropTypes.object,
    className: PropTypes.string,

    id: PropTypes.number,

    editable: PropTypes.bool,
    isPreviewOnly: PropTypes.bool,
    fetchRealData: PropTypes.bool,

    onWidgetClone: PropTypes.func,
    onWidgetChange: PropTypes.func,
    onMouseUp: PropTypes.func,
    onTouchEnd: PropTypes.func,
    onMouseDown: PropTypes.func,
    onWidgetDelete: PropTypes.func,

    params: PropTypes.shape({
      // id: PropTypes.number.isRequired
    }).isRequired,
  };

  constructor(props) {
    super(props);

    this.handleSaveChanges = this.handleSaveChanges.bind(this);
    this.handleWidgetClone = this.handleWidgetClone.bind(this);
    this.handleWidgetDelete = this.handleWidgetDelete.bind(this);
    this.toggleSettingsVisibility = this.toggleSettingsVisibility.bind(this);

    this.state = {
      isConfigVisible: false
    };
  }

  getWidgetByType(type, widget) {

    const attributes = {
      key            :widget.id,
      fetchRealData  :this.props.fetchRealData,
      params         :this.props.params,
      data           :widget,
      name           :widget.type + widget.id,
      editable       :this.props.editable,
      previewMode    :this.state.isConfigVisible,
      onWidgetDelete :this.handleWidgetDelete
    };

    if (type === WIDGET_TYPES.LINEAR)
      return (
        <LinearWidget {...attributes}/>
      );

    if (type === WIDGET_TYPES.BAR)
      return (
        <BarChartWidget {...attributes}/>
      );

    if (type === WIDGET_TYPES.LABEL)
      return (
        <LabelWidget {...attributes}/>
      );

  }

  handleSaveChanges(values) {
    this.toggleSettingsVisibility();

    this.props.onWidgetChange(values);
  }

  toggleSettingsVisibility() {
    this.setState({
      isConfigVisible: !this.state.isConfigVisible
    });
  }

  getWidgetSettingsByType(type, widget) {
    if (type === WIDGET_TYPES.LINEAR)
      return (
        <LinearWidget.Settings visible={this.state.isConfigVisible}
                               initialValues={widget}
                               form={`widget-settings-${widget.id}`}
                               onClose={this.toggleSettingsVisibility}
                               onSubmit={this.handleSaveChanges}/>
      );

    if (type === WIDGET_TYPES.BAR)
      return (
        <BarChartWidget.Settings visible={this.state.isConfigVisible}
                                 initialValues={widget}
                                 params={this.props.params}
                                 form={`widget-settings-${widget.id}`}
                                 onClose={this.toggleSettingsVisibility}
                                 onSubmit={this.handleSaveChanges}/>
      );

    if (type === WIDGET_TYPES.LABEL)
      return (
        <LabelWidget.Settings    visible={this.state.isConfigVisible}
                                 initialValues={widget}
                                 params={this.props.params}
                                 form={`widget-settings-${widget.id}`}
                                 onClose={this.toggleSettingsVisibility}
                                 onSubmit={this.handleSaveChanges}/>
      );
  }

  handleWidgetClone() {
    this.props.onWidgetClone(this.props.data);
  }

  handleWidgetDelete() {
    this.props.onWidgetDelete(this.props.id);
  }

  preventDragNDrop(e) {
    e.preventDefault();
    e.stopPropagation();
  }

  render() {

    const className = classnames({
      'widgets--widget': true,
      [this.props.className]: true
    });

    let styles = {
      ...(this.props.style || {}),
      'backgroundColor': '#'+this.props.data.backgroundColor,
      'color': '#'+this.props.data.textColor
    };

    return (
      <div className={className}
           onMouseDown={this.props.onMouseDown}
           onMouseUp={this.props.onMouseUp}
           onTouchEnd={this.props.onTouchEnd}
           style={styles}
      >
        <div className="widgets--widget-label">
          <Dotdotdot clamp={1}>{this.props.data.label || 'No Widget Name'}</Dotdotdot>
          {this.props.editable && (
            <div className="widgets--widget-tools" onMouseDown={this.preventDragNDrop}
                 onMouseUp={this.preventDragNDrop}>

              <Popconfirm title="Are you sure you want to delete this widget"
                          okText="Yes"
                          cancelText="No"
                          onConfirm={this.handleWidgetDelete}
                          overlayClassName="danger">
                <Button icon="delete" size="small"/>
              </Popconfirm>

              <Button icon="copy" size="small" onClick={this.handleWidgetClone}/>

              <Button icon="setting" size="small" onClick={this.toggleSettingsVisibility}/>

            </div>
          )}
        </div>
        {this.getWidgetByType(this.props.data.type, this.props.data)}
        {!this.props.isPreviewOnly && this.getWidgetSettingsByType(this.props.data.type, this.props.data) || null}
        {this.props.children}
      </div>
    );
  }
}

export default Widget;
