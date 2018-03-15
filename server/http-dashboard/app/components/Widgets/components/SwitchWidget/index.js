import React from 'react';

import SwitchSettings from './settings';
import PropTypes from 'prop-types';
import {Switch as AntdSwitch, message} from 'antd';
import {WIDGETS_SWITCH_ALIGNMENT} from "services/Widgets";
import './styles.less';
import Dotdotdot from 'react-dotdotdot';
import {WIDGETS_SWITCH_LABEL_ALIGNMENT} from "services/Widgets/index";

class Switch extends React.Component {

  static propTypes = {
    visible: PropTypes.bool,

    data  : PropTypes.object,
    params: PropTypes.object,

    deviceId: PropTypes.number,

    value: PropTypes.string,

    parentElementProps: PropTypes.shape({
      id         : PropTypes.string,
      onMouseUp  : PropTypes.func,
      onTouchEnd : PropTypes.func,
      onMouseDown: PropTypes.func,
      style      : PropTypes.object,
    }),

    tools        : PropTypes.element,
    settingsModal: PropTypes.element,
    resizeHandler: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.element),
      PropTypes.element,
    ]),

    onWriteToVirtualPin: PropTypes.func

  };

  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
  }

  getAlignmentClassName(alignment) {
    if(alignment === WIDGETS_SWITCH_ALIGNMENT.LEFT)
      return `widgets--widget-switch--alignment-left`;

    if(alignment === WIDGETS_SWITCH_ALIGNMENT.CENTER)
      return `widgets--widget-switch--alignment-center`;

  }

  getLabelAlignmentClassName(alignment) {
    if(alignment === WIDGETS_SWITCH_LABEL_ALIGNMENT.LEFT)
      return `widgets--widget-switch--label-alignment-left`;

    if(alignment === WIDGETS_SWITCH_LABEL_ALIGNMENT.RIGHT)
      return `widgets--widget-switch--label-alignment-right`;

  }

  getSwitchColorByStatus(status, color) {
    if(status)
      return `#${color}`;

    if(!status)
      return `#BFBFBF`;
  }

  getLabelByStatus(status, data) {
    if(status)
      return data.onLabel;

    if(!status)
      return data.offLabel;
  }

  getSwitchPositionClassName(isNameHidden) {
    if(isNameHidden)
      return `widgets--widget-switch--centered`;
    return null;
  }

  getValue() {
    return this.props.value;
  }

  handleChange(value) {

    if(!this.props.onWriteToVirtualPin)
      return false;

    if(this.props.data.offValue === undefined || this.props.data.onValue === undefined) {
      return message.warning("Please set On/Off value for Switch before use it");
    }

    if (this.props.data.sources && this.props.data.sources.length && this.props.data.sources[0].dataStream) {

      const pin = this.props.data.sources[0].dataStream.pin;

      this.props.onWriteToVirtualPin({
        pin  : pin,
        value: value ? this.props.data.onValue : this.props.data.offValue
      });

    }
  }

  renderSwitch() {

    const checked = (() => {

      if (this.props.data.sources && this.props.data.sources.length && this.props.data.sources[0].dataStream) {
        const value = this.getValue();

        if(this.props.data.onValue === undefined) return false; // fix enabled status on Dashboard Edit

        return String(value).toLowerCase() === String(this.props.data.onValue).toLowerCase();
      }

      return false;

    })();

    const alignmentClassName = this.getAlignmentClassName(this.props.data.alignment);

    const labelAlignmentClassName = this.getLabelAlignmentClassName(this.props.data.labelPosition);

    const switchPositionClassName = this.getSwitchPositionClassName(this.props.data.isWidgetNameHidden);

    const label = this.getLabelByStatus(checked, this.props.data);

    const color = this.getSwitchColorByStatus(checked, this.props.data.color);

    const {isSwitchLabelsEnabled} = this.props.data;

    return (
      <div className={`widgets--widget-switch ${alignmentClassName} ${switchPositionClassName} ${isSwitchLabelsEnabled && labelAlignmentClassName || ''}`}>
        <div className={`widgets--widget-switch-wrapper`}>
          <AntdSwitch style={{'backgroundColor': color, 'borderColor': color}} checked={checked} onChange={this.handleChange}/>

          { isSwitchLabelsEnabled && (
            <span className={`widgets--widget-switch--label`}>
              <Dotdotdot clamp={1}>{ label }</Dotdotdot>
            </span>
          )}
        </div>
      </div>
    );
  }

  render() {
    return (
      <div {...this.props.parentElementProps} className={`widgets--widget`}>
        {this.props.tools}

        {(!this.props.data.isWidgetNameHidden || (this.props.data.isWidgetNameHidden && !this.props.data.isSwitchLabelsEnabled)) && (
          <div className="widgets--widget-label">
            <Dotdotdot clamp={1}>{this.props.data.label || 'No Widget Name'}</Dotdotdot>
          </div>
        )}
        { /* widget content */ }

        { this.renderSwitch() }

        { /* end widget content */ }

        {this.props.settingsModal}
        {this.props.resizeHandler}
      </div>
    );
  }

}

Switch.Settings = SwitchSettings;

export default Switch;
