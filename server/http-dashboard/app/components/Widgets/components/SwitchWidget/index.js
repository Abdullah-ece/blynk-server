import React from 'react';

import SwitchSettings from './settings';
import PropTypes from 'prop-types';
import {Switch as AntdSwitch} from 'antd';
import {WIDGETS_SWITCH_ALIGNMENT} from "services/Widgets";
import './styles.less';

class Switch extends React.Component {

  static propTypes = {
    visible: PropTypes.bool,

    data: PropTypes.object,
    params: PropTypes.object,

    onClose: PropTypes.func,
    resetForm: PropTypes.func,
    changeForm: PropTypes.func,
    destroyForm: PropTypes.func,
    handleSubmit: PropTypes.func,
    initializeForm: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
  }

  handleCancel() {
    if (typeof this.props.onClose === 'function')
      this.props.onClose();

    // this.props.resetForm(this.props.form);
  }

  handleSave() {
    if(typeof this.props.handleSubmit === 'function')
      this.props.handleSubmit();
  }

  getAlignmentClassName(alignment) {
    if(alignment === WIDGETS_SWITCH_ALIGNMENT.LEFT)
      return `widgets--widget-switch--alignment-left`;

    if(alignment === WIDGETS_SWITCH_ALIGNMENT.CENTER)
      return `widgets--widget-switch--alignment-center`;

  }

  render() {

    const alignmentClassName = this.getAlignmentClassName(this.props.data.alignment);

    return (
      <div className={`widgets--widget-switch ${alignmentClassName}`}>
        <div className={`widgets--widget-switch-wrapper`}>
          <AntdSwitch />
          {/* <span className={`widgets--widget-switch--label`}></span> */}
        </div>
      </div>
    );
  }

}

Switch.Settings = SwitchSettings;

export default Switch;
