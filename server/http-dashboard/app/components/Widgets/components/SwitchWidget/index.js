import React from 'react';

import SwitchSettings from './settings';
import PropTypes from 'prop-types';

class Switch extends React.Component {

  static propTypes = {
    visible: PropTypes.bool,

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

  render() {

    return (
      <div>
        Switch is there
      </div>
    );
  }

}

Switch.Settings = SwitchSettings;

export default Switch;
