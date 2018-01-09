import React from 'react';

import WidgetSettings from '../WidgetSettings';
import PropTypes from 'prop-types';

class SwitchSettings extends React.Component {

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
      <WidgetSettings
        visible={this.props.visible}
        onSave={this.handleSave}
        onCancel={this.handleCancel}
        config={(
          <div>Switch Settings</div>
        )}
      />
    );
  }

}

export default SwitchSettings;
