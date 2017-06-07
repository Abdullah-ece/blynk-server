import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import TimeModal from './modal';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {Time as TimeService} from 'services/Metadata';
import {reset, getFormValues, getFormSyncErrors} from 'redux-form';

@connect((state, ownProps) => ({
  values: getFormValues(ownProps.form)(state),
  errors: getFormSyncErrors(ownProps.form)(state)
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch)
}))
class Time extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        {TimeService.fromTimestamp(field.get('time'))}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <TimeModal form={this.props.form} initialValues={this.props.data.toJS()}/>
      </div>
    );
  }

}

export default Time;
