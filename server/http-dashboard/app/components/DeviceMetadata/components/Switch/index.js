import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import SwitchModal from './modal';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {reset, initialize, getFormValues, getFormSyncErrors} from 'redux-form';

@connect((state, ownProps) => ({
  values: getFormValues(ownProps.form)(state),
  errors: getFormSyncErrors(ownProps.form)(state)
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch),
  initialize: bindActionCreators(initialize, dispatch)
}))
class Switch extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        {Number(field.get('value')) === 0 ? field.get('from') : field.get('to')}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <SwitchModal form={this.props.form} initialValues={this.props.data.toJS()}/>
      </div>
    );
  }

}

export default Switch;
