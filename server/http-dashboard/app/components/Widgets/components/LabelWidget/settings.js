import React from 'react';

import WidgetSettings from '../WidgetSettings';
import PropTypes from 'prop-types';
import {reduxForm, getFormValues, reset, initialize, destroy, change, Field} from 'redux-form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {fromJS} from 'immutable';
import {
  SimpleContentEditable
} from 'components';

@connect((state, ownProps) => ({
  formValues: fromJS(getFormValues(ownProps.form)(state) || {}),
  dataStreams: fromJS(state.Product.edit.dataStreams.fields || []),
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch),
  resetForm: bindActionCreators(reset, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  destroyForm: bindActionCreators(destroy, dispatch),
}))
@reduxForm()
class LabelWidgetSettings extends React.Component {

  static propTypes = {
    visible: PropTypes.bool,

    onClose: PropTypes.func,
    resetForm: PropTypes.func,
    handleSubmit: PropTypes.func,

    form: PropTypes.string,
  };

  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
  }

  labelNameComponent({input}) {
    return (
      <SimpleContentEditable maxLength={35}
                             className="modal-window-widget-settings-config-widget-name"
                             value={input.value} o
                             onChange={input.onChange}/>
    );
  }

  handleCancel() {
    if (typeof this.props.onClose === 'function')
      this.props.onClose();

    this.props.resetForm(this.props.form);
  }

  handleSave() {
    if (typeof this.props.handleSubmit === 'function')
      this.props.handleSubmit();
  }

  render() {
    return (
      <WidgetSettings
        onSave={this.handleSave}
        onCancel={this.handleCancel}
        visible={this.props.visible}
        preview={(
          <div>Label Widget Preview</div>
        )}
        config={(
          <div>
            <div className="modal-window-widget-settings-config-column-header">
              <Field name="label" component={this.labelNameComponent}/>

              <div className="modal-window-widget-settings-config-add-source">
                {/*<Button type="dashed" onClick={this.handleAddSource}>Add source</Button>*/}
              </div>

            </div>
          </div>
        )}/>
    );
  }

}

export default LabelWidgetSettings;
