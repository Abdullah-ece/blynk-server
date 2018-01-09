import React from 'react';

import {fromJS} from 'immutable';

import {
  SimpleContentEditable
} from 'components';

import {reduxForm, getFormValues, Field} from 'redux-form';

import WidgetSettings from '../WidgetSettings';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
// import {bindActionCreators} from 'redux';

@connect((state, ownProps) => ({
  formValues: fromJS(getFormValues(ownProps.form)(state) || {}),
  dataStreams: fromJS(state.Product.edit.dataStreams.fields || []),
}), (/*dispatch*/) => ({
  // changeForm: bindActionCreators(change, dispatch),
  // resetForm: bindActionCreators(reset, dispatch),
  // initializeForm: bindActionCreators(initialize, dispatch),
  // destroyForm: bindActionCreators(destroy, dispatch),
}))
@reduxForm()
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

  labelNameComponent({input}) {
    return (
      <SimpleContentEditable maxLength={35}
                             className="modal-window-widget-settings-config-widget-name"
                             value={input.value}
                             onChange={input.onChange}/>
    );
  }

  render() {

    return (
      <WidgetSettings
        visible={this.props.visible}
        onSave={this.handleSave}
        onCancel={this.handleCancel}
        config={(
          <div className="modal-window-widget-settings-config-column-header">
            <Field name="label" component={this.labelNameComponent}/>

            <div className="modal-window-widget-settings-config-add-source">
              {/*<Button type="dashed" onClick={this.handleAddSource}>Add source</Button>*/}
            </div>
          </div>
        )}
      />
    );
  }

}

export default SwitchSettings;
