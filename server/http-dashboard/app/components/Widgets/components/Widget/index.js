import React from 'react';
import {
  Button,
  Tooltip,
} from 'antd';
import Dotdotdot from 'react-dotdotdot';
import PropTypes from 'prop-types';
import classnames from 'classnames';
import {
  WIDGET_TYPES
} from 'services/Widgets';
import {
  LinearWidget
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

    onMouseUp: PropTypes.func,
    onTouchEnd: PropTypes.func,
    onMouseDown: PropTypes.func,
    onWidgetDelete: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleWidgetDelete = this.handleWidgetDelete.bind(this);
  }

  getWidgetByType(type, widget) {
    if (type === WIDGET_TYPES.LINEAR)
      return (
        <LinearWidget key={widget.id}
                      data={widget}
                      editable={this.props.editable}
                      onWidgetDelete={this.handleWidgetDelete}/>
      );

  }

  getWidgetSettingsByType(type, widget) {
    if (type === WIDGET_TYPES.LINEAR)
      return (
        <LinearWidget.Settings data={widget} form="widget-settings"/>
      );
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
          <Dotdotdot clamp={1}>{this.props.data.title}</Dotdotdot>
          {this.props.editable && (
            <div className="widgets--widget-tools" onMouseDown={this.preventDragNDrop}
                 onMouseUp={this.preventDragNDrop}>

              <Button icon="delete" size="small" onClick={this.handleWidgetDelete}/>

              <Tooltip placement="top" title={'This feature is not avail right now'}>
                <Button icon="copy" size="small" disabled={true}/>
              </Tooltip>

              <Tooltip placement="top" title={'This feature is not avail right now'}>
                <Button icon="setting" size="small" disabled={true}/>
              </Tooltip>

            </div>
          )}
        </div>
        {this.getWidgetByType(this.props.data.type, this.props.data)}
        {this.getWidgetSettingsByType(this.props.data.type, this.props.data)}
        {this.props.children}
      </div>
    );
  }

}

export default Widget;
