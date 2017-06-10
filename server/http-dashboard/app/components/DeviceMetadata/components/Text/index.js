import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import TextModal from './modal';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {reset, getFormValues, getFormSyncErrors} from 'redux-form';

@connect((state, ownProps) => ({
  values: getFormValues(ownProps.form)(state),
  errors: getFormSyncErrors(ownProps.form)(state)
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch)
}))
class Text extends Base {

  constructor(props) {
    super(props);
  }

  isDeviceOwner() {
    const DEVICE_OWNER = 'Device Owner';
    return this.props.data.get('name') === DEVICE_OWNER;
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        {field.get('value') || <i>No Value</i>}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <TextModal form={this.props.form} initialValues={this.props.data.toJS()} isDeviceOwner={this.isDeviceOwner()}/>
      </div>
    );
  }

}

export default Text;
