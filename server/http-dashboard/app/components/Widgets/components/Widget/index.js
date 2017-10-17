import React from 'react';
import {
  Button,
} from 'antd';
import Dotdotdot from 'react-dotdotdot';
import PropTypes from 'prop-types';
import classnames from 'classnames';
import {
  WIDGET_TYPES
} from 'services/Widgets';
import {
  LinearWidget,
  BarChartWidget
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
      id: PropTypes.number.isRequired
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
    if (type === WIDGET_TYPES.LINEAR)
      return (
        <LinearWidget key={widget.id}
                      fetchRealData={this.props.fetchRealData}
                      params={this.props.params}
                      data={widget}
                      editable={this.props.editable}
                      onWidgetDelete={this.handleWidgetDelete}/>
      );

    if (type === WIDGET_TYPES.BAR)
      return (
        <BarChartWidget key={widget.id}
                   fetchRealData={this.props.fetchRealData}
                   params={this.props.params}
                   data={widget}
                   editable={this.props.editable}
                   onWidgetDelete={this.handleWidgetDelete}/>
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

    return (
      <div className={className}
           onMouseDown={this.props.onMouseDown}
           onMouseUp={this.props.onMouseUp}
           onTouchEnd={this.props.onTouchEnd}
           style={this.props.style}
      >
        <div className="widgets--widget-label">
          <Dotdotdot clamp={1}>{this.props.data.label}</Dotdotdot>
          {this.props.editable && (
            <div className="widgets--widget-tools" onMouseDown={this.preventDragNDrop}
                 onMouseUp={this.preventDragNDrop}>

              <Button icon="delete" size="small" onClick={this.handleWidgetDelete}/>

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
